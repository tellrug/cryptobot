package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TradingPlattform;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 02ub0400 on 17.01.2018.
 */
public class HoldingOrderDTO implements Serializable {

    private TradingPlattform tradingPlattform;

    //Beinhaltet Informationen welche Menge pro Symbol zur Verfuegung steht
    private Map<String, BigDecimal> holdingMap = new HashMap<>();


    public Map<String, BigDecimal> getHoldingMap() {
        return holdingMap;
    }

    public void setHoldingMap(Map<String, BigDecimal> holdingMap) {
        this.holdingMap = holdingMap;
    }

    public TradingPlattform getTradingPlattform() {
        return tradingPlattform;
    }

    public void setTradingPlattform(TradingPlattform tradingPlattform) {
        this.tradingPlattform = tradingPlattform;
    }
}
