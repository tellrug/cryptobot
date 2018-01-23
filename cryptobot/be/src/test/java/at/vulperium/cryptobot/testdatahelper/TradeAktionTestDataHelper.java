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
        tradeAktionDTO.setCryptoWaehrung(TradeJobTestDataHelper.SYMBOL);
        tradeAktionDTO.setCryptoWaehrungReferenz(TradeJobTestDataHelper.SYMBOL_REFERENZ);
        tradeAktionDTO.setMenge(TradeUtil.getBigDecimal(1));

        if (tradeTyp == TradeTyp.KAUF) {
            tradeAktionDTO.setPreisProEinheit(TradeUtil.getBigDecimal(1));
        }
        else {
            tradeAktionDTO.setPreisProEinheit(TradeUtil.getBigDecimal("0.5"));
        }

        return tradeAktionDTO;
    }
}
