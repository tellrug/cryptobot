package at.vulperium.cryptobot.tradejobs.vo;

import at.vulperium.cryptobot.enums.TradeStatusTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class FilterVO implements Serializable {

    private TradingPlattform tradingPlattform;
    private TradeStatusTyp tradeStatusTyp;

    public TradeStatusTyp getTradeStatusTyp() {
        return tradeStatusTyp;
    }

    public void setTradeStatusTyp(TradeStatusTyp tradeStatusTyp) {
        this.tradeStatusTyp = tradeStatusTyp;
    }

    public TradingPlattform getTradingPlattform() {
        return tradingPlattform;
    }

    public void setTradingPlattform(TradingPlattform tradingPlattform) {
        this.tradingPlattform = tradingPlattform;
    }
}
