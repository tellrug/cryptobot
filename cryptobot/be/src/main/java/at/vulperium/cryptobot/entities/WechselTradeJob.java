package at.vulperium.cryptobot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
@Entity
@Table(name = "WECHSEL_TRADEJOB")
@NamedQueries({
        @NamedQuery(name = WechselTradeJob.QRY_FIND_ALL,
                query = "SELECT t FROM WechselTradeJob t ")
})
public class WechselTradeJob extends AbstractTradeJob {

    public static final String QRY_FIND_ALL = "wechselTradeJob.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WECHSEL_TRADEJOB_SQ")
    @SequenceGenerator(name = "WECHSEL_TRADEJOB_SQ", sequenceName = "WECHSEL_TRADEJOB_SQ", allocationSize = 1, initialValue = 200000)
    @Column(name = "WECHSEL_TRADEJOB_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "KAUFWERT_GRENZE", nullable = false, updatable = true)
    private BigDecimal kaufwertGrenze;

    @Column(name = "MENGE_REFERENZWERT", nullable = false, updatable = true)
    private BigDecimal mengeReferenzwert;

    @Column(name = "GANZ_ZAHLIG", nullable = false, updatable = true)
    private boolean ganzzahligerVerkauf;

    @Column(name = "MIN_ZIELSATZ", nullable = true, updatable = true)
    private BigDecimal minimalZielSatz;

    @Column(name = "VORGESEHNER_VERKAUFSWERT", nullable = true, updatable = true)
    private BigDecimal vorgesehenerVerkaufwert;

    @Column(name = "TRADETYP", nullable = false, updatable = true)
    private String tradeTyp;

    @Column(name = "TRADEVERSUCHAM", nullable = true, updatable = true)
    private Date tradeVersuchAm;


    @Override
    public Long getId() {
        return id;
    }


    public boolean isGanzzahligerVerkauf() {
        return ganzzahligerVerkauf;
    }

    public void setGanzzahligerVerkauf(boolean ganzzahligerVerkauf) {
        this.ganzzahligerVerkauf = ganzzahligerVerkauf;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getKaufwertGrenze() {
        return kaufwertGrenze;
    }

    public void setKaufwertGrenze(BigDecimal kaufwertGrenze) {
        this.kaufwertGrenze = kaufwertGrenze;
    }

    public BigDecimal getMengeReferenzwert() {
        return mengeReferenzwert;
    }

    public void setMengeReferenzwert(BigDecimal mengeReferenzwert) {
        this.mengeReferenzwert = mengeReferenzwert;
    }

    public BigDecimal getMinimalZielSatz() {
        return minimalZielSatz;
    }

    public void setMinimalZielSatz(BigDecimal minimalZielSatz) {
        this.minimalZielSatz = minimalZielSatz;
    }

    public String getTradeTyp() {
        return tradeTyp;
    }

    public void setTradeTyp(String tradeTyp) {
        this.tradeTyp = tradeTyp;
    }

    public Date getTradeVersuchAm() {
        return tradeVersuchAm;
    }

    public void setTradeVersuchAm(Date tradeVersuchAm) {
        this.tradeVersuchAm = tradeVersuchAm;
    }

    public BigDecimal getVorgesehenerVerkaufwert() {
        return vorgesehenerVerkaufwert;
    }

    public void setVorgesehenerVerkaufwert(BigDecimal vorgesehenerVerkaufwert) {
        this.vorgesehenerVerkaufwert = vorgesehenerVerkaufwert;
    }
}
