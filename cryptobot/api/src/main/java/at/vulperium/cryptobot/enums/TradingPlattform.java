package at.vulperium.cryptobot.enums;

/**
 * Created by Ace on 24.12.2017.
 */
public enum TradingPlattform {
    ALLE("ALLE"),
    BINANCE ("BIC");

    TradingPlattform(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public static TradingPlattform getByCode(String code) {
        for (TradingPlattform tradingPlattform : values()) {
            if (tradingPlattform.getCode().equals(code)) {
                return tradingPlattform;
            }
        }

        return null;
    }
}
