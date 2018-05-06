package at.vulperium.cryptobot.observer;

import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.TimerJobDTO;
import at.vulperium.cryptobot.enums.TimerJobEnum;
import at.vulperium.cryptobot.events.TradeVerarbeitungEvent;
import at.vulperium.cryptobot.services.TimerJobService;
import at.vulperium.cryptobot.services.WaehrungService;
import at.vulperium.cryptobot.services.jobs.TradeJobVerwaltungService;
import at.vulperium.cryptobot.services.trades.TradeAktionVerwaltungService;
import at.vulperium.cryptobot.utils.ConfigUtil;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
@ApplicationScoped
public class TradeVerarbeitungEventObserver {

    private static final Logger logger = LoggerFactory.getLogger(TradeVerarbeitungEventObserver.class);

    private final static ConfigValue intervallTradebeobachtung = new ConfigValue("intervallBeobachtung");
    private final static ConfigValue intervallTradeAktion = new ConfigValue("intervallTradeAktion");

    private @Inject TradeJobVerwaltungService tradeJobVerwaltungService;
    private @Inject TradeAktionVerwaltungService tradeAktionVerwaltungService;
    private @Inject WaehrungService waehrungService;
    private @Inject TimerJobService timerJobService;

    public void observeTradeVerarbeitungEvent(@Observes TradeVerarbeitungEvent tradeVerarbeitungEvent) {
        logger.info("TradeVerarbeitungEvent von {} empfangen. Manueller-Check={}", LocalDateTime.now(), tradeVerarbeitungEvent.isManuellerCheck());

        if (tradeVerarbeitungEvent.getTimerjobId() != null) {

            if (tradeVerarbeitungEvent.getTimerJobEnum() == TimerJobEnum.TRADE_BEOBACHTUNG) {
                //Betrachten der aktuellen Kurse
                tradeJobVerwaltungService.verarbeiteBeobachtungsAufgaben();
            }
            else if (tradeVerarbeitungEvent.getTimerJobEnum() == TimerJobEnum.TRADE_AKTION) {
                //Verarbeiten der offenen Tradeaufgaben
                tradeAktionVerwaltungService.verarbeiteTradeAktionAufgaben();
            }

            //Aktualisieren des TimerJobs
            //Ermitteln der naechsten Durchfuehrung
            LocalDateTime naechsteDurchfuehrungAm = ermittleNaechsteDurchfuehrung(tradeVerarbeitungEvent.getTimerjobId());
            timerJobService.aktualisiereDurchfuehrungInfo(tradeVerarbeitungEvent.getTimerjobId(), naechsteDurchfuehrungAm);
        }
    }


    private LocalDateTime ermittleNaechsteDurchfuehrung(Long timerJobId) {

        LocalDateTime naechsteDurchfuehrungAm = LocalDateTime.now();

        TimerJobDTO timerJobDTO = timerJobService.holeTimerJob(timerJobId);
        if (timerJobDTO != null) {
            Duration intervall;
            if (timerJobDTO.getTimerJobEnum() == TimerJobEnum.TRADE_AKTION) {
                intervall = new Duration(Minutes.minutes(ConfigUtil.toInteger(intervallTradeAktion)).toStandardDuration());
            }
            else if (timerJobDTO.getTimerJobEnum() == TimerJobEnum.TRADE_BEOBACHTUNG) {
                intervall = new Duration(Minutes.minutes(ConfigUtil.toInteger(intervallTradebeobachtung)).toStandardDuration());
            }
            else {
                throw new IllegalStateException("Fehler bei der Ermittlung der naechsten Durchfuehrung von TimerJob=" + timerJobId + ". Timerbezeichnung unbekannt.");
            }

            naechsteDurchfuehrungAm.plus(intervall);
        }
        return naechsteDurchfuehrungAm;
    }

}
