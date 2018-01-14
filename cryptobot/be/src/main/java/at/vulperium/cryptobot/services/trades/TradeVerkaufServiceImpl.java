package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.Trend;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;


@ApplicationScoped
public class TradeVerkaufServiceImpl extends AbstractTradeService<TradeJobDTO> implements TradeVerkaufService {

    public static final Logger logger = LoggerFactory.getLogger(TradeVerkaufServiceImpl.class);
    private @Inject TradeJobService tradeJobService;

    @Override
    public void verarbeiteVerkaufAktion(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (tradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT || tradeJobDTO.getTradeStatus() == TradeStatus.BEOBACHTUNG) {
            verarbeiteTradeJob(tradeJobDTO, wsCryptoCoinDTO);
        }

        if (tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_PRUEFUNG_VERKAUF) {
            ueberpruefeVerkaufTrade(tradeJobDTO);
        }
    }

    public void verarbeiteWechselJobVerkauf(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(wechselTradeJobDTO, "wechselTradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (wechselTradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT) {
            //Job wurde erstellt
        }

    }

    @Override
    protected boolean ermittleZielErreicht(TradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei VERKAUF ist ziel erreicht wenn der aktuelle Kurs ueber dem Zielkurs liegt
        return aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 1 || aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 0;
    }


    @Override
    protected TradeJobReaktion ermittleTradeJobReaktion(TradeJobDTO tradeJobDTO, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.ABWAERTS) {
                //Trend ist nun fallend --> Verkauf
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, Kurs faellt wieder. Es kann verkauft werden", tradeJobDTO.getId());
                TradeStatus neuerTradeStatus = tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_VERKAUF ? TradeStatus.TRADE_VERKAUF : TradeStatus.ABGESCHLOSSEN;
                tradeJobDTO.setTradeStatus(neuerTradeStatus);
                return TradeJobReaktion.FOLGE_AKTION;
            } else {
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
    protected void aktualisiereTradeJob(TradeJobDTO tradeJobDTO) {
        tradeJobService.aktualisiereTradeJob(tradeJobDTO);
    }

    @Override
    protected TradeStatus fuehreFolgeaktionDurch(TradeJobDTO tradeJobDTO) {
        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.VERKAUF_ZIEL) {
            //momentan keine Folgeaktion
            return null;
        }

        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_VERKAUF && tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_VERKAUF) {
            //TODO hier die Verkauf-Order stellen
            boolean orderErfolgreichAbgesetzt = true;
            if (orderErfolgreichAbgesetzt) {
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_VERKAUF);
            }
            else {
                tradeJobDTO.setTradeStatus(TradeStatus.FEHLER);
            }
        }
        return null;
    }

    private void ueberpruefeVerkaufTrade(TradeJobDTO tradeJobDTO) {
        boolean verkaufErfolgreich = true;
        if (verkaufErfolgreich) {
            tradeJobDTO.setTradeStatus(TradeStatus.ABGESCHLOSSEN);
            tradeJobDTO.setErledigtAm(LocalDateTime.now());

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(tradeJobDTO);

            //speichern von TradeAktion
            //TODO speichern von TradeAktion

            logger.info("Verkauf von {} erfolgreich. TradeJob mit tradeJobId={} abgeschlossen", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());
        }
        else {
            logger.warn("Keinen Kauefer von {} bei TradeJob mit tradeJobId={} gefunden.", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());
            //TODO Trade stornieren? alles wieder zuruecksetzen so dass neue Verkauf-Order erstellt wird
        }
    }
}
