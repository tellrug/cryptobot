package at.vulperium.cryptobot.utils;

import org.joda.time.Duration;
import org.joda.time.Minutes;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public class TradeUtil {

    public static final int SCALE = 15;
    public static final Duration VERARBEITUNG_INTERVALL = new Duration(Minutes.minutes(1).toStandardDuration());

    public static BigDecimal getBigDecimal(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Der Wert null kann nicht in BigDecimal umgewandelt werden!");
        }

        return new BigDecimal(value).setScale(SCALE, BigDecimal.ROUND_HALF_DOWN);
    }

    public static BigDecimal getBigDecimal(int value) {
        String stringValue = String.valueOf(value);
        return new BigDecimal(stringValue).setScale(SCALE, BigDecimal.ROUND_HALF_DOWN);
    }

    public static BigDecimal getBigDecimal(double value) {
        String stringValue = String.valueOf(value);
        return new BigDecimal(stringValue).setScale(SCALE, BigDecimal.ROUND_HALF_DOWN);
    }
}
