package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.OrderDTO;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface BinanceClientService extends TradingPlattformClient {

    OrderDTO holeOrderInfosByClientOrderId(String symbolPair, String clientOrderId);
}
