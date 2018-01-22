package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public interface TradeKaufService {

    void verarbeiteKaufAktion(SimpelTradeJobDTO simpelTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO);

    void aktualisiereTradeJobNachTradeAktion(SimpelTradeJobDTO simpelTradeJobDTO, TradeAktionDTO tradeAktionDTO);
}
