package at.vulperium.cryptobot.testdatahelper;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
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
    public static final String BTC = "BTC";
    public static final String WJC_KAUF = "WJCK";
    public static final String WJC_VERKAUF = "WJCV";

    public SimpelTradeJobDTO erzeugeSimpleTradeJobDTO(TradeAktionEnum tradeAktionEnum, TradeStatus tradeStatus) {

        SimpelTradeJobDTO simpelTradeJobDTO = erzeugeBasisTradeJobDTO(tradeAktionEnum, tradeStatus);
        if (TradeAktionEnum.KAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.ORDER_KAUF == tradeAktionEnum) {
            if (TradeStatus.ERSTELLT == tradeStatus) {
                simpelTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                simpelTradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.003));
                simpelTradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.004));
            }
            else if (TradeStatus.BEOBACHTUNG == tradeStatus || TradeStatus.ABGESCHLOSSEN == tradeStatus) {
                simpelTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                simpelTradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.003));
                simpelTradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.001));
                simpelTradeJobDTO.setSpitzenwert(TradeUtil.getBigDecimal(0.001));
            }

        }
        else if (TradeAktionEnum.VERKAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.ORDER_VERKAUF == tradeAktionEnum) {
            if (TradeStatus.ERSTELLT == tradeStatus) {
                simpelTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                simpelTradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.007));
                simpelTradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.006));
            }
            else if (TradeStatus.BEOBACHTUNG == tradeStatus || TradeStatus.ABGESCHLOSSEN == tradeStatus) {
                simpelTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.005));
                simpelTradeJobDTO.setZielwert(TradeUtil.getBigDecimal(0.007));
                simpelTradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.009));
                simpelTradeJobDTO.setSpitzenwert(TradeUtil.getBigDecimal(0.009));
            }
        }

        simpelTradeJobDTO.setErledigtAm(tradeStatus == TradeStatus.ABGESCHLOSSEN ? LocalDateTime.now() : null);
        simpelTradeJobDTO.setTradeJobTyp(TradeJobTyp.SIMPEL);
        return simpelTradeJobDTO;
    }

    public WechselTradeJobDTO erzeugeWechselTradeJobDTO(TradeAktionEnum tradeAktionEnum) {
        WechselTradeJobDTO wechselTradeJobDTO = erzeugeBasisWechselTradeJobDTO(tradeAktionEnum, TradeStatus.ERSTELLT);
        if (TradeAktionEnum.KAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.ORDER_KAUF == tradeAktionEnum) {
            wechselTradeJobDTO.setTradeTyp(TradeTyp.KAUF);
            wechselTradeJobDTO.setCryptoWaehrung(WJC_KAUF);
        }
        else if (TradeAktionEnum.VERKAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.ORDER_VERKAUF == tradeAktionEnum) {
            wechselTradeJobDTO.setTradeTyp(TradeTyp.VERKAUF);
            wechselTradeJobDTO.setCryptoWaehrung(WJC_VERKAUF);
        }

        wechselTradeJobDTO.setMinimalZielSatz(TradeUtil.getBigDecimal(2));
        wechselTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(0.7)); //entspricht eigentlich aktuellenWert bei Erstellung des Jobs
        wechselTradeJobDTO.setKaufwertGrenze(TradeUtil.getBigDecimal(0.5));
        wechselTradeJobDTO.setMengeReferenzwert(TradeUtil.getBigDecimal(0.02));

        wechselTradeJobDTO.setTradeJobTyp(TradeJobTyp.WECHSEL);
        return wechselTradeJobDTO;
    }

    private WechselTradeJobDTO erzeugeBasisWechselTradeJobDTO(TradeAktionEnum tradeAktionEnum, TradeStatus tradeStatus) {
        WechselTradeJobDTO wechselTradeJobDTO = new WechselTradeJobDTO();
        wechselTradeJobDTO.setTradeAktionEnum(tradeAktionEnum);
        wechselTradeJobDTO.setTradeStatus(tradeStatus);
        wechselTradeJobDTO.setCryptoWaehrungReferenz(BTC);
        wechselTradeJobDTO.setErstelltAm(LocalDateTime.now());

        wechselTradeJobDTO.setTradingPlattform(TradingPlattform.BINANCE);
        wechselTradeJobDTO.setGanzZahlig(false);
        return wechselTradeJobDTO;
    }

    private SimpelTradeJobDTO erzeugeBasisTradeJobDTO(TradeAktionEnum tradeAktionEnum, TradeStatus tradeStatus) {
        SimpelTradeJobDTO simpelTradeJobDTO = new SimpelTradeJobDTO();
        simpelTradeJobDTO.setCryptoWaehrung(SYMBOL);
        simpelTradeJobDTO.setCryptoWaehrungReferenz(SYMBOL_REFERENZ);
        simpelTradeJobDTO.setErstelltAm(LocalDateTime.now());
        simpelTradeJobDTO.setTradeAktionEnum(tradeAktionEnum);
        simpelTradeJobDTO.setTradeStatus(tradeStatus);
        simpelTradeJobDTO.setMenge(TradeUtil.getBigDecimal(10));
        simpelTradeJobDTO.setTradingPlattform(TradingPlattform.BINANCE);
        simpelTradeJobDTO.setGanzZahlig(false);

        return simpelTradeJobDTO;
    }
}
