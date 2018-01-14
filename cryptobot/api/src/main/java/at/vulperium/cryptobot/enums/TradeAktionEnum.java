package at.vulperium.cryptobot.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ace on 26.12.2017.
 */
public enum TradeAktionEnum {

    //Es wird eine Kauf-Order beauftragt
    ORDER_KAUF("KO", TradeTyp.KAUF, "Kauf-Auftrag stellen"),
    //Es wird eine Verkauf-Order beauftragt
    ORDER_VERKAUF("VO", TradeTyp.VERKAUF, "Verkauf-Auftrag stellen"),

    //Es ist ein Kauf vorgesehen sobald gewisse Parameter eingetreten sind
    KAUF_ZIEL("K", TradeTyp.KAUF, "Kauf-Ziel beobachten"),
    //Es ist ein Verkauf vorgesehen sobald gewisse Parameter eingetreten sind
    VERKAUF_ZIEL("V", TradeTyp.VERKAUF, "Verkauf-Ziel beobachten");

    private String code;
    private TradeTyp tradeTyp;
    private String anzeigetext;

    TradeAktionEnum(String code, TradeTyp tradeTyp, String anzeigetext) {
        this.code = code;
        this.tradeTyp = tradeTyp;
        this.anzeigetext = anzeigetext;
    }

    public String getCode() {
        return code;
    }

    public TradeTyp getTradeTyp() {
        return tradeTyp;
    }

    public String getAnzeigetext() {
        return anzeigetext;
    }

    public static TradeAktionEnum getByCode(String code) {
        for (TradeAktionEnum tradeAktionEnum : values()) {
            if (tradeAktionEnum.getCode().equals(code)) {
                return tradeAktionEnum;
            }
        }

        return null;
    }

    public static List<TradeAktionEnum> getByTradeTyp(TradeTyp tradeTyp) {
        List<TradeAktionEnum> tradeAktionEnumList = new ArrayList<>();
        for (TradeAktionEnum tradeAktionEnum : values()) {
            if (tradeAktionEnum.getTradeTyp() == tradeTyp) {
                tradeAktionEnumList.add(tradeAktionEnum);
            }
        }

        return tradeAktionEnumList;
    }
}
