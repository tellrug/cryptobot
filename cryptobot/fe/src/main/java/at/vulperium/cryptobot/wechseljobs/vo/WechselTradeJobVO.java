package at.vulperium.cryptobot.wechseljobs.vo;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.util.Filterable;
import org.apache.commons.lang3.StringUtils;

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
    public boolean filteringSymbol(String symbol) {
        if (StringUtils.isEmpty(symbol)) {
            return true;
        }
        return wechselTradeJobDTO.getCryptoWaehrung().contains(symbol) || wechselTradeJobDTO.getCryptoWaehrungReferenz().contains(symbol);
    }

    @Override
    public boolean filteringTradingPlattform(TradingPlattform tradingPlattform) {
        if (tradingPlattform == null || tradingPlattform == TradingPlattform.ALLE) {
            return true;
        }
        return wechselTradeJobDTO.getTradingPlattform() == tradingPlattform;
    }

    @Override
    public boolean filteringTradeTyp(TradeTyp tradeTyp) {
        if (tradeTyp == null) {
            return true;
        }
        return wechselTradeJobDTO.getTradeTyp() == tradeTyp;
    }
}
