package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

public interface TradeJobVerwaltungService {

    void verarbeiteTradeAufgaben();

    void verarbeiteTradeAufgaben(List<TradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform);
}
