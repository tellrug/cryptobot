package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
public interface WechselTradeJobService {

    Long speicherNeuenWechselTradeJob(WechselTradeJobDTO wechselTradeJobDTO);

    List<WechselTradeJobDTO> holeAlleWechselTradeJobs();

    WechselTradeJobDTO holeWechselTradeJob(Long wechselTradeJobId);

    boolean erledigeWechselTradeJob(Long wechselTradeJobId);

    boolean aktualisiereWechselTradeJob(WechselTradeJobDTO wechselTradeJobDTO);

    List<WechselTradeJobDTO> filterTradeJobDTOList(List<WechselTradeJobDTO> tradeJobDTOList, TradeTyp tradeTyp);

    List<WechselTradeJobDTO> filterTradeJobDTOList(List<WechselTradeJobDTO> tradeJobDTOList, boolean erledigt);

    List<WechselTradeJobDTO> filterTradeJobDTOList(List<WechselTradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform);
}
