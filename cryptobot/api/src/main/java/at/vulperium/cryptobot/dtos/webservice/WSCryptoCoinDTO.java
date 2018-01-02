package at.vulperium.cryptobot.dtos.webservice;

import java.math.BigDecimal;

public class WSCryptoCoinDTO {

    private String symbol;
    private BigDecimal price;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
