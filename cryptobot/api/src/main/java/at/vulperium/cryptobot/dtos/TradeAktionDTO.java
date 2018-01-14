package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TradeStatus;
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
    private String vonWaehrung;
    private String zuWaehrung;
    private BigDecimal vonMenge;
    private BigDecimal zuMenge;
    private TradeStatus tradeStatus;
    private TradingPlattform tradingPlattform;
    private Long userId;
    private Long tradeJobId;

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

    public BigDecimal getVonMenge() {
        return vonMenge;
    }

    public void setVonMenge(BigDecimal vonMenge) {
        this.vonMenge = vonMenge;
    }

    public String getVonWaehrung() {
        return vonWaehrung;
    }

    public void setVonWaehrung(String vonWaehrung) {
        this.vonWaehrung = vonWaehrung;
    }

    public BigDecimal getZuMenge() {
        return zuMenge;
    }

    public void setZuMenge(BigDecimal zuMenge) {
        this.zuMenge = zuMenge;
    }

    public String getZuWaehrung() {
        return zuWaehrung;
    }

    public void setZuWaehrung(String zuWaehrung) {
        this.zuWaehrung = zuWaehrung;
    }

    public Long getTradeJobId() {
        return tradeJobId;
    }

    public void setTradeJobId(Long tradeJobId) {
        this.tradeJobId = tradeJobId;
    }
}
