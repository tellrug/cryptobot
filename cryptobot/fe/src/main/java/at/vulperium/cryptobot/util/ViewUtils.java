package at.vulperium.cryptobot.util;

import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
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
    public static final String NEW_LINE = "<br />";
    public static final String ABSTAND = "&nbsp;";
    public static final String COLOR_GREEN = "#008000";
    public static final String COLOR_RED = "#ff0000";
    public static final String COLOR_GRAY = "#808080";


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

    public static String formatTradeInfo(String s1, String s2, String symbol, TradeTyp tradeTyp) {
        String text = s1 + " " + symbol + " " + s2;
        return text;
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

    public static String formatColor(String text, String color) {
        return "<span style='color: " + color + "'>" + text + "</span>";
    }

    public static String transformTradeAktionUndStatusToIcon(TradeAktionEnum tradeAktionEnum, TradeStatus tradeStatus) {

        String aktionIcon = transformTradeAktionToIcon(tradeAktionEnum);
        String statusIcon = transformTradeStatusToIcon(tradeStatus);

        if (statusIcon == null) {
            return aktionIcon;
        }
        return aktionIcon + ABSTAND + statusIcon;
    }


    public static String transformTradeAktionToIcon(TradeAktionEnum tradeAktionEnum) {
        switch (tradeAktionEnum) {
            case ORDER_KAUF:
                return VaadinIcons.MONEY_DEPOSIT.getHtml();
            case KAUF_ZIEL:
                return VaadinIcons.ARROW_CIRCLE_UP.getHtml();
            case ORDER_VERKAUF:
                return VaadinIcons.MONEY_WITHDRAW.getHtml();
            case VERKAUF_ZIEL:
                return VaadinIcons.ARROW_CIRCLE_DOWN.getHtml();
        }

        throw new IllegalArgumentException("Fehlerhafte TradeAktion vorhanden: " + tradeAktionEnum);
    }

    public static String transformTradeStatusToIcon(TradeStatus tradeStatus) {
        switch (tradeStatus) {
            case ERSTELLT:
                return null;
            case BEOBACHTUNG:
                return VaadinIcons.EYE.getHtml();
            case ABGESCHLOSSEN:
                return VaadinIcons.CHECK_CIRCLE.getHtml();
            case FOLGE_AKTION:
                return VaadinIcons.ARROW_CIRCLE_RIGHT.getHtml();
            case TRADE_KAUF:
            case TRADE_VERKAUF:
                return VaadinIcons.CLIPBOARD_PULSE.getHtml();
            case TRADE_PRUEFUNG_KAUF:
            case TRADE_PRUEFUNG_VERKAUF:
                return VaadinIcons.HOURGLASS_START.getHtml();
            case TRADE_FEHLGESCHLAGEN:
            case FEHLER:
                return VaadinIcons.CLOSE.getHtml();
        }

        throw new IllegalArgumentException("Fehlerhafter TradeStatus vorhanden: " + tradeStatus);
    }
}
