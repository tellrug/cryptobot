package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * Created by 02ub0400 on 11.01.2018.
 */
public class TradeAktionDTO implements Serializable {

    private Long id;
    private LocalDateTime erstelltAm;
    private LocalDateTime erledigtAm;
    private String cryptoWaehrung;          //TODO wird noch durch DTO ersetzt
    private String cryptoWaehrungReferenz;  //TODO wird noch durch DTO ersetzt
    private BigDecimal menge;
    private BigDecimal mengeReferenz;
    private BigDecimal preisProEinheit;
    private TradeStatus tradeStatus;
    private TradingPlattform tradingPlattform;
    private Long userId;
    private Long referenzTradeAktionId;
    private TradeTyp tradeTyp;
    private Long tradeJobId;
    private TradeJobTyp tradeJobTyp;
    private String customerOrderId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReferenzTradeAktionId() {
        return referenzTradeAktionId;
    }

    public void setReferenzTradeAktionId(Long referenzTradeAktionId) {
        this.referenzTradeAktionId = referenzTradeAktionId;
    }

    public TradeTyp getTradeTyp() {
        return tradeTyp;
    }

    public void setTradeTyp(TradeTyp tradeTyp) {
        this.tradeTyp = tradeTyp;
    }

    public Long getTradeJobId() {
        return tradeJobId;
    }

    public void setTradeJobId(Long tradeJobId) {
        this.tradeJobId = tradeJobId;
    }

    public BigDecimal getPreisProEinheit() {
        return preisProEinheit;
    }

    public void setPreisProEinheit(BigDecimal preisProEinheit) {
        this.preisProEinheit = preisProEinheit;
    }

    public TradeJobTyp getTradeJobTyp() {
        return tradeJobTyp;
    }

    public void setTradeJobTyp(TradeJobTyp tradeJobTyp) {
        this.tradeJobTyp = tradeJobTyp;
    }

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

    public String getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(String customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public BigDecimal getMengeReferenz() {
        return mengeReferenz;
    }

    public void setMengeReferenz(BigDecimal mengeReferenz) {
        this.mengeReferenz = mengeReferenz;
    }
}
