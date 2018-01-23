package at.vulperium.cryptobot.tradejobs.vo;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.util.Filterable;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class TradeJobVO implements Serializable, Filterable {

    private SimpelTradeJobDTO simpelTradeJobDTO;

    public TradeJobVO(SimpelTradeJobDTO simpelTradeJobDTO) {
        this.simpelTradeJobDTO = simpelTradeJobDTO;
    }

    public SimpelTradeJobDTO getSimpelTradeJobDTO() {
        return simpelTradeJobDTO;
    }

    public void setSimpelTradeJobDTO(SimpelTradeJobDTO simpelTradeJobDTO) {
        this.simpelTradeJobDTO = simpelTradeJobDTO;
    }


    @Override
    public boolean filtering(FilterVO filterVO) {
        if (simpelTradeJobDTO.getErledigtAm() != null && !filterVO.isZeigeAbgeschlossen()) {
            return false;
        }

        if (filterVO.getTradingPlattform() != null && filterVO.getTradingPlattform() != TradingPlattform.ALLE) {
            if (filterVO.getTradingPlattform() != simpelTradeJobDTO.getTradingPlattform()) {
                return false;
            }
        }

        if (filterVO.getTradeTyp() != null && filterVO.getTradeTyp() != simpelTradeJobDTO.getTradeAktionEnum().getTradeTyp()) {
            return false;
        }

        return true;
    }
}
