package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.OrderDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.Map;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface TradingPlattformService {

    boolean ueberpruefeErreichbarkeit(TradingPlattform tradingPlattform);

    Map<String, WSCryptoCoinDTO> holeWSCryptoCoinMap(TradingPlattform tradingPlattform);

    HoldingDTO ermittleHoldingInformationen(TradingPlattform tradingPlattform);

    OrderDTO holeOrderDTOzuTradeAktion(TradeAktionDTO tradeAktionDTO);

    boolean storniereOrder(TradeAktionDTO tradeAktionDTO);

    boolean erstelleOrder(TradeAktionDTO tradeAktionDTO);
}
