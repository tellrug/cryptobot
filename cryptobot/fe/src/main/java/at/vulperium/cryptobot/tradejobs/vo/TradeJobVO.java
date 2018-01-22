package at.vulperium.cryptobot.tradejobs.vo;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class TradeJobVO implements Serializable {

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
}
