package at.vulperium.cryptobot.testdatahelper;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradeBasisStatus;
import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
@ApplicationScoped
public class TradeJobTestDataHelper {

    public static final String SYMBOL = "TTT";
    public static final String SYMBOL_REFERENZ = "BTC";

    public TradeJobDTO erzeugeTradeJobDTOVerkauf() {
        return erzeugeTradeJobDTOVerkauf(false);
    }

    public TradeJobDTO erzeugeTradeJobDTOVerkauf(boolean zielErreicht) {
        TradeJobDTO tradeJobDTO = erzeugeTradeJobDTO();

        if (zielErreicht) {
            tradeJobDTO.setTradeJobStatus(TradeJobStatus.VERKAUF_ZIEL_ERREICHT);
            tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.008));
            tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.009));
        }
        else {
            tradeJobDTO.setTradeJobStatus(TradeJobStatus.VERKAUF_ZIEL);
            tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.008));
            tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.006));
        }

        return tradeJobDTO;
    }

    public TradeJobDTO erzeugeTradeJobDTOKauf(boolean zielErreicht) {
        TradeJobDTO tradeJobDTO = erzeugeTradeJobDTO();

        if (zielErreicht) {
            tradeJobDTO.setTradeJobStatus(TradeJobStatus.KAUF_ZIEL_ERREICHT);
            tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.004));
            tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.003));
        }
        else {
            tradeJobDTO.setTradeJobStatus(TradeJobStatus.KAUF_ZIEL);
            tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.003));
            tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.004));
        }

        return tradeJobDTO;
    }

    private TradeJobDTO erzeugeTradeJobDTO() {
        TradeJobDTO tradeJobDTO = new TradeJobDTO();
        tradeJobDTO.setCryptoWaehrung(SYMBOL);
        tradeJobDTO.setCryptoWaehrungReferenz(SYMBOL_REFERENZ);
        tradeJobDTO.setErstelltAm(LocalDateTime.now());
        tradeJobDTO.setTradeBasisStatus(TradeBasisStatus.ERSTELLT);
        tradeJobDTO.setMenge(TradeUtil.getBigDecimal(10));
        tradeJobDTO.setTradingPlattform(TradingPlattform.BINANCE);
        tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));

        return tradeJobDTO;
    }
}
