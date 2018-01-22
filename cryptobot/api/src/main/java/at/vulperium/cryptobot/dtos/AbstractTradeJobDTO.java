package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
public abstract class AbstractTradeJobDTO {

    private Long id;
    private LocalDateTime erstelltAm;
    private LocalDateTime erledigtAm;
    private String cryptoWaehrung;          //TODO wird noch durch DTO ersetzt
    private String cryptoWaehrungReferenz;  //TODO wird noch durch DTO ersetzt
    private BigDecimal menge;
    private BigDecimal kaufwert;
    private BigDecimal letztwert;
    private BigDecimal zielwert;
    private BigDecimal spitzenwert;
    private boolean ganzZahlig;
    private TradeAktionEnum tradeAktionEnum;
    private TradeStatus tradeStatus;
    private TradingPlattform tradingPlattform;
    private TradeJobTyp tradeJobTyp;

    public String getCryptoWaehrung() {
        return cryptoWaehrung;
    }

    public void setCryptoWaehrung(String cryptoWaehrung) {
        this.cryptoWaehrung = cryptoWaehrung;
    }

    public String getCryptoWaehrungReferenz() {
        return cryptoWaehrungReferenz;
    }

    public void setCryptoWaehrungReferenz(String cryptoWaehrungReferenz) {
        this.cryptoWaehrungReferenz = cryptoWaehrungReferenz;
    }

    public LocalDateTime getErledigtAm() {
        return erledigtAm;
    }

    public void setErledigtAm(LocalDateTime erledigtAm) {
        this.erledigtAm = erledigtAm;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getSpitzenwert() {
        return spitzenwert;
    }

    public void setSpitzenwert(BigDecimal spitzenwert) {
        this.spitzenwert = spitzenwert;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public TradeAktionEnum getTradeAktionEnum() {
        return tradeAktionEnum;
    }

    public void setTradeAktionEnum(TradeAktionEnum tradeAktionEnum) {
        this.tradeAktionEnum = tradeAktionEnum;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public TradingPlattform getTradingPlattform() {
        return tradingPlattform;
    }

    public void setTradingPlattform(TradingPlattform tradingPlattform) {
        this.tradingPlattform = tradingPlattform;
    }

    public BigDecimal getZielwert() {
        return zielwert;
    }

    public void setZielwert(BigDecimal zielwert) {
        this.zielwert = zielwert;
    }

    public TradeJobTyp getTradeJobTyp() {
        return tradeJobTyp;
    }

    public void setTradeJobTyp(TradeJobTyp tradeJobTyp) {
        this.tradeJobTyp = tradeJobTyp;
    }

    public boolean isGanzZahlig() {
        return ganzZahlig;
    }

    public void setGanzZahlig(boolean ganzZahlig) {
        this.ganzZahlig = ganzZahlig;
    }
}
