package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by 02ub0400 on 16.01.2018.
 */
public interface TradeAktionVerwaltungService {

    void verarbeiteTradeAktionAufgaben();

    void verarbeiteTradeAktionAufgaben(List<TradeAktionDTO> tradeAktionDTOList, TradingPlattform tradingPlattform);

    boolean fuehreTradeAktionDurch(TradeAktionDTO tradeAktionDTO);
}
