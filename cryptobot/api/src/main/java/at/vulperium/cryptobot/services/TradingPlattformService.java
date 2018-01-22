package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.HoldingOrderDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.Map;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface TradingPlattformService {

    boolean ueberpruefeErreichbarkeit(TradingPlattform tradingPlattform);

    Map<String, WSCryptoCoinDTO> holeWSCryptoCoinMap(TradingPlattform tradingPlattform);

    HoldingOrderDTO ermittleHoldingOrderInformationen(TradingPlattform tradingPlattform);
}
