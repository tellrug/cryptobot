package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by 02ub0400 on 15.01.2018.
 */
public interface TradeAktionService {

    Long speichereTradeAktion(TradeAktionDTO tradeAktionDTO);

    boolean aktualisiereTradeAktion(TradeAktionDTO tradeAktionDTO);

    List<TradeAktionDTO> holeAlleTradeAktionen();

    TradeAktionDTO holeTradeAktion(Long tradeAktionId);

    List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, TradeTyp tradeTyp);

    List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, boolean erledigt);

    List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, TradingPlattform tradingPlattform);

    List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, Long jobId);
}
