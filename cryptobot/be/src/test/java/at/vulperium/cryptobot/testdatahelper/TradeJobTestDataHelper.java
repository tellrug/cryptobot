package at.vulperium.cryptobot.testdatahelper;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
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

    public TradeJobDTO erzeugeSimpleTradeJobDTO(TradeAktionEnum tradeAktionEnum, TradeStatus tradeStatus) {

        TradeJobDTO tradeJobDTO = erzeugeBasisTradeJobDTO(tradeAktionEnum, tradeStatus);
        if (TradeAktionEnum.KAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.ORDER_KAUF == tradeAktionEnum) {
            if (TradeStatus.ERSTELLT == tradeStatus) {
                tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.003));
                tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.004));
            }
            else if (TradeStatus.BEOBACHTUNG == tradeStatus || TradeStatus.ABGESCHLOSSEN == tradeStatus) {
                tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.003));
                tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.001));
            }

        }
        else if (TradeAktionEnum.VERKAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.ORDER_VERKAUF == tradeAktionEnum) {
            if (TradeStatus.ERSTELLT == tradeStatus) {
                tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.007));
                tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.006));
            }
            else if (TradeStatus.BEOBACHTUNG == tradeStatus || TradeStatus.ABGESCHLOSSEN == tradeStatus) {
                tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.007));
                tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.009));
            }
        }

        tradeJobDTO.setErledigtAm(tradeStatus == TradeStatus.ABGESCHLOSSEN ? LocalDateTime.now(): null);
        tradeJobDTO.setTradeJobTyp(TradeJobTyp.SIMPEL);
        return tradeJobDTO;
    }

    private TradeJobDTO erzeugeBasisTradeJobDTO(TradeAktionEnum tradeAktionEnum, TradeStatus tradeStatus) {
        TradeJobDTO tradeJobDTO = new TradeJobDTO();
        tradeJobDTO.setCryptoWaehrung(SYMBOL);
        tradeJobDTO.setCryptoWaehrungReferenz(SYMBOL_REFERENZ);
        tradeJobDTO.setErstelltAm(LocalDateTime.now());
        tradeJobDTO.setTradeAktionEnum(tradeAktionEnum);
        tradeJobDTO.setTradeStatus(tradeStatus);
        tradeJobDTO.setMenge(TradeUtil.getBigDecimal(10));
        tradeJobDTO.setTradingPlattform(TradingPlattform.BINANCE);

        return tradeJobDTO;
    }
}
