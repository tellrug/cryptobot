package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.OrderStatus;

public class OrderDTO {

    private String symbolPair;
    private String clientOrderId;
    private Long tradeAktionId;

    private OrderStatus orderStatus;

    public String getSymbolPair() {
        return symbolPair;
    }

    public void setSymbolPair(String symbolPair) {
        this.symbolPair = symbolPair;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public Long getTradeAktionId() {
        return tradeAktionId;
    }

    public void setTradeAktionId(Long tradeAktionId) {
        this.tradeAktionId = tradeAktionId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
