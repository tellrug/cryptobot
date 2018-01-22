package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.Trend;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
@ApplicationScoped
public class TradeKaufServiceImpl extends AbstractTradeService<SimpelTradeJobDTO> implements TradeKaufService {

    public static final Logger logger = LoggerFactory.getLogger(TradeKaufServiceImpl.class);

    private @Inject TradeJobService tradeJobService;
    private @Inject TradeAktionService tradeAktionService;
    private @Inject TradeAktionVerwaltungService tradeAktionVerwaltungService;

    @Override
    public void verarbeiteKaufAktion(SimpelTradeJobDTO simpelTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(simpelTradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (simpelTradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT || simpelTradeJobDTO.getTradeStatus() == TradeStatus.BEOBACHTUNG) {
            verarbeiteTradeJob(simpelTradeJobDTO, wsCryptoCoinDTO);
        }
    }

    @Override
    protected TradeJobReaktion ermittleTradeJobReaktion(SimpelTradeJobDTO simpelTradeJobDTO, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.AUFWAERTS) {
                //Trend ist nun steigend --> Kauf
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, Kurs steigt wieder. Es kann gekauft werden", simpelTradeJobDTO.getId());

                TradeStatus neuerTradeStatus = simpelTradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_KAUF ? TradeStatus.TRADE_KAUF : TradeStatus.ABGESCHLOSSEN;
                simpelTradeJobDTO.setTradeStatus(neuerTradeStatus);

                return TradeJobReaktion.FOLGE_AKTION;
            }
            else {
                //Trend ist konstant oder abwaerts
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, jedoch wird mit KAUF noch gewartet.", simpelTradeJobDTO.getId());
                //Nichts tun
                simpelTradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
                return TradeJobReaktion.WARTEN;
            }
        }

