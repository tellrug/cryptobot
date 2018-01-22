package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
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
    private @Inject TradeAktionService tradeAktionService;
    private @Inject TradeAktionVerwaltungService tradeAktionVerwaltungService;

    @Override
    public void verarbeiteWechselJobVerkauf(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(wechselTradeJobDTO, "wechselTradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (wechselTradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT || wechselTradeJobDTO.getTradeStatus() == TradeStatus.BEOBACHTUNG) {
            //Job wurde erstellt  bzw. es wird auf Verkaufmoment gewartet
            //Ueberpruefen ob Verkaufmoment eingetreten ist
            verarbeiteTradeJob(wechselTradeJobDTO, wsCryptoCoinDTO);
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

    protected void aktualisiereTradeJob(WechselTradeJobDTO tradeJobDTO) {
        wechselTradeJobService.aktualisiereWechselTradeJob(tradeJobDTO);
    }

    @Override
    protected TradeStatus fuehreFolgeaktionDurch(WechselTradeJobDTO tradeJobDTO) {
        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_VERKAUF && tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_VERKAUF) {
            //Verkauf-Order erstellen

            //Erstellen von entsprechender TradeAktion
            TradeAktionDTO verkaufTradeAktion = erstelleTradeAktion(tradeJobDTO);

            //Order erstellen
            if (tradeAktionVerwaltungService.fuehreTradeAktionDurch(verkaufTradeAktion)) {
                tradeJobDTO.setVorgesehenerVerkaufwert(verkaufTradeAktion.getPreisProEinheit());
                tradeJobDTO.setTradeVersuchAm(LocalDateTime.now());
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_VERKAUF);
            }
            else {
                //Fehler beim Erstellen des Trades
                tradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
            }
            //TODO wird das hier noch benoetigt?
            return null;
        }
        logger.error("Zu TradeJob mit wechselTradeJobId={} konnte keine Folgeaktion durchgefuehrt werden.", tradeJobDTO.getId());
        throw new IllegalStateException("Zu TradeJob mit wechselTradeJobId=" + tradeJobDTO.getId() + " konnte keine Folgeaktion durchgefuehrt werden.");
    }

    @Override
    public void aktualisiereTradeJobNachTradeAktion(WechselTradeJobDTO tradeJobDTO, TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeJobDTO, "wechselTradeJobDTO ist null.");
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");

        if (tradeAktionDTO.getTradeStatus() == TradeStatus.ABGESCHLOSSEN) {
            //Verkauf ist erledigt --> WechselJob muss umgestellt werden
            tradeJobDTO.setKaufwert(tradeJobDTO.getLetztwert()); //Darf nicht auf NULL gesetzt werden wegen Trend-Bestimmung
            tradeJobDTO.setMenge(null);
            tradeJobDTO.setZielwert(null);
            tradeJobDTO.setVorgesehenerVerkaufwert(null);

            tradeJobDTO.setTradeVersuchAm(null);

            tradeJobDTO.setTradeStatus(TradeStatus.ERSTELLT);
            tradeJobDTO.setTradeAktionEnum(TradeAktionEnum.ORDER_KAUF);
            tradeJobDTO.setTradeTyp(TradeTyp.KAUF);

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(tradeJobDTO);

            logger.info("Verkauf erfolgreich. Der TradeTyp von WechselTradeJob mit wechselTradeJobId={} wurde umgesetzt", tradeJobDTO.getId(), tradeJobDTO.getTradeTyp());
        }
        else if (tradeAktionDTO.getTradeStatus() == TradeStatus.TRADE_FEHLGESCHLAGEN) {
            logger.warn("Keinen Kauefer von {} bei WechselTradeJob mit wechselTradeJobId={} gefunden.", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());

            //alles wieder zuruecksetzen so dass neue Kauf-Order erstellt wird
            tradeJobDTO.setTradeStatus(TradeStatus.BEOBACHTUNG);
            //Aktualisieren von TradeJob
            aktualisiereTradeJob(tradeJobDTO);
        }

    }

    private TradeAktionDTO erstelleTradeAktion(WechselTradeJobDTO tradeJobDTO) {
        //Es wird eine TradeAktion erstellt: Verkauf

        TradeAktionDTO tradeAktionDTO = new TradeAktionDTO();
        tradeAktionDTO.setTradeTyp(TradeTyp.VERKAUF);
        tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_VERKAUF);
        tradeAktionDTO.setErstelltAm(LocalDateTime.now());
        tradeAktionDTO.setTradingPlattform(tradeJobDTO.getTradingPlattform());

        tradeAktionDTO.setTradeJobId(tradeJobDTO.getId());
        tradeAktionDTO.setTradeJobTyp(tradeJobDTO.getTradeJobTyp());
        //tradeAktionDTO.setUserId(); //TODO technischen User setzen

        tradeAktionDTO.setVonMenge(ermittleRelevanteTradeMenge(tradeJobDTO.getMenge(), tradeJobDTO.isGanzZahlig()));
        //tradeAktionDTO.setZuMenge(); wird das hier benoetigt
        tradeAktionDTO.setVonWaehrung(tradeJobDTO.getCryptoWaehrung());
        tradeAktionDTO.setZuWaehrung(tradeJobDTO.getCryptoWaehrungReferenz());

        //Ermitteln des Preises
        tradeAktionDTO.setPreisProEinheit(ermittleOrderWert(tradeJobDTO));

        //Speichern der TradeAktion
        Long tradeAktionId = tradeAktionService.speichereTradeAktion(tradeAktionDTO);
        return tradeAktionDTO;
    }
}
