package at.vulperium.cryptobot.enums;

import at.vulperium.cryptobot.utils.TradeUtil;

import java.math.BigDecimal;

public enum Trend {

    AUFWAERTS,
    ABWAERTS,
    KONSTANT;

    //TODO als ConfigParam auslagern
    //Dieser Satz gibt an ab wann eine Trendaenderung angenommen wird
    public static final BigDecimal AENDERUNGSSATZ = TradeUtil.getBigDecimal(0.03);
}