        //Ziel wurde noch nicht erreicht
        logger.info("Ziel fuer Trade tradeAktionId={} wurde noch nicht erreicht, es wird noch gewartet", simpelTradeJobDTO.getId());
        //Keine Aktion
        return TradeJobReaktion.WARTEN;
    }

    @Override
    protected boolean ermittleZielErreicht(SimpelTradeJobDTO simpelTradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei KAUF ist das Ziel erreicht wenn der aktuelle Kurs unter dem Zielkurs liegt
        return aktuellerKurs.compareTo(simpelTradeJobDTO.getZielwert()) == -1 || aktuellerKurs.compareTo(simpelTradeJobDTO.getZielwert()) == 0;
    }

    @Override
    protected void aktualisiereTradeJob(SimpelTradeJobDTO simpelTradeJobDTO) {
        tradeJobService.aktualisiereTradeJob(simpelTradeJobDTO);
    }

    @Override
    protected TradeStatus fuehreFolgeaktionDurch(SimpelTradeJobDTO simpelTradeJobDTO) {
        if (simpelTradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.KAUF_ZIEL) {
            //momentan keine Folgeaktion
            return null;
        }

        if (simpelTradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_KAUF && simpelTradeJobDTO.getTradeStatus() == TradeStatus.TRADE_KAUF) {
            //Kauf-Order erstellen
            //Erstellen von entsprechender TradeAktion
            TradeAktionDTO kaufTradeAktion = erstelleTradeAktion(simpelTradeJobDTO);

            //Order erstellen
            if (tradeAktionVerwaltungService.fuehreTradeAktionDurch(kaufTradeAktion)) {
                //simpelTradeJobDTO.setTradeVersuchAm(LocalDateTime.now());
                simpelTradeJobDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_KAUF);
            }
            else {
                //Fehler beim Erstellen des Trades
                simpelTradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
            }
            //TODO wird das hier noch benoetigt?
            return null;
        }
        logger.error("Zu TradeJob mit simpelTradeJobId={} konnte keine Folgeaktion durchgefuehrt werden.", simpelTradeJobDTO.getId());
        throw new IllegalStateException("Zu TradeJob mit simpelTradeJobId=" + simpelTradeJobDTO.getId() + " konnte keine Folgeaktion durchgefuehrt werden.");
    }

    public void aktualisiereTradeJobNachTradeAktion(SimpelTradeJobDTO simpelTradeJobDTO, TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(simpelTradeJobDTO, "simpelTradeJobDTO ist null.");
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");

        if (tradeAktionDTO.getTradeStatus() == TradeStatus.ABGESCHLOSSEN) {
            simpelTradeJobDTO.setTradeStatus(TradeStatus.ABGESCHLOSSEN);
            simpelTradeJobDTO.setErledigtAm(LocalDateTime.now());

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(simpelTradeJobDTO);

            logger.info("Kauf von {} erfolgreich. TradeJob mit tradeJobId={} abgeschlossen", simpelTradeJobDTO.getCryptoWaehrung(), simpelTradeJobDTO.getId());
        }
        else if (tradeAktionDTO.getTradeStatus() == TradeStatus.TRADE_FEHLGESCHLAGEN) {
            logger.warn("Keinen Verkauefer von {} bei TradeJob mit tradeJobId={} gefunden.", simpelTradeJobDTO.getCryptoWaehrung(), simpelTradeJobDTO.getId());

            //alles wieder zuruecksetzen so dass neue Kauf-Order erstellt wird
            simpelTradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
            //Aktualisieren von TradeJob
            aktualisiereTradeJob(simpelTradeJobDTO);
        }
    }

    private TradeAktionDTO erstelleTradeAktion(SimpelTradeJobDTO tradeJobDTO) {
        //Es wird eine TradeAktion erstellt: Kauf

        TradeAktionDTO tradeAktionDTO = new TradeAktionDTO();
        tradeAktionDTO.setTradeTyp(TradeTyp.KAUF);
        tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_KAUF);
        tradeAktionDTO.setErstelltAm(LocalDateTime.now());
        tradeAktionDTO.setTradingPlattform(tradeJobDTO.getTradingPlattform());

        tradeAktionDTO.setTradeJobId(tradeJobDTO.getId());
        tradeAktionDTO.setTradeJobTyp(tradeJobDTO.getTradeJobTyp());
        //tradeAktionDTO.setUserId(); //TODO technischen User setzen

        tradeAktionDTO.setVonWaehrung(tradeJobDTO.getCryptoWaehrungReferenz());
        tradeAktionDTO.setZuWaehrung(tradeJobDTO.getCryptoWaehrung());

        //Ermitteln des Preises
        tradeAktionDTO.setPreisProEinheit(ermittleOrderWert(tradeJobDTO));
        //Kauf (BTC) ist nie ganzzahlig

        tradeAktionDTO.setVonMenge(ermittleRelevanteTradeMenge(tradeAktionDTO.getPreisProEinheit().multiply(tradeJobDTO.getMenge()), false));
        //tradeAktionDTO.setZuMenge(); wird das hier benoetigt

        //Speichern der TradeAktion
        Long tradeAktionId = tradeAktionService.speichereTradeAktion(tradeAktionDTO);
        return tradeAktionDTO;
    }

    /*
    private void ueberpruefeKaufTrade(SimpelTradeJobDTO simpelTradeJobDTO) {
        boolean kaufErfolgreich = true;
        if (kaufErfolgreich) {
            simpelTradeJobDTO.setTradeStatus(TradeStatus.ABGESCHLOSSEN);
            simpelTradeJobDTO.setErledigtAm(LocalDateTime.now());

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(simpelTradeJobDTO);

            //speichern von TradeAktion
            //TODO speichern von TradeAktion

            logger.info("Kauf von {} erfolgreich. TradeJob mit tradeJobId={} abgeschlossen", simpelTradeJobDTO.getCryptoWaehrung(), simpelTradeJobDTO.getId());
        }
        else {
            logger.warn("Keinen Verkauefer von {} bei TradeJob mit tradeJobId={} gefunden.", simpelTradeJobDTO.getCryptoWaehrung(), simpelTradeJobDTO.getId());
            //TODO Trade stornieren? alles wieder zuruecksetzen so dass neue Verkauf-Order erstellt wird
        }
    }
    */
}
