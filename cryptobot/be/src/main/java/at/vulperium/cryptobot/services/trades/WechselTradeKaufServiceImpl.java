package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.Trend;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * Created by Ace on 13.01.2018.
 */
@ApplicationScoped
public class WechselTradeKaufServiceImpl extends AbstractTradeService<WechselTradeJobDTO> implements WechselTradeKaufService {

    public static final Logger logger = LoggerFactory.getLogger(WechselTradeKaufServiceImpl.class);

    private @Inject WechselTradeJobService wechselTradeJobService;

    @Override
    public void verarbeiteWechselJobKauf(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(wechselTradeJobDTO, "wechselTradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (wechselTradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT || wechselTradeJobDTO.getTradeStatus() == TradeStatus.BEOBACHTUNG) {
            //Job wurde erstellt  bzw. es wird auf Kaufmoment gewartet
            //Ueberpruefen ob Kaufmoment eingetreten ist
            verarbeiteTradeJob(wechselTradeJobDTO, wsCryptoCoinDTO);
        }

        //TradeStatus koennte sich veraendert haben --> deswegen wird hier gleich die naechste Aufgabe durchgefuehrt
        if (wechselTradeJobDTO.getTradeStatus() == TradeStatus.TRADE_PRUEFUNG_KAUF) {
            //Ueberpruefen ob Order gestellt wurde
            ueberpruefeKaufTrade(wechselTradeJobDTO);
        }
    }


    @Override
    protected boolean ermittleZielErreicht(WechselTradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei KAUF ist das Ziel erreicht wenn der aktuelle Kurs unter der KaufwertGrenze liegt
        return aktuellerKurs.compareTo(tradeJobDTO.getKaufwertGrenze()) == -1 || aktuellerKurs.compareTo(tradeJobDTO.getKaufwertGrenze()) == 0;
    }

    @Override
    protected TradeJobReaktion ermittleTradeJobReaktion(WechselTradeJobDTO tradeJobDTO, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.AUFWAERTS) {
                //Trend ist nun steigend --> Kauf
                logger.info("Kauf-Ziel fuer WechselTrade tradeJobId={} wurde erreicht, Kurs steigt wieder. Es wird gekauft", tradeJobDTO.getId());
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_KAUF);
                return TradeJobReaktion.FOLGE_AKTION;
            }
            else {
                //Trend ist konstant oder abwaerts
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, jedoch wird mit KAUF noch gewartet.", tradeJobDTO.getId());
                //Nichts tun
                tradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
                return TradeJobReaktion.WARTEN;
            }
        }

        //Ziel wurde noch nicht erreicht
        logger.info("Ziel fuer Trade tradeAktionId={} wurde noch nicht erreicht, es wird noch gewartet", tradeJobDTO.getId());
        //Keine Aktion
        return TradeJobReaktion.WARTEN;
    }

    @Override
    protected void aktualisiereTradeJob(WechselTradeJobDTO tradeJobDTO) {
        wechselTradeJobService.aktualisiereWechselTradeJob(tradeJobDTO);
    }

    @Override
    protected TradeStatus fuehreFolgeaktionDurch(WechselTradeJobDTO tradeJobDTO) {
        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_KAUF && tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_KAUF) {
            //Kauf-Order erstellen
            //TODO Aufruf von Service
            boolean orderErfolgreichAbgesetzt = true;
            if (orderErfolgreichAbgesetzt) {
                //Kauf-Informationen setzen
                //tradeJobDTO.setKaufwert();
                //tradeJobDTO.setMenge();

                tradeJobDTO.setTradeVersuchAm(LocalDateTime.now());
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_KAUF);
                return null;
            }
            else {
                //Fehler beim Erstellen des Trades
                tradeJobDTO.setTradeStatus(TradeStatus.FEHLER);
                return null;
            }
        }
        logger.error("Zu TradeJob mit wechselTradeJobId={} konnte keine Folgeaktion durchgefuehrt werden.", tradeJobDTO.getId());
        throw new IllegalStateException("Zu TradeJob mit wechselTradeJobId=" + tradeJobDTO.getId() + " konnte keine Folgeaktion durchgefuehrt werden.");
    }

    private void ueberpruefeKaufTrade(WechselTradeJobDTO tradeJobDTO) {
        /*
        Es werden offene Orders betrachtet.Ueberpruefen ob relevante offene Order vorhanden ist:
        *Einheit und ReferenzEinheit
        * Menge
        * Kaufwert aus offenen Order vergleichen (KAUF)

        Keine relevante Order vorhanden:
        Kauf war erfolgreich-- >
                Zielwert wird gesetzt, tradeVersuchAm auf null gesetzt, Status umstellen auf BEOBACHTEN, tradeTyp
        wird auf VERKAUF gesetzt -- > TradeAktion speichern
        Relevanter Order vorhanden:
        Kauf hat nicht funktioniert -- > ?????
        */

        boolean kaufErfolgreich = true;
        if (kaufErfolgreich) {
            //setzen von Zielwert
            tradeJobDTO.setZielwert(tradeJobDTO.getKaufwert().multiply(tradeJobDTO.getMinimalZielSatz()));

            //Zuruecksetzen von TradeVersuchAm
            tradeJobDTO.setTradeVersuchAm(null);

            tradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
            tradeJobDTO.setTradeTyp(TradeTyp.VERKAUF);

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(tradeJobDTO);

            //speichern von TradeAktion
            //TODO speichern von TradeAktion

            logger.info("Kauf erfolgreich. Der TradeTyp von WechselTradeJob mit wechselTradeJobId={} wurde umgesetzt", tradeJobDTO.getId(), tradeJobDTO.getTradeTyp());
        }
        else {
            logger.warn("Keinen Verkauefer von {} bei WechselTradeJob mit wechselTradeJobId={} gefunden.", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());
            //TODO Trade stornieren? alles wieder zuruecksetzen so dass neue Kauf-Order erstellt wird
        }
    }
}
