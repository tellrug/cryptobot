package at.vulperium.cryptobot.wechseljobs.vo;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradejobs.vo.FilterVO;
import at.vulperium.cryptobot.util.Filterable;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public class WechselTradeJobVO implements Serializable, Filterable {

    private WechselTradeJobDTO wechselTradeJobDTO;

    public WechselTradeJobVO(WechselTradeJobDTO wechselTradeJobDTO) {
        this.wechselTradeJobDTO = wechselTradeJobDTO;
    }

    public WechselTradeJobDTO getWechselTradeJobDTO() {
        return wechselTradeJobDTO;
    }

    public void setWechselTradeJobDTO(WechselTradeJobDTO wechselTradeJobDTO) {
        this.wechselTradeJobDTO = wechselTradeJobDTO;
    }


    @Override
    public boolean filtering(FilterVO filterVO) {
        if (wechselTradeJobDTO.getErledigtAm() != null && !filterVO.isZeigeAbgeschlossen()) {
            return false;
        }

        if (filterVO.getTradingPlattform() != null && filterVO.getTradingPlattform() != TradingPlattform.ALLE) {
            if (filterVO.getTradingPlattform() != wechselTradeJobDTO.getTradingPlattform()) {
                return false;
            }
        }

        if (filterVO.getTradeTyp() != null && filterVO.getTradeTyp() != wechselTradeJobDTO.getTradeTyp()) {
            return false;
        }

        return true;
    }
}
