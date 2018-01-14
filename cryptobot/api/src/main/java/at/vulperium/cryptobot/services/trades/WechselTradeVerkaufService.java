package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;

/**
 * Created by Ace on 14.01.2018.
 */
public interface WechselTradeVerkaufService {

    void verarbeiteWechselJobVerkauf(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO);
}
