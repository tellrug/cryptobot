package at.vulperium.cryptobot.tradeaktionen.service;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.services.trades.TradeAktionService;
import at.vulperium.cryptobot.tradeaktionen.vo.TradeAktionVO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
@ApplicationScoped
public class TradeAktionViewService {

    private @Inject TradeAktionService tradeAktionService;

    public List<TradeAktionVO> holeAlleTradeAktionVOs() {
        List<TradeAktionVO> tradeAktionVOList = new ArrayList<>();
        List<TradeAktionDTO> tradeAktionDTOList = tradeAktionService.holeAlleTradeAktionen();
        tradeAktionVOList.addAll(tradeAktionDTOList.stream().map(TradeAktionVO::new).collect(Collectors.toList()));
        return tradeAktionVOList;
    }


    public List<TradeAktionVO> holeTradesFuerJobId(Long jobId) {
        if (jobId == null) {
            return null;
        }
        List<TradeAktionDTO> tradeAktionDTOList = tradeAktionService.holeAlleTradeAktionen();
        List<TradeAktionDTO> relevanteTradeAktionDTOList = tradeAktionService.filterTradeAktionDTOList(tradeAktionDTOList, jobId);

        List<TradeAktionVO> tradeAktionVOList = new ArrayList<>();
        tradeAktionVOList.addAll(relevanteTradeAktionDTOList.stream().map(TradeAktionVO::new).collect(Collectors.toList()));
        return tradeAktionVOList;
    }
}
