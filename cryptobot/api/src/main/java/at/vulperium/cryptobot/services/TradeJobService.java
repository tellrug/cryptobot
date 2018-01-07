package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradeStatusTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by Ace on 26.12.2017.
 */
public interface TradeJobService {

    TradeJobDTO holeTradeJob(Long id);

    List<TradeJobDTO> holeAlleTradeJobs();

    List<TradeJobDTO> filterTradeJobDTOList(List<TradeJobDTO> tradeJobDTOList, TradeStatusTyp tradeStatusTyp);

    List<TradeJobDTO> filterTradeJobDTOList(List<TradeJobDTO> tradeJobDTOList, boolean erledigt);

    List<TradeJobDTO> filterTradeJobDTOList(List<TradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform);

    Long speichereTradeJob(TradeJobDTO tradeJobDTO);

    boolean aktualisiereTradeJob(TradeJobDTO tradeJobDTO);

    boolean erledigeTradeJob(Long tradeJobId);
}
