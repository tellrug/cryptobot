package at.vulperium.cryptobot.observer;

import at.vulperium.cryptobot.events.TradeVerarbeitungEvent;
import at.vulperium.cryptobot.services.WaehrungService;
import at.vulperium.cryptobot.services.jobs.TradeJobVerwaltungService;
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

    private @Inject TradeJobVerwaltungService tradeJobVerwaltungService;
    private @Inject WaehrungService waehrungService;

    public void observeDNSCheckEvent(@Observes TradeVerarbeitungEvent tradeVerarbeitungEvent) {
        logger.info("TradeVerarbeitungEvent von {} empfangen. Manueller-Check={}", tradeVerarbeitungEvent.getStartVerarbeitung(), tradeVerarbeitungEvent.isManuellerCheck());

        //TODO Abspeichern von Information der zueletzt angestossenen Verarbeitung

        //Verarbeiten der offenen Tradeaufgaben
        tradeJobVerwaltungService.verarbeiteTradeAufgaben();
        //List<WaehrungKurzDTO> cL =waehrungService.holeAlleCryptoWaehrungen();
        //logger.info("Anzahl der CRYPTOWAEHRUNGEN = {}", cL.size());
    }
}
