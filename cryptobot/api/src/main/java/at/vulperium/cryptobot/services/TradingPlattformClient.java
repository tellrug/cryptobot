package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface TradingPlattformClient {

    boolean ping();

    Map<String, WSCryptoCoinDTO> ermittleLetztePreiseMap();

    HoldingDTO ermittleHoldingInformationen();

    boolean storniereOrder(TradeAktionDTO tradeAktionDTO);

    boolean erstelleOrder(TradeAktionDTO tradeAktionDTO);


}
