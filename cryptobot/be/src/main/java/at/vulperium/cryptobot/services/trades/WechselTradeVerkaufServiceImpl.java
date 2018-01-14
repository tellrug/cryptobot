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
 * Created by Ace on 14.01.2018.
 */
@ApplicationScoped
public class WechselTradeVerkaufServiceImpl extends AbstractTradeService<WechselTradeJobDTO> implements WechselTradeVerkaufService {

    public static final Logger logger = LoggerFactory.getLogger(WechselTradeVerkaufServiceImpl.class);

    private @Inject WechselTradeJobService wechselTradeJobService;

    @Override
    public void verarbeiteWechselJobVerkauf(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(wechselTradeJobDTO, "wechselTradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (wechselTradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT || wechselTradeJobDTO.getTradeStatus() == TradeStatus.BEOBACHTUNG) {
            //Job wurde erstellt  bzw. es wird auf Verkaufmoment gewartet
            //Ueberpruefen ob Verkaufmoment eingetreten ist
            verarbeiteTradeJob(wechselTradeJobDTO, wsCryptoCoinDTO);
        }

        //TradeStatus koennte sich veraendert haben --> deswegen wird hier gleich die naechste Aufgabe durchgefuehrt
        if (wechselTradeJobDTO.getTradeStatus() == TradeStatus.TRADE_PRUEFUNG_VERKAUF) {
            //Ueberpruefen ob Order gestellt wurde
            ueberpruefeVerkaufTrade(wechselTradeJobDTO);
        }
    }

    @Override
    protected boolean ermittleZielErreicht(WechselTradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei VERKAUF ist ziel erreicht wenn der aktuelle Kurs ueber dem Zielkurs liegt
        return aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 1 || aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 0;
    }

    @Override
    protected TradeJobReaktion ermittleTradeJobReaktion(WechselTradeJobDTO tradeJobDTO, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.ABWAERTS) {
                //Trend ist nun fallend --> Verkauf
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, Kurs faellt wieder. Es kann verkauft werden", tradeJobDTO.getId());
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_VERKAUF);
                return TradeJobReaktion.FOLGE_AKTION;
            }
            else {
                //Trend ist konstant oder aufwaerts
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, jedoch wird mit VERKAUF noch gewartet.", tradeJobDTO.getId());
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
        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_VERKAUF && tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_VERKAUF) {
            //Verkauf-Order erstellen
            //TODO Aufruf von Service
            boolean orderErfolgreichAbgesetzt = true;
            if (orderErfolgreichAbgesetzt) {
                //Verkauf-Informationen setzen
                //tradeJobDTO.setVorgesehenerVerkaufwert();

                tradeJobDTO.setTradeVersuchAm(LocalDateTime.now());
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_VERKAUF);
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

    private void ueberpruefeVerkaufTrade(WechselTradeJobDTO tradeJobDTO) {
        /*
        Es werden offene Orders betrachtet. Ueberpruefen ob relevante offene Order vorhanden ist:
        *Einheit und ReferenzEinheit
        *Menge
        *vorgesehenerVerkaufwert aus offenen Order vergleichen (VERKAUF)

        Keine relevante Order vorhanden: Verauf war erfolgreich --> Kaufwert wird auf 0 gesetzt, Zielwert wird auf 0 gesetzt, vorgesehenerVerkaufwert wird auf 0 gesetzt,
        menge wird auf 0 gesetzt, tradeVersuchAm auf null gesetzt, Status umstellen auf BEOBACHTEN, tradeTyp wird auf KAUF gesetzt --> TradeAktion speichern
        Relevanter Order vorhanden: Verkauf hat nicht funktioniert --> ?????
        */

        boolean verkaufErfolgreich = true;
        if (verkaufErfolgreich) {
            tradeJobDTO.setKaufwert(null);
            tradeJobDTO.setMenge(null);
            tradeJobDTO.setZielwert(null);
            tradeJobDTO.setVorgesehenerVerkaufwert(null);

            tradeJobDTO.setTradeVersuchAm(null);

            tradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
            tradeJobDTO.setTradeTyp(TradeTyp.KAUF);

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(tradeJobDTO);

            //speichern von TradeAktion
            //TODO speichern von TradeAktion

            logger.info("Verkauf erfolgreich. Der TradeTyp von WechselTradeJob mit wechselTradeJobId={} wurde umgesetzt", tradeJobDTO.getId(), tradeJobDTO.getTradeTyp());
        }
        else {
            logger.warn("Keinen Kauefer von {} bei WechselTradeJob mit wechselTradeJobId={} gefunden.", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());
            //TODO Trade stornieren? alles wieder zuruecksetzen so dass neue Verkauf-Order erstellt wird
        }
    }
}
