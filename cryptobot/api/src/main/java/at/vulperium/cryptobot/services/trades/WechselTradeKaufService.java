package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;

/**
 * Created by Ace on 14.01.2018.
 */
public interface WechselTradeKaufService {

    void verarbeiteWechselJobKauf(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO);

    void aktualisiereTradeJobNachTradeAktion(WechselTradeJobDTO tradeJobDTO, TradeAktionDTO tradeAktionDTO);
}
