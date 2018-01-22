package at.vulperium.cryptobot.util;

import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public interface Filterable {

    boolean filteringTradingPlattform(TradingPlattform tradingPlattform);

    boolean filteringSymbol(String symbol);

    boolean filteringTradeTyp(TradeTyp tradeTyp);
}
