package at.vulperium.cryptobot.wechseljobs.service;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.services.trades.WechselTradeJobService;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import at.vulperium.cryptobot.wechseljobs.vo.WechselTradeJobVO;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
@ApplicationScoped
public class WechselTradeJobViewService {

    private @Inject WechselTradeJobService wechselTradeJobService;


    public List<WechselTradeJobVO> holeAlleWechselTradejobVOs() {
        List<WechselTradeJobVO> tradeJobVOList = new ArrayList<>();
        List<WechselTradeJobDTO> wechselTradeJobDTOList = wechselTradeJobService.holeAlleWechselTradeJobs();

        for (WechselTradeJobDTO wechselTradeJobDTO : wechselTradeJobDTOList) {
            tradeJobVOList.add(new WechselTradeJobVO(wechselTradeJobDTO));
        }
        return tradeJobVOList;
    }

    public boolean erstelleNeuenTradeJob(WechselTradeJobVO wechselTradeJobVO) {
        if (wechselTradeJobVO.getWechselTradeJobDTO().getId() != null) {
            return false;
        }
        wechselTradeJobService.speicherNeuenWechselTradeJob(wechselTradeJobVO.getWechselTradeJobDTO());
        return true;
    }

    public boolean bearbeiteTradeJob(WechselTradeJobVO wechselTradeJobVO) {
        if (wechselTradeJobVO.getWechselTradeJobDTO().getId() == null) {
            return false;
        }

        if (wechselTradeJobVO.getWechselTradeJobDTO().getTradeStatus() == TradeStatus.ABGESCHLOSSEN) {
            wechselTradeJobVO.getWechselTradeJobDTO().setErledigtAm(LocalDateTime.now());
        }
        else {
            wechselTradeJobVO.getWechselTradeJobDTO().setErledigtAm(null);
        }
        return wechselTradeJobService.aktualisiereWechselTradeJob(wechselTradeJobVO.getWechselTradeJobDTO());
    }
}
