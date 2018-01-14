package at.vulperium.cryptobot.enums;

/**
 * Created by Ace on 24.12.2017.
 */
public enum TradingPlattform {
    ALLE("ALLE", null),
    BINANCE ("BIC", "icons/bnb.svg");

    TradingPlattform(String code, String iconRes) {
        this.iconRes = iconRes;
        this.code = code;
    }

    private String code;
    private String iconRes;

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

    public String getIconRes() {
        return iconRes;
    }
}
