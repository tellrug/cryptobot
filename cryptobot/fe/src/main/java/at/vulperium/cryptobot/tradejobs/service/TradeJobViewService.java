package at.vulperium.cryptobot.tradejobs.service;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 04.01.2018.
 */
@ApplicationScoped
public class TradeJobViewService {

    private @Inject TradeJobService tradeJobService;

    public List<TradeJobVO> holeAlleTradejobVOs() {
        List<TradeJobVO> tradeJobVOList = new ArrayList<>();
        List<TradeJobDTO> tradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        tradeJobVOList.addAll(tradeJobDTOList.stream().map(TradeJobVO::new).collect(Collectors.toList()));
        return tradeJobVOList;
    }

    public boolean erstelleNeuenTradeJob(TradeJobVO neuerTradeJobVO) {
        if (neuerTradeJobVO.getTradeJobDTO().getId() != null) {
            return false;
        }

        Long id = tradeJobService.speichereTradeJob(neuerTradeJobVO.getTradeJobDTO());
        if (neuerTradeJobVO.getTradeJobDTO().getId() == null) {
            neuerTradeJobVO.getTradeJobDTO().setId(id);
        }

        return true;
    }

    public boolean bearbeiteTradeJob(TradeJobVO tradeJobVO) {
        if (tradeJobVO.getTradeJobDTO().getId() == null) {
            return false;
        }

        if (tradeJobVO.getTradeJobDTO().getTradeAktionEnum() == TradeAktionEnum.KAUF_ZIEL ||
                tradeJobVO.getTradeJobDTO().getTradeAktionEnum() == TradeAktionEnum.VERKAUF_ZIEL) {
            tradeJobVO.getTradeJobDTO().setErledigtAm(null);
        }
        else {
            tradeJobVO.getTradeJobDTO().setErledigtAm(LocalDateTime.now());
        }


        return tradeJobService.aktualisiereTradeJob(tradeJobVO.getTradeJobDTO());
    }
}
