package at.vulperium.cryptobot.dtos;

import java.io.Serializable;

/**
 * Simple Ausfuehrung eines WaherungsDTOs
 */
public class CryptoWaehrungKurzDTO implements Serializable {

    private Long id;
    private String bezeichnung;
    private String kurzbezeichnung;
    private boolean gueltig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getKurzbezeichnung() {
        return kurzbezeichnung;
    }

    public void setKurzbezeichnung(String kurzbezeichnung) {
        this.kurzbezeichnung = kurzbezeichnung;
    }

    public boolean isGueltig() {
        return gueltig;
    }

    public void setGueltig(boolean gueltig) {
        this.gueltig = gueltig;
    }
}
