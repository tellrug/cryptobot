package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.HoldingOrderDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;

import java.util.List;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface TradingPlattformClient {

    boolean ping();

    List<WSCryptoCoinDTO> ermittleLetztePreise();

    HoldingOrderDTO ermittleHoldingOrderInformationen();
}
