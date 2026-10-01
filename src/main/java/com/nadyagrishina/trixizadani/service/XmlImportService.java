package com.nadyagrishina.trixizadani.service;

import com.nadyagrishina.trixizadani.model.CastObce;
import com.nadyagrishina.trixizadani.model.Obec;
import com.nadyagrishina.trixizadani.repository.CastObceRepository;
import com.nadyagrishina.trixizadani.repository.ObecRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class XmlImportService {

    private final ObecRepository obecRepository;
    private final CastObceRepository castObceRepository;

    public XmlImportService(ObecRepository obecRepository, CastObceRepository castObceRepository) {
        this.obecRepository = obecRepository;
        this.castObceRepository = castObceRepository;
    }

    @Transactional
    public void importData(String urlString) {
        try {
            URL url = URI.create(urlString).toURL();

            try (InputStream inputStream = url.openStream();
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

                if (zipInputStream.getNextEntry() != null) {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    XMLStreamReader reader = factory.createXMLStreamReader(zipInputStream);

                    Obec currentObec = null;
                    CastObce currentCastObce = null;
                    String currentTag = "";

                    Map<Long, Obec> obecCache = new HashMap<>();

                    while (reader.hasNext()) {
                        int event = reader.next();

                        if (event == XMLStreamConstants.START_ELEMENT) {
                            currentTag = getCleanTagName(reader);

                            if ("Obec".equalsIgnoreCase(currentTag) && currentCastObce == null) {
                                currentObec = new Obec();
                            } else if ("CastObce".equalsIgnoreCase(currentTag)) {
                                currentCastObce = new CastObce();
                            }
                        } else if (event == XMLStreamConstants.CHARACTERS) {
                            String text = reader.getText().trim();

                            if (!text.isEmpty()) {
                                if (currentObec != null && currentCastObce == null) {
                                    if ("Kod".equalsIgnoreCase(currentTag)) {
                                        currentObec.setKod(Long.parseLong(text));
                                    } else if ("Nazev".equalsIgnoreCase(currentTag)) {
                                        currentObec.setNazev(text);
                                    }
                                } else if (currentCastObce != null) {
                                    if ("Kod".equalsIgnoreCase(currentTag)) {
                                        if (currentCastObce.getKod() == 0) {
                                            currentCastObce.setKod(Long.parseLong(text));
                                        } else {
                                            long obecKod = Long.parseLong(text);
                                            Obec parentObec = obecCache.get(obecKod);
                                            if (parentObec != null) {
                                                currentCastObce.setObec(parentObec);
                                            }
                                        }
                                    } else if ("Nazev".equalsIgnoreCase(currentTag) && currentCastObce.getNazev() == null) {
                                        currentCastObce.setNazev(text);
                                    }
                                }
                            }
                        } else if (event == XMLStreamConstants.END_ELEMENT) {
                            String endTag = getCleanTagName(reader);

                            if ("Obec".equalsIgnoreCase(endTag) && currentObec != null && currentCastObce == null) {
                                Obec savedObec = obecRepository.save(currentObec);
                                obecCache.put(savedObec.getKod(), savedObec);
                                currentObec = null;
                            } else if ("CastObce".equalsIgnoreCase(endTag) && currentCastObce != null) {
                                castObceRepository.save(currentCastObce);
                                currentCastObce = null;
                            }

                            currentTag = "";
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during XML import", e);
        }
    }

    private String getCleanTagName(XMLStreamReader reader) {
        String name = reader.getLocalName();
        if (name.contains(":")) {
            name = name.substring(name.indexOf(":") + 1);
        }
        return name;
    }
}