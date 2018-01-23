package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.HoldingOrderDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.TradingPlattformService;
import at.vulperium.cryptobot.services.jobs.TradeJobVerwaltungService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 16.01.2018.
 */
@ApplicationScoped
public class TradeAktionVerwaltungServiceImpl implements TradeAktionVerwaltungService {

    private static final Logger logger = LoggerFactory.getLogger(TradeAktionVerwaltungServiceImpl.class);

    private @Inject TradeAktionService tradeAktionService;
    private @Inject TradeJobVerwaltungService tradeJobVerwaltungService;
    private @Inject TradingPlattformService tradingPlattformService;

    @Override
    public void verarbeiteTradeAktionAufgaben() {
        //Holen aller TradeAktionen
        List<TradeAktionDTO> alleTradeAktionDTOList = tradeAktionService.holeAlleTradeAktionen();
        List<TradeAktionDTO> offeneTradeAktionDTOList = tradeAktionService.filterTradeAktionDTOList(alleTradeAktionDTOList, false);

        for (TradingPlattform tradingPlattform : TradingPlattform.values()) {

            if (tradingPlattform == TradingPlattform.ALLE) {
                //nur fuer Anzeige --> solche Jobs darf es garnicht geben
                continue;
            }

            //Fuer einzelnen TradingPlattformen relevante TradeJobs
            List<TradeAktionDTO> relevanteTradeAktionDTOList = tradeAktionService.filterTradeAktionDTOList(offeneTradeAktionDTOList, tradingPlattform);
            verarbeiteTradeAktionAufgaben(relevanteTradeAktionDTOList, tradingPlattform);
        }
    }


    @Override
    public void verarbeiteTradeAktionAufgaben(List<TradeAktionDTO> tradeAktionDTOList, TradingPlattform tradingPlattform) {
        if (CollectionUtils.isEmpty(tradeAktionDTOList)) {
            //Keine offene TradeAktionen vorhanden
            return;
        }

        List<TradeAktionDTO> kaufTradeAktionList = tradeAktionService.filterTradeAktionDTOList(tradeAktionDTOList, TradeTyp.KAUF);
        List<TradeAktionDTO> verkaufTradeAktionList = tradeAktionService.filterTradeAktionDTOList(tradeAktionDTOList, TradeTyp.VERKAUF);
        //Abarbeiten der VerkaufTrades
        if (CollectionUtils.isNotEmpty(verkaufTradeAktionList)) {
            for (TradeAktionDTO tradeAktionDTO : verkaufTradeAktionList) {
                fuehreTradeAktionDurch(tradeAktionDTO);
            }
        }

        //Abarbeiten der KaufTrades
        if (CollectionUtils.isNotEmpty(kaufTradeAktionList)) {
            for (TradeAktionDTO tradeAktionDTO : kaufTradeAktionList) {
                fuehreTradeAktionDurch(tradeAktionDTO);
            }
        }
    }

    /**
     * Fuehrt die TradeAktion durch
     * ACHTUNG: TradeAktion muss bereits persistiert sein
     *
     * @param tradeAktionDTO
     */
    @Override
    public boolean fuehreTradeAktionDurch(TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");
        Validate.notNull(tradeAktionDTO.getId(), "tradeAktionId ist null.");

        HoldingOrderDTO holdingOrderDTO = tradingPlattformService.ermittleHoldingOrderInformationen(tradeAktionDTO.getTradingPlattform());

        //Ueberpruefen um welche Art von Trade es sich handelt
        switch (tradeAktionDTO.getTradeStatus()) {
            case TRADE_KAUF:
                return erstelleKaufOrder(tradeAktionDTO, holdingOrderDTO);
            case TRADE_VERKAUF:
                return erstelleVerkaufOrder(tradeAktionDTO, holdingOrderDTO);
            case TRADE_PRUEFUNG_KAUF:
            case TRADE_PRUEFUNG_VERKAUF:
                return ueberpruefeKaufUndVerkaufOrder(tradeAktionDTO, holdingOrderDTO);
            default:
                throw new IllegalStateException("Fuer die TradeAktion=" + tradeAktionDTO.getId() + " konnte kein gueltiger TradeStatus ermittelt werden.");
        }
    }

    /**
     * Ueberprueft ob Trade-Orders auch erfolgreich abgeschlossen wurden
     *
     * @param tradeAktionDTO
     */
    private boolean ueberpruefeKaufUndVerkaufOrder(TradeAktionDTO tradeAktionDTO, HoldingOrderDTO holdingOrderDTO) {
        boolean orderAbgeschlossen = ueberpruefeTradeAktion(tradeAktionDTO, holdingOrderDTO);
        if (orderAbgeschlossen) {
            tradeAktionDTO.setTradeStatus(TradeStatus.ABGESCHLOSSEN);
            tradeAktionDTO.setErledigtAm(LocalDateTime.now());
        }
        else {
            tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_FEHLGESCHLAGEN);
            tradeAktionDTO.setErledigtAm(LocalDateTime.now());
        }

