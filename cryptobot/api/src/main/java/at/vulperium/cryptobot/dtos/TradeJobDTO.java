package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TradeBasisStatus;
import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Ace on 26.12.2017.
 */
public class TradeJobDTO implements Serializable {

    private Long id;
    private String cryptoWaehrung; //TODO wird noch durch DTO ersetzt
    private LocalDateTime erstelltAm;
    private LocalDateTime erledigtAm;
    private BigDecimal menge;
    private BigDecimal kaufwert;
    private BigDecimal letztwert;
    private BigDecimal zielwert;
    private String cryptoWaehrungReferenz; //TODO wird noch durch DTO ersetzt
    private TradeJobStatus tradeJobStatus;
    private TradeBasisStatus tradeBasisStatus;
    private TradingPlattform tradingPlattform;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCryptoWaehrung() {
        return cryptoWaehrung;
    }

    public void setCryptoWaehrung(String cryptoWaehrung) {
        this.cryptoWaehrung = cryptoWaehrung;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public LocalDateTime getErledigtAm() {
        return erledigtAm;
    }

    public void setErledigtAm(LocalDateTime erledigtAm) {
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

    public String getCryptoWaehrungReferenz() {
        return cryptoWaehrungReferenz;
    }

    public void setCryptoWaehrungReferenz(String cryptoWaehrungReferenz) {
        this.cryptoWaehrungReferenz = cryptoWaehrungReferenz;
    }

    public TradeJobStatus getTradeJobStatus() {
        return tradeJobStatus;
    }

    public void setTradeJobStatus(TradeJobStatus tradeJobStatus) {
        this.tradeJobStatus = tradeJobStatus;
    }

    public TradeBasisStatus getTradeBasisStatus() {
        return tradeBasisStatus;
    }

    public void setTradeBasisStatus(TradeBasisStatus tradeBasisStatus) {
        this.tradeBasisStatus = tradeBasisStatus;
    }

    public TradingPlattform getTradingPlattform() {
        return tradingPlattform;
    }

    public void setTradingPlattform(TradingPlattform tradingPlattform) {
        this.tradingPlattform = tradingPlattform;
    }
}
