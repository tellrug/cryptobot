package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.BenachrichtigungManager;
import at.vulperium.cryptobot.services.TradingPlattformService;
import at.vulperium.cryptobot.services.trades.TradeKaufService;
import at.vulperium.cryptobot.services.trades.TradeVerkaufService;
import at.vulperium.cryptobot.services.trades.WechselTradeJobService;
import at.vulperium.cryptobot.services.trades.WechselTradeKaufService;
import at.vulperium.cryptobot.services.trades.WechselTradeVerkaufService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TradeJobVerwaltungServiceImpl implements TradeJobVerwaltungService {

    public static final Logger logger = LoggerFactory.getLogger(TradeJobVerwaltungServiceImpl.class);

    private @Inject TradeJobService tradeJobService;
    private @Inject WechselTradeJobService wechselTradeJobService;
    private @Inject TradeVerkaufService tradeVerkaufService;
    private @Inject TradeKaufService tradeKaufService;
    private @Inject WechselTradeKaufService wechselTradeKaufService;
    private @Inject WechselTradeVerkaufService wechselTradeVerkaufService;
    private @Inject TradingPlattformService tradingPlattformService;
    private @Inject BenachrichtigungManager benachrichtigungManager;


    /**
     * Ueberpureft ob fuer einzelne Trade-Plattformen offene Aufgaben vorhanden sind und fuehrt diese durch
     */
    @Override
    public void verarbeiteBeobachtungsAufgaben() {

        //Laden alle TradeJobs
        List<SimpelTradeJobDTO> alleSimpelTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        List<SimpelTradeJobDTO> offeneSimpelTradeJobDTOList = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, false);

        List<WechselTradeJobDTO> alleWechselTradeJobList = wechselTradeJobService.holeAlleWechselTradeJobs();
        List<WechselTradeJobDTO> offeneWechselTradeJobDTOList = wechselTradeJobService.filterTradeJobDTOList(alleWechselTradeJobList, false);

        for (TradingPlattform tradingPlattform : TradingPlattform.values()) {

            if (tradingPlattform == TradingPlattform.ALLE) {
                //nur fuer Anzeige --> solche Jobs darf es garnicht geben
                continue;
            }

            //Fuer einzelnen TradingPlattformen relevante TradeJobs
            List<SimpelTradeJobDTO> relevanteSimpelTradeJobDTOList = tradeJobService.filterTradeJobDTOList(offeneSimpelTradeJobDTOList, tradingPlattform);
            List<WechselTradeJobDTO> relevanteWechselTradeJobDTOList = wechselTradeJobService.filterTradeJobDTOList(offeneWechselTradeJobDTOList, tradingPlattform);

            if (CollectionUtils.isNotEmpty(relevanteSimpelTradeJobDTOList) || CollectionUtils.isNotEmpty(relevanteWechselTradeJobDTOList)) {
                // Durchfuehren der einzelnen TradeAufgaben
                verarbeiteBeobachtungsAufgaben(relevanteSimpelTradeJobDTOList, relevanteWechselTradeJobDTOList, tradingPlattform);
            }
        }
    }

    /**
     * Fuehrt die offenen TradeAufgaben fuer eine Trade-Plattform aus
     */
    @Override
    public void verarbeiteBeobachtungsAufgaben(List<SimpelTradeJobDTO> simpelTradeJobDTOList, List<WechselTradeJobDTO> wechselTradeJobDTOList, TradingPlattform tradingPlattform) {

        //Abfragen der aktuellen Kurse - Aufruf von WS
        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = tradingPlattformService.holeWSCryptoCoinMap(tradingPlattform);

        //Verarbeiten der simplen Aufgaben
        verarbeiteSimpleTradeAufgaben(simpelTradeJobDTOList, wsCryptoCoinDTOMap, tradingPlattform);

        //Verarbeiten der WechselJobs
        verarbeiteWechselTradeAufgaben(wechselTradeJobDTOList, wsCryptoCoinDTOMap, tradingPlattform);
    }

    @Override
    public void aktualisiereTradeJob(TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");
        if (tradeAktionDTO.getTradeJobId() != null) {
            if (TradeJobTyp.SIMPEL == tradeAktionDTO.getTradeJobTyp()) {
                SimpelTradeJobDTO simpelTradeJobDTO = tradeJobService.holeTradeJob(tradeAktionDTO.getTradeJobId());
                if (simpelTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
                    tradeKaufService.aktualisiereTradeJobNachTradeAktion(simpelTradeJobDTO, tradeAktionDTO);
                }
                else if (simpelTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
                    tradeVerkaufService.aktualisiereTradeJobNachTradeAktion(simpelTradeJobDTO, tradeAktionDTO);
                }
            }
            else if (TradeJobTyp.WECHSEL == tradeAktionDTO.getTradeJobTyp()) {
                WechselTradeJobDTO wechselTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(tradeAktionDTO.getTradeJobId());
                if (wechselTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
                    wechselTradeKaufService.aktualisiereTradeJobNachTradeAktion(wechselTradeJobDTO, tradeAktionDTO);
                }
                else if (wechselTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
                    wechselTradeVerkaufService.aktualisiereTradeJobNachTradeAktion(wechselTradeJobDTO, tradeAktionDTO);
                }
            }
        }
    }

    private void verarbeiteSimpleTradeAufgaben(List<SimpelTradeJobDTO> simpelTradeJobDTOList, Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {
        //Aufteilen der Aufgaben in Kauf und Verkauf
        List<SimpelTradeJobDTO> kaufJobList = tradeJobService.filterTradeJobDTOList(simpelTradeJobDTOList, TradeTyp.KAUF);
        List<SimpelTradeJobDTO> verkaufJobList = tradeJobService.filterTradeJobDTOList(simpelTradeJobDTOList, TradeTyp.VERKAUF);

        //Verkaufsaufgaben
        verarbeiteVerkaufAufgaben(verkaufJobList, wsCryptoCoinDTOMap, tradingPlattform);
        //Kaufaufgaben
        verarbeiteKaufAufgaben(kaufJobList, wsCryptoCoinDTOMap, tradingPlattform);

        //Verschicken der Benachrichtigungen
        benachrichtigungManager.fuehreBenachrichtigungDurch();
    }

    private void verarbeiteWechselTradeAufgaben(List<WechselTradeJobDTO> wechselTradeJobDTOList,
                                                Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {

        if (CollectionUtils.isEmpty(wechselTradeJobDTOList)) {
            logger.info("Kein offene WechselJobs fuer die Trading-Plattform={} vorhanden!", tradingPlattform);
            return;
        }

        for (WechselTradeJobDTO wechselTradeJobDTO : wechselTradeJobDTOList) {
            //Finden der passenden WSCryptoCoin
            WSCryptoCoinDTO wsCryptoCoinDTO = ermittleWSCryptoCoinDTO(wsCryptoCoinDTOMap, wechselTradeJobDTO);

            if (wsCryptoCoinDTO == null) {
                //Keine passende Informationen vom WS --> weitermachen
                setzeFehlerStatus(wechselTradeJobDTO);
                continue;
            }

            //Passende Infos vom WS sind vorhanden
            //Ueberpruefen in welcher Phase sich der WechselJob befindet
            if (wechselTradeJobDTO.getTradeTyp() == TradeTyp.KAUF) {
                //Kauf-Phase
                wechselTradeKaufService.verarbeiteWechselJobKauf(wechselTradeJobDTO, wsCryptoCoinDTO);
            }
            else if (wechselTradeJobDTO.getTradeTyp() == TradeTyp.VERKAUF) {
                //Verkauf-Phase
                wechselTradeVerkaufService.verarbeiteWechselJobVerkauf(wechselTradeJobDTO, wsCryptoCoinDTO);
            }
            else {
                continue;
            }
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle WechselJobs fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);

        //Verschicken der Benachrichtigungen
        benachrichtigungManager.fuehreBenachrichtigungDurch();
    }

    private void verarbeiteKaufAufgaben(List<SimpelTradeJobDTO> kaufJobList, Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {
        if (CollectionUtils.isEmpty(kaufJobList)) {
            logger.info("Kein offene Kaufaufgaben fuer die Trading-Plattform={} vorhanden!", tradingPlattform);
        }

        for (SimpelTradeJobDTO simpelTradeJobDTO : kaufJobList) {
            //Finden der passenden WSCryptoCoin
            WSCryptoCoinDTO wsCryptoCoinDTO = ermittleWSCryptoCoinDTO(wsCryptoCoinDTOMap, simpelTradeJobDTO);

            if (wsCryptoCoinDTO == null) {
                //Keine passende Informationen vom WS --> weitermachen
                setzeFehlerStatus(simpelTradeJobDTO);
                continue;
            }

            //Passenede Informationen vom WS sind vorhanden
            tradeKaufService.verarbeiteKaufAktion(simpelTradeJobDTO, wsCryptoCoinDTO);
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle Kaufaufgaben fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);
    }

    private void verarbeiteVerkaufAufgaben(List<SimpelTradeJobDTO> verkaufJobList, Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, TradingPlattform tradingPlattform) {
        if (CollectionUtils.isEmpty(verkaufJobList)) {
            logger.info("Kein offene Verkaufaufgaben fuer die Trading-Plattform={} vorhanden!", tradingPlattform);
        }

        for (SimpelTradeJobDTO simpelTradeJobDTO : verkaufJobList) {
            //Finden der passenden WSCryptoCoin
            WSCryptoCoinDTO wsCryptoCoinDTO = ermittleWSCryptoCoinDTO(wsCryptoCoinDTOMap, simpelTradeJobDTO);

            if (wsCryptoCoinDTO == null) {
                //Keine passende Informationen vom WS --> weitermachen
                setzeFehlerStatus(simpelTradeJobDTO);
                continue;
            }

            //Passenede Informationen vom WS sind vorhanden
            tradeVerkaufService.verarbeiteVerkaufAktion(simpelTradeJobDTO, wsCryptoCoinDTO);
        }

        //Alle Verkaufsaufggaben sind erledigt
        logger.info("Alle Verkaufaufgaben fuer die Trading-Plattform={} abgearbeitet!", tradingPlattform);
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
                tradeJobService.aktualisiereTradeJob((SimpelTradeJobDTO) tradeJobDTO);
            }
            else if (tradeJobDTO.getTradeJobTyp() == TradeJobTyp.WECHSEL) {
                wechselTradeJobService.aktualisiereWechselTradeJob((WechselTradeJobDTO) tradeJobDTO);
            }

        }
    }
}
