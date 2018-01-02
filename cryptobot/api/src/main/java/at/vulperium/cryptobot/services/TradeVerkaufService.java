package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public interface TradeVerkaufService {

    void verarbeiteVerkaufAktion(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO);
}
