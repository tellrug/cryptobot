package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.BenachrichtigungService;
import at.vulperium.cryptobot.services.trades.TradeKaufService;
import at.vulperium.cryptobot.services.trades.TradeVerkaufService;
import at.vulperium.cryptobot.services.TradingPlattformService;
import at.vulperium.cryptobot.services.trades.WechselTradeJobService;
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
    private @Inject WechselTradeJobService wechselTradeJobService;
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

        List<WechselTradeJobDTO> alleWechselTradeJobList = wechselTradeJobService.holeAlleWechselTradeJobs();
        List<WechselTradeJobDTO> offeneWechselTradeJobDTOList = wechselTradeJobService.filterTradeJobDTOList(alleWechselTradeJobList, false);

        for (TradingPlattform tradingPlattform : TradingPlattform.values()) {

            if (tradingPlattform == TradingPlattform.ALLE) {
                //nur fuer Anzeige --> solche Jobs darf es garnicht geben
                continue;
            }

            //Fuer einzelnen TradingPlattformen relevante TradeJobs
            List<TradeJobDTO> relevanteTradeJobDTOList = tradeJobService.filterTradeJobDTOList(offeneTradeJobDTOList, tradingPlattform);
            List<WechselTradeJobDTO> relevanteWechselTradeJobDTOList = wechselTradeJobService.filterTradeJobDTOList(offeneWechselTradeJobDTOList, tradingPlattform);

            if (CollectionUtils.isNotEmpty(relevanteTradeJobDTOList) || CollectionUtils.isNotEmpty(relevanteWechselTradeJobDTOList)) {
                // Durchfuehren der einzelnen TradeAufgaben
                verarbeiteTradeAufgaben(relevanteTradeJobDTOList, relevanteWechselTradeJobDTOList, tradingPlattform);
            }
        }
    }

    /**
     * Fuehrt die offenen TradeAufgaben fuer eine Trade-Plattform aus
     */
    @Override
    public void verarbeiteTradeAufgaben(List<TradeJobDTO> tradeJobDTOList, List<WechselTradeJobDTO> wechselTradeJobDTOList, TradingPlattform tradingPlattform) {

        //Abfragen der aktuellen Kurse - Aufruf von WS
        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = tradingPlattformService.holeWSCryptoCoinMap(tradingPlattform);

        //Verarbeiten der simplen Aufgaben
        verarbeiteSimpleTradeAufgaben(tradeJobDTOList, wsCryptoCoinDTOMap, tradingPlattform);

        //Verarbeiten der WechselJobs
        verarbeiteWechselTradeAufgaben(wechselTradeJobDTOList, wsCryptoCoinDTOMap, tradingPlattform);
    }

    private void verarbeiteSimpleTradeAufgaben(List<TradeJobDTO> tradeJobDTOList, Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {
        //Aufteilen der Aufgaben in Kauf und Verkauf
        List<TradeJobDTO> kaufJobList = tradeJobService.filterTradeJobDTOList(tradeJobDTOList, TradeTyp.KAUF);
        List<TradeJobDTO> verkaufJobList = tradeJobService.filterTradeJobDTOList(tradeJobDTOList, TradeTyp.VERKAUF);

        //Zuerst wird verkauft und anschliessend gekauft
        List<TradeJobDTO> neuerTradeStatusTradeJobDTOList = new ArrayList<>();
        //Verkaufsaufgaben
        neuerTradeStatusTradeJobDTOList.addAll(verarbeiteVerkaufAufgaben(verkaufJobList, wsCryptoCoinDTOMap, tradingPlattform));
        //Kaufaufgaben
        neuerTradeStatusTradeJobDTOList.addAll(verarbeiteKaufAufgaben(kaufJobList, wsCryptoCoinDTOMap, tradingPlattform));

        if (CollectionUtils.isNotEmpty(neuerTradeStatusTradeJobDTOList)) {
            //TradeJobs mit neuem Status werden fuer die Benachrichtigung vorgesehen
            benachrichtigungService.versendeBenachrichtigung(neuerTradeStatusTradeJobDTOList, BenachrichtigungTyp.MAIL, tradingPlattform);
        }
    }

    private void verarbeiteWechselTradeAufgaben(List<WechselTradeJobDTO> wechselTradeJobDTOList, Map<String,
            WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {

        if (CollectionUtils.isEmpty(wechselTradeJobDTOList)) {
            logger.info("Kein offene WechselJobs fuer die Trading-Plattform={} vorhanden!", tradingPlattform);
            return;
        }

        List<WechselTradeJobDTO> neuerTradeStatusWechselTradeJobDTOList = new ArrayList<>();
        for (WechselTradeJobDTO wechselTradeJobDTO : wechselTradeJobDTOList) {
            //Finden der passenden WSCryptoCoin
            WSCryptoCoinDTO wsCryptoCoinDTO = ermittleWSCryptoCoinDTO(wsCryptoCoinDTOMap, wechselTradeJobDTO);

            if (wsCryptoCoinDTO == null) {
                //Keine passende Informationen vom WS --> weitermachen
                setzeFehlerStatus(wechselTradeJobDTO);
                continue;
            }

            //Passende WS vom Service sind vorhanden
            TradeJobReaktion tradeJobReaktion = null; //tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
            if (tradeJobReaktion == TradeJobReaktion.NEUER_TRADESTATUS) {
                neuerTradeStatusWechselTradeJobDTOList.add(wechselTradeJobDTO);
            }
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle WechselJobs fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);
        if (CollectionUtils.isNotEmpty(neuerTradeStatusWechselTradeJobDTOList)) {
            //WechselTradeJobs mit neuem Status werden fuer die Benachrichtigung vorgesehen
            //benachrichtigungService.versendeBenachrichtigung(neuerTradeStatusTradeJobDTOList, BenachrichtigungTyp.MAIL, tradingPlattform);
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
                setzeFehlerStatus(tradeJobDTO);
                continue;
            }

            //Passenede Informationen vom WS sind vorhanden
            tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
            if (tradeJobDTO.getTradeStatus() == TradeStatus.ABGESCHLOSSEN) {
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
                setzeFehlerStatus(tradeJobDTO);
                continue;
            }

            //Passenede Informationen vom WS sind vorhanden
            tradeVerkaufService.verarbeiteVerkaufAktion(tradeJobDTO, wsCryptoCoinDTO);
            if (tradeJobDTO.getTradeStatus() == TradeStatus.ABGESCHLOSSEN) {
                neuerTradeStatusTradeJobDTOList.add(tradeJobDTO);
            }
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle Verkaufaufgaben fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);
        return neuerTradeStatusTradeJobDTOList;
    }


    private void verarbeiteWechselJobAufgabe(WechselTradeJobDTO wechselTradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {

        //Ermitteln in welcher Phase sich der WechselJob befindet
        TradeTyp tradeTyp = wechselTradeJobDTO.getTradeTyp();


    }

    private WSCryptoCoinDTO ermittleWSCryptoCoinDTO(Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, AbstractTradeJobDTO tradeJobDTO) {

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


    private <T extends AbstractTradeJobDTO> void setzeFehlerStatus(T tradeJobDTO) {
        if (tradeJobDTO.getTradeStatus() != TradeStatus.FEHLER) {
            tradeJobDTO.setTradeStatus(TradeStatus.FEHLER);

            if (tradeJobDTO.getTradeJobTyp() == TradeJobTyp.SIMPEL) {
                tradeJobService.aktualisiereTradeJob((TradeJobDTO) tradeJobDTO);
            }
            else if (tradeJobDTO.getTradeJobTyp() == TradeJobTyp.WECHSEL) {
                wechselTradeJobService.aktualisiereWechselTradeJob((WechselTradeJobDTO) tradeJobDTO);
            }

        }
    }
}
