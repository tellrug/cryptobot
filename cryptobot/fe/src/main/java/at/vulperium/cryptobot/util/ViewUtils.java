package at.vulperium.cryptobot.util;

import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.Trend;
import com.vaadin.icons.VaadinIcons;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 04.01.2018.
 */
public class ViewUtils {

    public static final String DATE_TIME_FORMAT_OHNE_SEKUNDEN = "yyyy-MM-dd HH:mm";
    private static final String NEW_LINE = "<br />";
    private static final String ABSTAND = "&nbsp;";
    private static final String COLOR_GREEN = "#008000";
    private static final String COLOR_RED = "#ff0000";
    private static final String COLOR_GRAY = "#808080";


    public static String formatWertDiffInfo(BigDecimal value1) {
        return formatWertInfoColor(value1, null);
    }

    public static String formatWertInfoColor(BigDecimal value1, String einheitValue1) {
        String value1AsString = formatWertEinheit(value1, einheitValue1);
        String text = "<strong>" + value1AsString + "</strong>";
        return colorDiff(Trend.ermittleAbsolutTrend(value1), text);
    }

    public static String formatWertInfo(String bezValue1, BigDecimal value1) {
        return formatWertInfo(bezValue1, value1, null);
    }

    public static String formatWertInfo(String bezValue1, BigDecimal value1, String einheitValue1) {
        String value1AsString = formatWertEinheit(value1, einheitValue1);
        return "<strong>" + bezValue1 + ": </strong>" + value1AsString;
    }

    public static String formatWertInfo(String bezValue1, BigDecimal value1, String bezValue2, BigDecimal value2) {
        return formatWertInfo(bezValue1, value1, null, bezValue2, value2, null);
    }

    public static String formatWertInfo(String bezValue1, BigDecimal value1, String einheitValue1, String bezValue2, BigDecimal value2, String einheitValue2) {
        return formatWertInfo(bezValue1, value1, einheitValue1, bezValue2, value2, einheitValue2, false);
    }

    public static String formatWertInfo(String bezValue1, BigDecimal value1, String einheitValue1, String bezValue2, BigDecimal value2, String einheitValue2, boolean color) {
        String value1AsString;
        String value2AsString;
        if (color) {
            value1AsString = formatWertInfoColor(value1, einheitValue1);
            value2AsString = formatWertInfoColor(value2, einheitValue2);
        }
        else {
            value1AsString = formatWertEinheit(value1, einheitValue1);
            value2AsString = formatWertEinheit(value2, einheitValue2);
        }

        return "<strong>" + bezValue1 + ": </strong>" + value1AsString + NEW_LINE + "" +
                "<strong>" + bezValue2 + ": </strong>" + value2AsString;
    }

    public static String formatWertEinheit(BigDecimal value1, String einheitValue1) {
        String value1AsString = value1 != null ? value1.toString() : "-";
        if (einheitValue1 != null) {
            value1AsString = value1AsString + " " + einheitValue1;
        }
        return value1AsString;
    }

    public static String dateTimeToStringOhneSkunden(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        return localDateTime.toString(DateTimeFormat.forPattern(DATE_TIME_FORMAT_OHNE_SEKUNDEN));
    }

    public static String colorDiff(Trend trend, String text) {
        if (trend == null) {
            return "<span style='color: " + COLOR_GRAY + "'>" + text + "</span>";
        }

        switch (trend) {
            case AUFWAERTS:
                return "<span style='color: " + COLOR_GREEN + "'>" + text + "</span>";
            case ABWAERTS:
                return "<span style='color: " + COLOR_RED + "'>" + text + "</span>";
            default:
                return "<span style='color: " + COLOR_GRAY + "'>" + text + "</span>";
        }
    }


    public static String transformTradeJobStatus(TradeJobStatus tradeJobStatus) {
        switch (tradeJobStatus) {
            case KAUF_ZIEL:
                return VaadinIcons.MONEY_DEPOSIT.getHtml();
            case KAUF_ZIEL_ERREICHT:
                return VaadinIcons.MONEY_DEPOSIT.getHtml() + ABSTAND + VaadinIcons.CHECK_CIRCLE.getHtml();
            case KAUF_FEHLER:
                return VaadinIcons.MONEY_DEPOSIT.getHtml() + ABSTAND + VaadinIcons.CLOSE.getHtml();
            case VERKAUF_ZIEL:
                return VaadinIcons.MONEY_WITHDRAW.getHtml();
            case VERKAUF_ZIEL_ERREICHT:
                return VaadinIcons.MONEY_WITHDRAW.getHtml() + ABSTAND + VaadinIcons.CHECK_CIRCLE.getHtml();
            case VERKAUF_FEHLER:
                return VaadinIcons.MONEY_WITHDRAW.getHtml() + ABSTAND + VaadinIcons.CLOSE.getHtml();
        }

        throw new IllegalArgumentException("Fehlerhafter TradeJobStatus vorhanden");
    }
}
