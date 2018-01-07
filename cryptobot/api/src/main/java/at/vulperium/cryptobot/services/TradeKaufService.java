package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobReaktion;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public interface TradeKaufService {

    TradeJobReaktion verarbeiteKaufAktion(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO);
}
