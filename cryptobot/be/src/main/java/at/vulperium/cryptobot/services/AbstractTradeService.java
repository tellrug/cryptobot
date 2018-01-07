package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.Trend;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;
import java.math.BigDecimal;

public abstract class AbstractTradeService {

    private @Inject TradeJobService tradeJobService;
    private @Inject BenachrichtigungService benachrichtigungService;

    protected TradeJobReaktion verarbeiteTradeJobUndErmittleBenachrichtigung(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        //Bestimmen des Trends
        Trend trend = ermittleTrend(tradeJobDTO.getLetztwert() != null ? tradeJobDTO.getLetztwert() : tradeJobDTO.getKaufwert(), wsCryptoCoinDTO.getPrice());

        //Ueberpruefen ob Zielwertueberschritten wurde
        boolean zielErreicht = ermittleZielErreicht(tradeJobDTO, wsCryptoCoinDTO.getPrice());
        TradeJobStatus neuerTradeJobStatus = ermittleNeuenStatusWennErledigt(tradeJobDTO.getId(), zielErreicht, trend);

        //Aktualisieren von Letztwert
        tradeJobDTO.setLetztwert(wsCryptoCoinDTO.getPrice());

        TradeJobReaktion tradeJobReaktion = TradeJobReaktion.WARTEN;
        //Durchfuehren von Aktion
        if(neuerTradeJobStatus != null) {
            //Verkaufsaktion durchfuehren & setzen von neuen Status
            //TODO Aktion automatisch durchfuehren
            tradeJobDTO.setErledigtAm(LocalDateTime.now());
            tradeJobDTO.setTradeJobStatus(neuerTradeJobStatus);
            tradeJobReaktion = TradeJobReaktion.NEUER_TRADESTATUS;
        }

        tradeJobService.aktualisiereTradeJob(tradeJobDTO);

        return tradeJobReaktion;
    }


    protected Trend ermittleTrend(BigDecimal alterWert, BigDecimal neuerWert) {
        BigDecimal aenderung = neuerWert.divide(alterWert, BigDecimal.ROUND_HALF_EVEN);

        if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == 1 && (aenderung.subtract(TradeUtil.getBigDecimal(1.0))).compareTo(Trend.getAenderungssatz()) == 1){
            //wenn aenderung > 1 && (aenderung-1) > Trend.AENDERUNGSSATZ --> Aufwaertstrend
            return Trend.AUFWAERTS;
        }

        if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == -1 && (TradeUtil.getBigDecimal(1.0).subtract(aenderung)).compareTo(Trend.getAenderungssatz()) == 1){
            //wenn aenderung < 1 && (1-aenderung) > Trend.AENDERUNGSSATZ --> Abwaertstrend
            return Trend.ABWAERTS;
        }

        //sonst Gleichbleibend
        return Trend.KONSTANT;
    }

    protected abstract boolean ermittleZielErreicht(TradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs);


    protected abstract TradeJobStatus ermittleNeuenStatusWennErledigt(Long tradeAktionId, boolean zielErreicht, Trend trend);
}
