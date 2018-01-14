package at.vulperium.cryptobot.entities;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
public abstract class AbstractTradeJob extends BaseEntity<Long> {

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

    @Column(name = "AKTION", nullable = false, updatable = false)
    private String tradeAktion;

    @Column(name = "TRADE_STATUS", nullable = false, updatable = true)
    private String tradestatus;

    @Column(name = "PLATTFORM", nullable = false, updatable = true)
    private String tradingplattform;


    public String getCryptoWaehrung() {
        return cryptoWaehrung;
    }

    public void setCryptoWaehrung(String cryptoWaehrung) {
        this.cryptoWaehrung = cryptoWaehrung;
    }

    public Date getErledigtAm() {
        return erledigtAm;
    }

    public void setErledigtAm(Date erledigtAm) {
        this.erledigtAm = erledigtAm;
    }

    public Date getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(Date erstelltAm) {
        this.erstelltAm = erstelltAm;
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

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public String getReferenzCryptoWaehrung() {
        return referenzCryptoWaehrung;
    }

    public void setReferenzCryptoWaehrung(String referenzCryptoWaehrung) {
        this.referenzCryptoWaehrung = referenzCryptoWaehrung;
    }

    public String getTradeAktion() {
        return tradeAktion;
    }

    public void setTradeAktion(String tradeAktion) {
        this.tradeAktion = tradeAktion;
    }

    public String getTradestatus() {
        return tradestatus;
    }

    public void setTradestatus(String tradestatus) {
        this.tradestatus = tradestatus;
    }

    public String getTradingplattform() {
        return tradingplattform;
    }

    public void setTradingplattform(String tradingplattform) {
        this.tradingplattform = tradingplattform;
    }

    public BigDecimal getZielwert() {
        return zielwert;
    }

    public void setZielwert(BigDecimal zielwert) {
        this.zielwert = zielwert;
    }
}
