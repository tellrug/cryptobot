package at.vulperium.cryptobot.tradeaktionen.vo;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradejobs.vo.FilterVO;
import at.vulperium.cryptobot.util.Filterable;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public class TradeAktionVO implements Serializable, Filterable {

    private TradeAktionDTO tradeAktionDTO;

    public TradeAktionVO(TradeAktionDTO tradeAktionDTO) {
        this.tradeAktionDTO = tradeAktionDTO;
    }

    public TradeAktionDTO getTradeAktionDTO() {
        return tradeAktionDTO;
    }

    public void setTradeAktionDTO(TradeAktionDTO tradeAktionDTO) {
        this.tradeAktionDTO = tradeAktionDTO;
    }

    @Override
    public boolean filtering(FilterVO filterVO) {
        if (tradeAktionDTO.getErledigtAm() != null && !filterVO.isZeigeAbgeschlossen()) {
            return false;
        }

        if (filterVO.getTradingPlattform() != null && filterVO.getTradingPlattform() != TradingPlattform.ALLE) {
            if (filterVO.getTradingPlattform() != tradeAktionDTO.getTradingPlattform()) {
                return false;
            }
        }

        if (filterVO.getTradeTyp() != null && filterVO.getTradeTyp() != tradeAktionDTO.getTradeTyp()) {
            return false;
        }

        return true;
    }
}
