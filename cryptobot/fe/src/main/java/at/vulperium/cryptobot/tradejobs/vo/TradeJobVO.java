package at.vulperium.cryptobot.tradejobs.vo;

import at.vulperium.cryptobot.dtos.TradeJobDTO;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class TradeJobVO implements Serializable {

    private TradeJobDTO tradeJobDTO;

    public TradeJobVO(TradeJobDTO tradeJobDTO) {
        this.tradeJobDTO = tradeJobDTO;
    }

    public TradeJobDTO getTradeJobDTO() {
        return tradeJobDTO;
    }

    public void setTradeJobDTO(TradeJobDTO tradeJobDTO) {
        this.tradeJobDTO = tradeJobDTO;
    }
}
