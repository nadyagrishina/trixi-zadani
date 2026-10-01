package com.nadyagrishina.trixizadani.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "obec")
public class Obec {

    @Id
    private long kod;

    private String nazev;

    public long getKod() {
        return kod;
    }

    public void setKod(long kod) {
        this.kod = kod;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String name) {
        this.nazev = name;
    }
}
