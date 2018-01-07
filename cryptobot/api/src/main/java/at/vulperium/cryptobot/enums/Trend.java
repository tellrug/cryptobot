package at.vulperium.cryptobot.enums;

import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.utils.TradeUtil;

import java.math.BigDecimal;

public enum Trend {

    AUFWAERTS,
    ABWAERTS,
    KONSTANT;

    //Dieser Satz gibt an ab wann eine Trendaenderung angenommen wird
    private static final ConfigValue aenderungssatz = new ConfigValue("aenderungssatz");

    public static BigDecimal getAenderungssatz() {
        return TradeUtil.getBigDecimal(aenderungssatz.get());
    }

    public static Trend ermittleAbsolutTrend(BigDecimal value) {
        if (value == null) {
            return null;
        }

        if (value.compareTo(TradeUtil.getBigDecimal("0")) == 1) {
            return AUFWAERTS;
        }
        else if (value.compareTo(TradeUtil.getBigDecimal("0")) == -1) {
            return ABWAERTS;
        }
        return KONSTANT;
    }

    public static Trend ermittleAbsolutTrend(BigDecimal neuerWert, BigDecimal alterWert) {
        return ermittleAbsolutTrend(TradeUtil.diffAbsolut(neuerWert, alterWert));
    }
}
