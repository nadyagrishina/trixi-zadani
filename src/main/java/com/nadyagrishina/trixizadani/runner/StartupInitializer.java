package com.nadyagrishina.trixizadani.runner;

import com.nadyagrishina.trixizadani.service.XmlImportService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupInitializer {

    private final XmlImportService xmlImportService;

    public StartupInitializer(XmlImportService xmlImportService) {
        this.xmlImportService = xmlImportService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String url = "https://www.smartform.cz/download/kopidlno.xml.zip";
        xmlImportService.importData(url);
    }
}
