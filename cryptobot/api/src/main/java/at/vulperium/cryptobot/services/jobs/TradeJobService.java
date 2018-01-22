package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by Ace on 26.12.2017.
 */
public interface TradeJobService {

    SimpelTradeJobDTO holeTradeJob(Long id);

    List<SimpelTradeJobDTO> holeAlleTradeJobs();

    List<SimpelTradeJobDTO> filterTradeJobDTOList(List<SimpelTradeJobDTO> simpelTradeJobDTOList, TradeTyp tradeTyp);

    List<SimpelTradeJobDTO> filterTradeJobDTOList(List<SimpelTradeJobDTO> simpelTradeJobDTOList, boolean erledigt);

    List<SimpelTradeJobDTO> filterTradeJobDTOList(List<SimpelTradeJobDTO> simpelTradeJobDTOList, TradingPlattform tradingPlattform);

    Long speichereTradeJob(SimpelTradeJobDTO simpelTradeJobDTO);

    boolean aktualisiereTradeJob(SimpelTradeJobDTO simpelTradeJobDTO);

    boolean erledigeTradeJob(Long tradeJobId);
}
