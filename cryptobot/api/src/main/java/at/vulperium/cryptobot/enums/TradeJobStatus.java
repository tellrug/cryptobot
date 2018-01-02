package at.vulperium.cryptobot.enums;

/**
 * Created by Ace on 26.12.2017.
 */
public enum TradeJobStatus {

    //Es ist ein Kauf vorgesehen sobald gewisse Parameter eingetreten sind
    KAUF_ZIEL("K", TradeStatusTyp.KAUF),
    //Es ist ein Verkauf vorgesehen sobald gewisse Parameter eingetreten sind
    VERKAUF_ZIEL("V", TradeStatusTyp.VERKAUF),
    KAUF_ABGESCHLOSSEN("KA", TradeStatusTyp.KAUF),
    VERKAUF_ABGESCHLOSSEN("VA", TradeStatusTyp.VERKAUF),
    KAUF_FEHLER("KERR", TradeStatusTyp.KAUF),
    VERKAUF_FEHLER("VERR", TradeStatusTyp.VERKAUF);

    private String code;
    private TradeStatusTyp tradeStatusTyp;

    TradeJobStatus(String code, TradeStatusTyp tradeStatusTyp) {
        this.code = code;
        this.tradeStatusTyp = tradeStatusTyp;
    }

    public String getCode() {
        return code;
    }

    public TradeStatusTyp getTradeStatusTyp() {
        return tradeStatusTyp;
    }

    public static TradeJobStatus getByCode(String code) {
        for (TradeJobStatus tradeJobStatus : values()) {
            if (tradeJobStatus.getCode().equals(code)) {
                return tradeJobStatus;
            }
        }

        return null;
    }
}
