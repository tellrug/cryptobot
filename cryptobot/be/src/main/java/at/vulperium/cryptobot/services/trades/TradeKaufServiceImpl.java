package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
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

/**
 * Created by 02ub0400 on 02.01.2018.
 */
@ApplicationScoped
public class TradeKaufServiceImpl extends AbstractTradeService<TradeJobDTO> implements TradeKaufService {

    public static final Logger logger = LoggerFactory.getLogger(TradeKaufServiceImpl.class);

    private @Inject TradeJobService tradeJobService;

    @Override
    public void verarbeiteKaufAktion(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        if (tradeJobDTO.getTradeStatus() == TradeStatus.ERSTELLT || tradeJobDTO.getTradeStatus() == TradeStatus.BEOBACHTUNG) {
            verarbeiteTradeJob(tradeJobDTO, wsCryptoCoinDTO);
        }

        if (tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_PRUEFUNG_KAUF) {
            ueberpruefeKaufTrade(tradeJobDTO);
        }
    }

    @Override
    protected TradeJobReaktion ermittleTradeJobReaktion(TradeJobDTO tradeJobDTO, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.AUFWAERTS) {
                //Trend ist nun steigend --> Kauf
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, Kurs steigt wieder. Es kann gekauft werden", tradeJobDTO.getId());

                TradeStatus neuerTradeStatus = tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_KAUF ? TradeStatus.TRADE_KAUF : TradeStatus.ABGESCHLOSSEN;
                tradeJobDTO.setTradeStatus(neuerTradeStatus);

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
    protected boolean ermittleZielErreicht(TradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei KAUF ist das Ziel erreicht wenn der aktuelle Kurs unter dem Zielkurs liegt
        return aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == -1 || aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 0;
    }

    @Override
    protected void aktualisiereTradeJob(TradeJobDTO tradeJobDTO) {
        tradeJobService.aktualisiereTradeJob(tradeJobDTO);
    }

    @Override
    protected TradeStatus fuehreFolgeaktionDurch(TradeJobDTO tradeJobDTO) {
        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.KAUF_ZIEL) {
            //momentan keine Folgeaktion
            return null;
        }

        if (tradeJobDTO.getTradeAktionEnum() == TradeAktionEnum.ORDER_KAUF && tradeJobDTO.getTradeStatus() == TradeStatus.TRADE_KAUF) {
            //TODO hier die Kauf-Order stellen
            boolean orderErfolgreichAbgesetzt = true;
            if (orderErfolgreichAbgesetzt) {
                tradeJobDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_KAUF);
            }
            else {
                tradeJobDTO.setTradeStatus(TradeStatus.FEHLER);
            }
        }
        return null;
    }


    private void ueberpruefeKaufTrade(TradeJobDTO tradeJobDTO) {
        boolean kaufErfolgreich = true;
        if (kaufErfolgreich) {
            tradeJobDTO.setTradeStatus(TradeStatus.ABGESCHLOSSEN);
            tradeJobDTO.setErledigtAm(LocalDateTime.now());

            //Aktualisieren von TradeJob
            aktualisiereTradeJob(tradeJobDTO);

            //speichern von TradeAktion
            //TODO speichern von TradeAktion

            logger.info("Kauf von {} erfolgreich. TradeJob mit tradeJobId={} abgeschlossen", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());
        }
        else {
            logger.warn("Keinen Verkauefer von {} bei TradeJob mit tradeJobId={} gefunden.", tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getId());
            //TODO Trade stornieren? alles wieder zuruecksetzen so dass neue Verkauf-Order erstellt wird
        }
    }
}
