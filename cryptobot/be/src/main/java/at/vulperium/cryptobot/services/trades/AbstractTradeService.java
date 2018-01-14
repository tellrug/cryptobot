package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.Trend;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

public abstract class AbstractTradeService<T extends AbstractTradeJobDTO> {

    protected void verarbeiteTradeJob(T tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        //Bestimmen des Trends
        Trend trend = ermittleTrend(tradeJobDTO.getLetztwert() != null ? tradeJobDTO.getLetztwert() : tradeJobDTO.getKaufwert(), wsCryptoCoinDTO.getPrice());

        //Ueberpruefen ob Zielwertueberschritten wurde
        boolean zielErreicht = ermittleZielErreicht(tradeJobDTO, wsCryptoCoinDTO.getPrice());
        TradeJobReaktion tradeJobReaktion = ermittleTradeJobReaktion(tradeJobDTO, zielErreicht, trend);

        //Aktualisieren von Letztwert
        tradeJobDTO.setLetztwert(wsCryptoCoinDTO.getPrice());

        if (TradeJobReaktion.FOLGE_AKTION == tradeJobReaktion) {
            fuehreFolgeaktionDurch(tradeJobDTO);
        }

        //Wenn Abgeschlossen oder Fehler dann soll auch auf erledigt gesetzt werden
        if ((TradeStatus.ABGESCHLOSSEN == tradeJobDTO.getTradeStatus() || TradeStatus.FEHLER == tradeJobDTO.getTradeStatus())
                && tradeJobDTO.getErledigtAm() == null) {
            //Am Schluss wird erledigtAm belegt
            tradeJobDTO.setErledigtAm(LocalDateTime.now());
        }

        //Aktualisieren des TradeJobs (Letztwerte)
        aktualisiereTradeJob(tradeJobDTO);
    }


    protected Trend ermittleTrend(BigDecimal alterWert, BigDecimal neuerWert) {
        BigDecimal aenderung = neuerWert.divide(alterWert, BigDecimal.ROUND_HALF_EVEN);

        if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == 1 && (aenderung.subtract(TradeUtil.getBigDecimal(1.0))).compareTo(Trend.getAenderungssatz()) == 1) {
            //wenn aenderung > 1 && (aenderung-1) > Trend.AENDERUNGSSATZ --> Aufwaertstrend
            return Trend.AUFWAERTS;
        }

        if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == -1 && (TradeUtil.getBigDecimal(1.0).subtract(aenderung)).compareTo(Trend.getAenderungssatz()) == 1) {
            //wenn aenderung < 1 && (1-aenderung) > Trend.AENDERUNGSSATZ --> Abwaertstrend
            return Trend.ABWAERTS;
        }

        //sonst Gleichbleibend
        return Trend.KONSTANT;
    }

    protected abstract boolean ermittleZielErreicht(T tradeJobDTO, BigDecimal aktuellerKurs);


    protected abstract TradeJobReaktion ermittleTradeJobReaktion(T tradeJobDTO, boolean zielErreicht, Trend trend);

    protected abstract void aktualisiereTradeJob(T tradeJobDTO);

    protected abstract TradeStatus fuehreFolgeaktionDurch(T tradeJobDTO);
}
