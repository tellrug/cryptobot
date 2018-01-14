package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

public interface TradeJobVerwaltungService {

    void verarbeiteTradeAufgaben();

    void verarbeiteTradeAufgaben(List<TradeJobDTO> tradeJobDTOList, List<WechselTradeJobDTO> wechselTradeJobDTOList, TradingPlattform tradingPlattform);

}
