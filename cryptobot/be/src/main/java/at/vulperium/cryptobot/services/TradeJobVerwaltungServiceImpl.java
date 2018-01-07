package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.TradeStatusTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TradeJobVerwaltungServiceImpl implements TradeJobVerwaltungService {

    public static final Logger logger = LoggerFactory.getLogger(TradeJobVerwaltungServiceImpl.class);

    private @Inject TradeJobService tradeJobService;
    private @Inject TradeVerkaufService tradeVerkaufService;
    private @Inject TradeKaufService tradeKaufService;
    private @Inject TradingPlattformService tradingPlattformService;
    private @Inject BenachrichtigungService benachrichtigungService;


    /**
     * Ueberpureft ob fuer einzelne Trade-Plattformen offene Aufgaben vorhanden sind und fuehrt diese durch
     */
    @Override
    public void verarbeiteTradeAufgaben() {

        //Laden alle TradeJobs
        List<TradeJobDTO> alleTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        List<TradeJobDTO> offeneTradeJobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, false);

        for (TradingPlattform tradingPlattform : TradingPlattform.values()) {

            if (tradingPlattform == TradingPlattform.ALLE) {
                //nur fuer Anzeige --> solche Jobs darf es garnicht geben
                continue;
            }

            //Fuer einzelnen TRadingPlattformen relevante TradeJobs
            List<TradeJobDTO> relevanteTradeJobDTOList = tradeJobService.filterTradeJobDTOList(offeneTradeJobDTOList, tradingPlattform);
            if (CollectionUtils.isNotEmpty(relevanteTradeJobDTOList)) {
                // Durchfuehren der einzelnen TradeAufgaben
                verarbeiteTradeAufgaben(relevanteTradeJobDTOList, tradingPlattform);
            }
        }
    }

    /**
     * Fuehrt die offenen TradeAufgaben fuer eine Trade-Plattform aus
     */
    @Override
    public void verarbeiteTradeAufgaben(List<TradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform) {

        //Abfragen der aktuellen Kurse - Aufruf von WS
        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = tradingPlattformService.holeWSCryptoCoinMap(tradingPlattform);

        //Aufteilen der Aufgaben in Kauf und Verkauf
        List<TradeJobDTO> kaufJobList = tradeJobService.filterTradeJobDTOList(tradeJobDTOList, TradeStatusTyp.KAUF);
        List<TradeJobDTO> verkaufJobList = tradeJobService.filterTradeJobDTOList(tradeJobDTOList, TradeStatusTyp.VERKAUF);

        //Zuerst wird verkauft und anschliessend gekauft
        List<TradeJobDTO> neuerTradeStatusTradeJobDTOList = new ArrayList<>();
        //Verkaufsaufgaben
        neuerTradeStatusTradeJobDTOList.addAll(verarbeiteVerkaufAufgaben(verkaufJobList, wsCryptoCoinDTOMap, tradingPlattform));
        //Kaufaufgaben
        neuerTradeStatusTradeJobDTOList.addAll(verarbeiteKaufAufgaben(kaufJobList, wsCryptoCoinDTOMap, tradingPlattform));

        if (CollectionUtils.isNotEmpty(neuerTradeStatusTradeJobDTOList)) {
            //TradeJobs mit neuem Status werden fuer die Benachrichtigung vorgesehen
            benachrichtigungService.versendeBenachrichtigung(neuerTradeStatusTradeJobDTOList, BenachrichtigungTyp.MAIL , tradingPlattform);
        }
    }

    private List<TradeJobDTO> verarbeiteKaufAufgaben(List<TradeJobDTO> kaufJobList, Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {
        List<TradeJobDTO> neuerTradeStatusTradeJobDTOList = new ArrayList<>();

        if (CollectionUtils.isEmpty(kaufJobList)) {
            logger.info("Kein offene Kaufaufgaben fuer die Trading-Plattform={} vorhanden!", tradingPlattform);
            return neuerTradeStatusTradeJobDTOList;
        }

        for (TradeJobDTO tradeJobDTO : kaufJobList) {
            //Finden der passenden WSCryptoCoin
            WSCryptoCoinDTO wsCryptoCoinDTO = ermittleWSCryptoCoinDTO(wsCryptoCoinDTOMap, tradeJobDTO);

            if (wsCryptoCoinDTO == null) {
                //Keine passende Informationen vom WS --> weitermachen
                if (tradeJobDTO.getTradeJobStatus() != TradeJobStatus.KAUF_FEHLER) {
                    tradeJobDTO.setTradeJobStatus(TradeJobStatus.KAUF_FEHLER);
                    tradeJobService.aktualisiereTradeJob(tradeJobDTO);
                }
                continue;
            }

            //Passenede Informationen vom WS sind vorhanden
            TradeJobReaktion tradeJobReaktion = tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
            if (tradeJobReaktion == TradeJobReaktion.NEUER_TRADESTATUS) {
                neuerTradeStatusTradeJobDTOList.add(tradeJobDTO);
            }
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle Kaufaufgaben fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);
        return neuerTradeStatusTradeJobDTOList;
    }

    private List<TradeJobDTO> verarbeiteVerkaufAufgaben(List<TradeJobDTO> verkaufJobList, Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {
        List<TradeJobDTO> neuerTradeStatusTradeJobDTOList = new ArrayList<>();

        if (CollectionUtils.isEmpty(verkaufJobList)) {
            logger.info("Kein offene Verkaufaufgaben fuer die Trading-Plattform={} vorhanden!", tradingPlattform);
            return neuerTradeStatusTradeJobDTOList;
        }

        for (TradeJobDTO tradeJobDTO : verkaufJobList) {
            //Finden der passenden WSCryptoCoin
            WSCryptoCoinDTO wsCryptoCoinDTO = ermittleWSCryptoCoinDTO(wsCryptoCoinDTOMap, tradeJobDTO);

            if (wsCryptoCoinDTO == null) {
                //Keine passende Informationen vom WS --> weitermachen
                if (tradeJobDTO.getTradeJobStatus() != TradeJobStatus.VERKAUF_FEHLER) {
                    tradeJobDTO.setTradeJobStatus(TradeJobStatus.VERKAUF_FEHLER);
                    tradeJobService.aktualisiereTradeJob(tradeJobDTO);
                }
                continue;
            }

            //Passenede Informationen vom WS sind vorhanden
            TradeJobReaktion tradeJobReaktion = tradeVerkaufService.verarbeiteVerkaufAktion(tradeJobDTO, wsCryptoCoinDTO);
            if (tradeJobReaktion == TradeJobReaktion.NEUER_TRADESTATUS) {
                neuerTradeStatusTradeJobDTOList.add(tradeJobDTO);
            }
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle Verkaufaufgaben fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);
        return neuerTradeStatusTradeJobDTOList;
    }

    private WSCryptoCoinDTO ermittleWSCryptoCoinDTO(Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradeJobDTO tradeJobDTO) {

        String symbol = tradeJobDTO.getCryptoWaehrung() + tradeJobDTO.getCryptoWaehrungReferenz();
        WSCryptoCoinDTO wsCryptoCoinDTO = wsCryptoCoinDTOMap.get(symbol);

        if (wsCryptoCoinDTO == null) {
            //Keine passende CryptoCoin vomm WebService gefunden
            logger.warn("Fuer die TradeJob tradeAktionId={} wurden zu der waehrung={} und referenzwaehrung={} keine passenden Informationen ermittelt",
                    tradeJobDTO.getId(), tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getCryptoWaehrungReferenz());
            return null;
        }

        return wsCryptoCoinDTO;
    }

}
