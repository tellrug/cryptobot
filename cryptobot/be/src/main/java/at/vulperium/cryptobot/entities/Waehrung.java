package at.vulperium.cryptobot.entities;

import javax.persistence.*;

/**
 * Created by Ace on 26.12.2017.
 */
@Entity
@Table(name = "WAEHRUNG")
@NamedQueries({
        @NamedQuery(name = Waehrung.QRY_FIND_ALL,
                query = "SELECT t FROM CryptoWaehrung t "),
        @NamedQuery(name = Waehrung.QRY_FIND_BY_ID,
                query = "SELECT t FROM CryptoWaehrung t WHERE t.id = :" + Waehrung.PARAM_ID)
})
public class Waehrung extends BaseEntity<Long> {

    public static final String QRY_FIND_ALL = "waehrung.findAll";
    public static final String QRY_FIND_BY_ID = "waehrung.findById";
    public static final String PARAM_ID = "paramId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAEHRUNG_SQ")
    @SequenceGenerator(name = "WAEHRUNG_SQ", sequenceName = "WAEHRUNG_SQ", allocationSize = 1, initialValue = 200000)
    @Column(name = "WAEHRUNG_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "BEZEICHNUNG", nullable = false, updatable = true)
    private String bezeichnung;

    @Column(name = "KURZBEZEICHNUNG", nullable = false, updatable = true)
    private String kurzbezeichnung;

    @Column(name = "GUELTIG", nullable = false, updatable = true)
    private Boolean gueltig;

    @Override
    public Long getId() {
        return id;
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

    public Boolean getGueltig() {
        return gueltig;
    }

    public void setGueltig(Boolean gueltig) {
        this.gueltig = gueltig;
    }
}
