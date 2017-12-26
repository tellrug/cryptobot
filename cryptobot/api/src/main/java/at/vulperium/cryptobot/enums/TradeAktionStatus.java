package at.vulperium.cryptobot.enums;

/**
 * Created by Ace on 26.12.2017.
 */
public enum TradeAktionStatus {

    //Es ist ein Kauf vorgesehen sobald gewisse Parameter eingetreten sind
    KAUF_ZIEL("K", TradeBasisStatus.KAUF),
    //Es ist ein Verkauf vorgesehen sobald gewisse Parameter eingetreten sind
    VERKAUF_ZIEL("V", TradeBasisStatus.VERKAUF),
    KAUF_ABGESCHLOSSEN("KA", TradeBasisStatus.KAUF),
    VERKAUF_ABGESCHLOSSEN("VA", TradeBasisStatus.VERKAUF);

    private String code;
    private TradeBasisStatus tradeBasisStatus;

    TradeAktionStatus(String code, TradeBasisStatus tradeBasisStatus) {
        this.code = code;
        this.tradeBasisStatus = tradeBasisStatus;
    }

    public String getCode() {
        return code;
    }

    public TradeBasisStatus getTradeBasisStatus() {
        return tradeBasisStatus;
    }

    public static TradeAktionStatus getByCode(String code) {
        for (TradeAktionStatus tradeAktionStatus : values()) {
            if (tradeAktionStatus.getCode().equals(code)) {
                return tradeAktionStatus;
            }
        }

        return null;
    }
}
