package at.vulperium.cryptobot.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Ace on 24.12.2017.
 */
@Entity
@Table(name = "TRADEAKTION")
@NamedQueries({
        @NamedQuery(name = TradeAktion.QRY_FIND_ALL,
        query = "SELECT t FROM TradeAktion t "),
        @NamedQuery(name = TradeAktion.QRY_FIND_BY_ID,
                query = "SELECT t FROM TradeAktion t WHERE t.id = :" + TradeAktion.PARAM_ID)
})
public class TradeAktion extends BaseEntity<Long> {

    public static final String QRY_FIND_ALL = "tradeAktion.findAll";
    public static final String QRY_FIND_BY_ID = "tradeAktion.findById";
    public static final String PARAM_ID = "paramId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRADEAKTION_SQ")
    @SequenceGenerator(name = "TRADEAKTION_SQ", sequenceName = "TRADEAKTION_SQ", allocationSize = 1, initialValue = 200000)
    @Column(name = "TRADEAKTION_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "CRYPTOWAEHRUNG", nullable = false, updatable = false)
    private String cryptoWaehrung;

    @Column(name = "ERSTELLTAM", nullable = false, updatable = false)
    private Date erstelltAm;

    @Column(name = "ERLEDIGTAM", nullable = true, updatable = true)
    private Date erledigtAm;

    @Column(name = "MENGE", nullable = false, updatable = true)
    private BigDecimal menge;

    @Column(name = "KAUFWERT", nullable = false, updatable = true)
    private BigDecimal kaufwert;

    @Column(name = "LETZTWERT", nullable = true, updatable = true)
    private BigDecimal letztwert;

    @Column(name = "ZIELWERT", nullable = true, updatable = true)
    private BigDecimal zielwert;

    @Column(name = "CRYPTOWAEHRUNG_REFERENZ", nullable = false, updatable = false)
    private String referenzCryptoWaehrung;

    @Column(name = "STATUS", nullable = false, updatable = true)
    private String status;

    @Column(name = "PLATTFORM", nullable = false, updatable = true)
    private String tradingplattform;

    @Override
    public Long getId() {
        return id;
    }

    public String getCryptoWaehrung() {
        return cryptoWaehrung;
    }

    public void setCryptoWaehrung(String cryptoWaehrung) {
        this.cryptoWaehrung = cryptoWaehrung;
    }

    public Date getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(Date erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Date getErledigtAm() {
        return erledigtAm;
    }

    public void setErledigtAm(Date erledigtAm) {
        this.erledigtAm = erledigtAm;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public BigDecimal getKaufwert() {
        return kaufwert;
    }

    public void setKaufwert(BigDecimal kaufwert) {
        this.kaufwert = kaufwert;
    }

    public BigDecimal getLetztwert() {
        return letztwert;
    }

    public void setLetztwert(BigDecimal letztwert) {
        this.letztwert = letztwert;
    }

    public BigDecimal getZielwert() {
        return zielwert;
    }

    public void setZielwert(BigDecimal zielwert) {
        this.zielwert = zielwert;
    }

    public String getReferenzCryptoWaehrung() {
        return referenzCryptoWaehrung;
    }

    public void setReferenzCryptoWaehrung(String referenzCryptoWaehrung) {
        this.referenzCryptoWaehrung = referenzCryptoWaehrung;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradingplattform() {
        return tradingplattform;
    }

    public void setTradingplattform(String tradingplattform) {
        this.tradingplattform = tradingplattform;
    }
}
