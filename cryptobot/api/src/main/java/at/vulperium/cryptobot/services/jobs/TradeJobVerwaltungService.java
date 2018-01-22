package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

public interface TradeJobVerwaltungService {

    void verarbeiteBeobachtungsAufgaben();

    void verarbeiteBeobachtungsAufgaben(List<SimpelTradeJobDTO> simpelTradeJobDTOList, List<WechselTradeJobDTO> wechselTradeJobDTOList, TradingPlattform tradingPlattform);

    void aktualisiereTradeJob(TradeAktionDTO tradeAktionDTO);
}