        //TradeAktion aktualisieren
        tradeAktionService.aktualisiereTradeAktion(tradeAktionDTO);

        //Ueberpruefen ob bei TradeJob auch was gemacht werden muss
        if (tradeAktionDTO.getTradeJobId() != null) {
            tradeJobVerwaltungService.aktualisiereTradeJob(tradeAktionDTO);
        }

        return orderAbgeschlossen;
    }

    private boolean erstelleKaufOrder(TradeAktionDTO tradeAktionDTO, HoldingOrderDTO holdingOrderDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");


        boolean orderErfolgreichErstellt = erstelleOrder(tradeAktionDTO, holdingOrderDTO);
        if (orderErfolgreichErstellt) {
            tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_KAUF);
            tradeAktionService.aktualisiereTradeAktion(tradeAktionDTO);
        }
        else {
            tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_FEHLGESCHLAGEN);
            tradeAktionDTO.setErledigtAm(LocalDateTime.now());
            logger.warn("Fehler bei Erstellung der Kauf-Order von TradeAktion mit tradeAktionId={}.", tradeAktionDTO.getId());
        }

        return orderErfolgreichErstellt;
    }

    private boolean erstelleVerkaufOrder(TradeAktionDTO tradeAktionDTO, HoldingOrderDTO holdingOrderDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");

        boolean orderErfolgreichErstellt = erstelleOrder(tradeAktionDTO, holdingOrderDTO);
        if (orderErfolgreichErstellt) {
            tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_VERKAUF);
        }
        else {
            tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_FEHLGESCHLAGEN);
            tradeAktionDTO.setErledigtAm(LocalDateTime.now());
            logger.warn("Fehler bei Erstellung der Verkauf-Order von TradeAktion mit tradeAktionId={}.", tradeAktionDTO.getId());
        }
        tradeAktionService.aktualisiereTradeAktion(tradeAktionDTO);

        return orderErfolgreichErstellt;
    }


    private boolean erstelleOrder(TradeAktionDTO tradeAktionDTO, HoldingOrderDTO holdingOrderDTO) {
        //Ueberpruefen ob Trade erstellt werden kann
        String notwendigesSymbol = null;
        BigDecimal notwendigeMenge= null;
        if (tradeAktionDTO.getTradeTyp() == TradeTyp.KAUF) {
            notwendigesSymbol = tradeAktionDTO.getCryptoWaehrungReferenz();
            notwendigeMenge = tradeAktionDTO.getMengeReferenz();
        }
        else if (tradeAktionDTO.getTradeTyp() == TradeTyp.VERKAUF) {
            notwendigesSymbol = tradeAktionDTO.getCryptoWaehrung();
            notwendigeMenge = tradeAktionDTO.getMenge();
        }
        if (istOrderMoeglich(notwendigesSymbol, notwendigeMenge, holdingOrderDTO)) {
            //Aufruf von WS zum Erstellen einer TradeOrder

            //Aktualisieren von HoldingOrderDTO wenn Trade erstelllt werden konnte (nur abziehen)
        }
        else {
            return false;
        }

        return true;
    }


    private boolean istOrderMoeglich(String vonSymbol, BigDecimal angeforderteMenge, HoldingOrderDTO holdingOrderDTO) {
        Validate.notNull(holdingOrderDTO, "holdingOrderDTO ist null.");

        Map<String, BigDecimal> holdingMap = holdingOrderDTO.getHoldingMap();
        if (holdingMap == null) {
            logger.error("Fehler bei der Ueberpruefung der HoldingMap. HoldingMap ist null!");
            return false;
        }

        BigDecimal vorhandeneMenge = holdingMap.get(vonSymbol);
        if (vorhandeneMenge == null) {
            logger.warn("Fehler bei der Ueberpruefung der HoldingMap. Fuer Symbol={} ist keine Information vorhanden.", vonSymbol);
            return false;
        }

        if (vorhandeneMenge.compareTo(angeforderteMenge) == -1) {
            logger.warn("Fehler bei der Ueberpruefung der HoldingMap. Menge={} von Symbol={} ist nicht ausreichend. Angeforderte Menge={}",
                    vorhandeneMenge, vonSymbol, angeforderteMenge);
            return false;
        }

        return true;
    }

    private boolean ueberpruefeTradeAktion(TradeAktionDTO tradeAktionDTO, HoldingOrderDTO holdingOrderDTO) {
        //TODO per WS nachschauen ob die Order noch offen ist oder ob die Order bereits abgeschlossen wurde
        //TODO wenn Order nicht abgeschlossen wurde gleich stornieren?
        return true;
    }

    /*
    @Override
    public TradeAktionDTO fuehreTradeAktionFuerTradeJobDurch(AbstractTradeJobDTO abstractTradeJobDTO) {
        TradeAktionDTO tradeAktionDTO;

        if (abstractTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
            tradeAktionDTO = erstelleKaufOrder(abstractTradeJobDTO);

            if (tradeAktionDTO == null) {
                //Fehler bei Erstellung der Order
                return null;
            }

            //Setzen von Informationen in den TradeJob
            abstractTradeJobDTO.setKaufwert(tradeAktionDTO.getPreisProEinheit());
            abstractTradeJobDTO.setMenge(tradeAktionDTO.getZuMenge());
        }
        else if (abstractTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
            tradeAktionDTO = erstelleVerkaufOrder(abstractTradeJobDTO);

            if (tradeAktionDTO == null) {
                //Fehler bei Erstellung der Order
                return null;
            }

            //Setzen von Informationen in den TradeJob
            //TODO mueesenhier noch werte gesetzt werden?
        }
        else {
            throw new IllegalStateException("Fuer TradeJob=" + abstractTradeJobDTO.getId() + " konnte kein richtiger TradeTyp ermittelt werden!");
        }

        //Setzen der Referenz zu TradeJob
        tradeAktionDTO.setTradeJobId(abstractTradeJobDTO.getId());
        abstractTradeJobDTO.setTradeJobTyp(abstractTradeJobDTO.getTradeJobTyp());

        //Speichern der TradeAktion
        tradeAktionService.speichereTradeAktion(tradeAktionDTO);
        return tradeAktionDTO;
    }
    */

    /*
    private void verarbeiteTradeAktionKaufUndVerkauf(List<TradeAktionDTO> tradeAktionList) {
        if (CollectionUtils.isEmpty(tradeAktionList)) {
            return;
        }

        for (TradeAktionDTO tradeAktionDTO : tradeAktionList) {
            if (tradeAktionDTO.getTradeStatus() == TradeStatus.TRADE_PRUEFUNG_KAUF || tradeAktionDTO.getTradeStatus() == TradeStatus.TRADE_PRUEFUNG_VERKAUF) {
                boolean orderAbgeschlossen = ueberpruefeTradeAktion(tradeAktionDTO);
                if (orderAbgeschlossen) {
                    tradeAktionDTO.setTradeStatus(TradeStatus.ABGESCHLOSSEN);
                    tradeAktionDTO.setErledigtAm(LocalDateTime.now());
                }
                else {
                    tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_FEHLGESCHLAGEN);
                }

                //TradeAktion aktualisieren
                tradeAktionService.aktualisiereTradeAktion(tradeAktionDTO);

                //Ueberpruefen ob bei TradeJob auch was gemacht werden muss
                if (tradeAktionDTO.getTradeJobId() != null) {
                    tradeJobVerwaltungService.aktualisiereTradeJob(tradeAktionDTO);
                }
            }
        }
    }
    */

    /*
    private TradeAktionDTO erstelleKaufOrder(AbstractTradeJobDTO tradeJobDTO) {
        //Kauf-Order erstellen
        TradeAktionDTO neueTradeAktion = erstelleOrder(tradeJobDTO.getCryptoWaehrungReferenz(), tradeJobDTO.getCryptoWaehrung());
        neueTradeAktion.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_KAUF);
        neueTradeAktion.setTradeTyp(TradeTyp.KAUF);
        neueTradeAktion.setUserId(null); //TODO durch einen technischen User ersetzen

        return neueTradeAktion;
    }

    private TradeAktionDTO erstelleVerkaufOrder(AbstractTradeJobDTO tradeJobDTO) {
        //Verkauf-Order erstellen per WS
        TradeAktionDTO neueTradeAktion = erstelleOrder(tradeJobDTO.getCryptoWaehrung(), tradeJobDTO.getCryptoWaehrungReferenz());
        neueTradeAktion.setTradeStatus(TradeStatus.TRADE_PRUEFUNG_VERKAUF);
        neueTradeAktion.setTradeTyp(TradeTyp.VERKAUF);
        neueTradeAktion.setUserId(null); //TODO durch einen technischen User ersetzen

        return neueTradeAktion;
    }


    private TradeAktionDTO erstelleOrder(String vonSymbol, String zuSymbol) {
        //Order erstellen per WS

        TradeAktionDTO neueTradeAktion = new TradeAktionDTO();
        neueTradeAktion.setErstelltAm(LocalDateTime.now());
        neueTradeAktion.setVonWaehrung(vonSymbol);
        neueTradeAktion.setZuWaehrung(zuSymbol);

        //neueTradeAktion.setVonMenge();
        //neueTradeAktion.setZuMenge();
        //neueTradeAktion.setPreisProEinheit();

        neueTradeAktion.setTradingPlattform(TradingPlattform.BINANCE);
        return neueTradeAktion;
    }

    */
}
