package at.vulperium.cryptobot.tradeaktionen.vo;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.util.Filterable;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public class TradeAktionVO implements Serializable, Filterable{

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


    //FILTER-EIGENSCHAFTEN
    @Override
    public boolean filteringSymbol(String symbol) {
        if (StringUtils.isEmpty(symbol)) {
            return true;
        }
        return tradeAktionDTO.getVonWaehrung().contains(symbol) || tradeAktionDTO.getZuWaehrung().contains(symbol);
    }

    @Override
    public boolean filteringTradingPlattform(TradingPlattform tradingPlattform) {
        if (tradingPlattform == null || tradingPlattform == TradingPlattform.ALLE) {
            return true;
        }
        return tradeAktionDTO.getTradingPlattform() == tradingPlattform;
    }

    @Override
    public boolean filteringTradeTyp(TradeTyp tradeTyp) {
        if (tradeTyp == null) {
            return true;
        }
        return tradeAktionDTO.getTradeTyp() == tradeTyp;
    }
}
