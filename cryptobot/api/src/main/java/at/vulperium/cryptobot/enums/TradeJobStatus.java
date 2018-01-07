package at.vulperium.cryptobot.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ace on 26.12.2017.
 */
public enum TradeJobStatus {

    //Es ist ein Kauf vorgesehen sobald gewisse Parameter eingetreten sind
    KAUF_ZIEL("K", TradeStatusTyp.KAUF, "Kauf vorgesehen"),
    //Es ist ein Verkauf vorgesehen sobald gewisse Parameter eingetreten sind
    VERKAUF_ZIEL("V", TradeStatusTyp.VERKAUF, "Verkauf vorgesehen"),
    KAUF_ZIEL_ERREICHT("KE", TradeStatusTyp.KAUF, "Kauf-Ziel erreicht"),
    VERKAUF_ZIEL_ERREICHT("VE", TradeStatusTyp.VERKAUF, "Verkauf-Ziel erreicht"),
    KAUF_FEHLER("KERR", TradeStatusTyp.KAUF, "Fehler bei Kauf"),
    VERKAUF_FEHLER("VERR", TradeStatusTyp.VERKAUF, "Fehler bei Verkauf");

    private String code;
    private TradeStatusTyp tradeStatusTyp;
    private String anzeigetext;

    TradeJobStatus(String code, TradeStatusTyp tradeStatusTyp, String anzeigetext) {
        this.code = code;
        this.tradeStatusTyp = tradeStatusTyp;
        this.anzeigetext = anzeigetext;
    }

    public String getCode() {
        return code;
    }

    public TradeStatusTyp getTradeStatusTyp() {
        return tradeStatusTyp;
    }

    public String getAnzeigetext() {
        return anzeigetext;
    }

    public static TradeJobStatus getByCode(String code) {
        for (TradeJobStatus tradeJobStatus : values()) {
            if (tradeJobStatus.getCode().equals(code)) {
                return tradeJobStatus;
            }
        }

        return null;
    }

    public static List<TradeJobStatus> getByTradeStatusTyp(TradeStatusTyp tradeStatusTyp) {
        List<TradeJobStatus> tradeJobStatusList = new ArrayList<>();
        for (TradeJobStatus tradeJobStatus : values()) {
            if (tradeJobStatus.getTradeStatusTyp() == tradeStatusTyp) {
                tradeJobStatusList.add(tradeJobStatus);
            }
        }

        return tradeJobStatusList;
    }
}
