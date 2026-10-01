package com.nadyagrishina.trixizadani.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cast_obce")
public class CastObce {

    @Id
    private long kod;

    private String nazev;

    @ManyToOne
    @JoinColumn(name = "obec_kod", nullable = false)
    private Obec obec;

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

    public Obec getObec() {
        return obec;
    }

    public void setObec(Obec obec) {
        this.obec = obec;
    }
}
