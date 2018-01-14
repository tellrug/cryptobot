package at.vulperium.cryptobot.tradejobs.vo;

import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class FilterVO implements Serializable {

    private TradingPlattform tradingPlattform;
    private TradeTyp tradeTyp;

    public TradeTyp getTradeTyp() {
        return tradeTyp;
    }

    public void setTradeTyp(TradeTyp tradeTyp) {
        this.tradeTyp = tradeTyp;
    }

    public TradingPlattform getTradingPlattform() {
        return tradingPlattform;
    }

    public void setTradingPlattform(TradingPlattform tradingPlattform) {
        this.tradingPlattform = tradingPlattform;
    }
}
