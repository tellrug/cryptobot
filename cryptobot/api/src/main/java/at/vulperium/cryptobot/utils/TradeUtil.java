package at.vulperium.cryptobot.utils;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public class TradeUtil {

    public static final int SCALE = 10;

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


    public static BigDecimal diffAbsolut(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            return null;
        }
        return value1.subtract(value2);
    }

    public static BigDecimal diffProzent(BigDecimal neuerWert, BigDecimal alterWert) {
        if (neuerWert == null || alterWert == null) {
            return null;
        }

        BigDecimal aenderung = neuerWert.divide(alterWert, BigDecimal.ROUND_HALF_EVEN);


        BigDecimal diffProzentValue;
        if (aenderung.compareTo(getBigDecimal(1.0)) == 1) {
            diffProzentValue = aenderung.subtract(getBigDecimal(1.0)); //relative Aenderung
        }
        else if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == -1) {
            diffProzentValue = (getBigDecimal(1.0).subtract(aenderung)).multiply(getBigDecimal(-1)); //relative Aenderung
        }
        else {
            diffProzentValue = getBigDecimal(0); //keine Aenderung
        }

        return diffProzentValue.multiply(getBigDecimal(100.0)).setScale(4, BigDecimal.ROUND_HALF_EVEN);
    }
}
