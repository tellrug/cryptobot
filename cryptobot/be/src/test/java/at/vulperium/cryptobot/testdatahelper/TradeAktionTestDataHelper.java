package at.vulperium.cryptobot.testdatahelper;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.joda.time.LocalDateTime;

/**
 * Created by 02ub0400 on 16.01.2018.
 */
public class TradeAktionTestDataHelper {

    public TradeAktionDTO erzeugeSimpleTradeAktionDTO(TradeTyp tradeTyp, TradeStatus tradeStatus) {

        TradeAktionDTO tradeAktionDTO = erzeugeBasisTradeAktionDTO(tradeTyp);
        tradeAktionDTO.setTradeStatus(tradeStatus);
        if (TradeStatus.ABGESCHLOSSEN == tradeStatus || TradeStatus.TRADE_FEHLGESCHLAGEN == tradeStatus) {
            tradeAktionDTO.setErledigtAm(LocalDateTime.now());
        }

        return tradeAktionDTO;
    }

    private TradeAktionDTO erzeugeBasisTradeAktionDTO(TradeTyp tradeTyp) {
        TradeAktionDTO tradeAktionDTO = new TradeAktionDTO();

        tradeAktionDTO.setTradeTyp(tradeTyp);
        tradeAktionDTO.setErstelltAm(LocalDateTime.now());
        tradeAktionDTO.setTradingPlattform(TradingPlattform.BINANCE);

        if (tradeTyp == TradeTyp.KAUF) {
            tradeAktionDTO.setVonWaehrung(TradeJobTestDataHelper.SYMBOL_REFERENZ);
            tradeAktionDTO.setZuWaehrung(TradeJobTestDataHelper.SYMBOL);
            tradeAktionDTO.setVonMenge(TradeUtil.getBigDecimal(1));
            tradeAktionDTO.setZuMenge(TradeUtil.getBigDecimal(1));
            tradeAktionDTO.setPreisProEinheit(TradeUtil.getBigDecimal(1));
        }
        else {
            tradeAktionDTO.setVonMenge(TradeUtil.getBigDecimal(1));
            tradeAktionDTO.setZuMenge(TradeUtil.getBigDecimal(2));
            tradeAktionDTO.setPreisProEinheit(TradeUtil.getBigDecimal("0.5"));
            tradeAktionDTO.setVonWaehrung(TradeJobTestDataHelper.SYMBOL);
            tradeAktionDTO.setZuWaehrung(TradeJobTestDataHelper.SYMBOL_REFERENZ);
        }

        return tradeAktionDTO;
    }
}
