package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import at.vulperium.cryptobot.services.jobs.TradeJobVerwaltungService;
import at.vulperium.cryptobot.services.trades.TradeAktionService;
import at.vulperium.cryptobot.services.trades.TradeAktionVerwaltungService;
import at.vulperium.cryptobot.services.trades.WechselTradeJobService;
import at.vulperium.cryptobot.testdatahelper.TradeJobTestDataHelper;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class TradeJobVerwaltungServiceTest extends ContainerTest {

    private @Inject TradeJobVerwaltungService tradeJobVerwaltungService;
    private @Inject TradeJobService tradeJobService;
    private @Inject WechselTradeJobService wechselTradeJobService;
    private @Inject TradeAktionService tradeAktionService;
    private @Inject TradeAktionVerwaltungService tradeAktionVerwaltungService;

    private TradeJobTestDataHelper tradeJobTestDataHelper = new TradeJobTestDataHelper();

    @Test
    public void testVerarbeiteSimpelTradeAufgaben_NurAufZiel() {
        //Offenen VerkaufTradejob anlegen
        SimpelTradeJobDTO verkaufSimpelTradeJobDTO1 = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        verkaufSimpelTradeJobDTO1.setCryptoWaehrung("VK1");
        Long verkaufTradeJobDTO1id = tradeJobService.speichereTradeJob(verkaufSimpelTradeJobDTO1);
        Assert.assertNotNull(verkaufTradeJobDTO1id);

        SimpelTradeJobDTO verkaufSimpelTradeJobDTO2 = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        verkaufSimpelTradeJobDTO2.setCryptoWaehrung("VK2");
        Long verkaufTradeJobDTO2id = tradeJobService.speichereTradeJob(verkaufSimpelTradeJobDTO2);
        Assert.assertNotNull(verkaufTradeJobDTO2id);

        //Offenen KaufTradejob anlegen
        SimpelTradeJobDTO kaufSimpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        kaufSimpelTradeJobDTO.setCryptoWaehrung("K1");
        Long kaufTradeJobDTOid = tradeJobService.speichereTradeJob(kaufSimpelTradeJobDTO);
        Assert.assertNotNull(kaufTradeJobDTOid);
        cleanInstances();

        List<SimpelTradeJobDTO> simpelTradeJobDTOList = new ArrayList<>();
        simpelTradeJobDTOList.add(verkaufSimpelTradeJobDTO1);
        simpelTradeJobDTOList.add(verkaufSimpelTradeJobDTO2);
        simpelTradeJobDTOList.add(kaufSimpelTradeJobDTO);

        tradeJobVerwaltungService.verarbeiteBeobachtungsAufgaben(simpelTradeJobDTOList, new ArrayList<>(), TradingPlattform.BINANCE);
        cleanInstances();

        SimpelTradeJobDTO checkVK1 = tradeJobService.holeTradeJob(verkaufSimpelTradeJobDTO1.getId());
        Assert.assertNotNull(checkVK1);
        Assert.assertNotNull(checkVK1.getErledigtAm());
        Assert.assertEquals(checkVK1.getTradeAktionEnum(), TradeAktionEnum.VERKAUF_ZIEL);
        Assert.assertEquals(checkVK1.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);

        SimpelTradeJobDTO checkVK2 = tradeJobService.holeTradeJob(verkaufSimpelTradeJobDTO2.getId());
        Assert.assertNotNull(checkVK2);
        Assert.assertNull(checkVK2.getErledigtAm());
        Assert.assertEquals(checkVK2.getTradeAktionEnum(), TradeAktionEnum.VERKAUF_ZIEL);
        Assert.assertEquals(checkVK2.getTradeStatus(), TradeStatus.BEOBACHTUNG);


        SimpelTradeJobDTO checkK1 = tradeJobService.holeTradeJob(kaufSimpelTradeJobDTO.getId());
        Assert.assertNotNull(checkK1);
        Assert.assertNotNull(checkK1.getErledigtAm());
        Assert.assertEquals(checkK1.getTradeAktionEnum(), TradeAktionEnum.KAUF_ZIEL);
        Assert.assertEquals(checkK1.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);
    }

    @Test
    public void testVerarbeiteWechselTradeAufgabe() {

        //Anlegen von WechselJob
        WechselTradeJobDTO wechselTradeJobDTO = tradeJobTestDataHelper.erzeugeWechselTradeJobDTO(TradeAktionEnum.ORDER_KAUF);
        Long wechselTradeJobId = wechselTradeJobService.speicherNeuenWechselTradeJob(wechselTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(wechselTradeJobId);
        Assert.assertNull(wechselTradeJobDTO.getSpitzenwert());
        Assert.assertNull(wechselTradeJobDTO.getLetztwert());


        List<WechselTradeJobDTO> wechselTradeJobDTOList = new ArrayList<>();
        wechselTradeJobDTOList.add(wechselTradeJobDTO);

        //-----------------------------------------------------------------
        //Verarbeiten den wechselTradeJobs
        //Kauf sollte nicht angestossen werden da Trend noch fallend ist -> spitzenwert und letztwert werden gesetzt, ziel wurde erreicht -> status auf Beobachtung
        tradeJobVerwaltungService.verarbeiteBeobachtungsAufgaben(new ArrayList<>(), wechselTradeJobDTOList, TradingPlattform.BINANCE);
        cleanInstances();

        WechselTradeJobDTO checkTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(wechselTradeJobId);
        Assert.assertNotNull(checkTradeJobDTO);
        Assert.assertNotNull(checkTradeJobDTO.getSpitzenwert());
        Assert.assertNotNull(checkTradeJobDTO.getLetztwert());
        Assert.assertEquals(checkTradeJobDTO.getTradeStatus(), TradeStatus.BEOBACHTUNG);


        //Anpassen von Spitzenwert um Kauf auszuloesen
        checkTradeJobDTO.setSpitzenwert(TradeUtil.getBigDecimal(0.000005));
        wechselTradeJobService.aktualisiereWechselTradeJob(checkTradeJobDTO);
        cleanInstances();

        //-----------------------------------------------------------------
        //Erneutes abarbeiten durch Timerjob
        checkTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(wechselTradeJobId);
        wechselTradeJobDTOList.clear();
        wechselTradeJobDTOList.add(checkTradeJobDTO);
        //Kauf wird ausgeloest

        tradeJobVerwaltungService.verarbeiteBeobachtungsAufgaben(new ArrayList<>(), wechselTradeJobDTOList, TradingPlattform.BINANCE);
        cleanInstances();

        WechselTradeJobDTO kaufOrderTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(wechselTradeJobId);
        Assert.assertNotNull(kaufOrderTradeJobDTO);
        Assert.assertNotNull(kaufOrderTradeJobDTO.getSpitzenwert());
        Assert.assertNotNull(kaufOrderTradeJobDTO.getLetztwert());
        Assert.assertEquals(kaufOrderTradeJobDTO.getTradeStatus(), TradeStatus.TRADE_PRUEFUNG_KAUF);
        Assert.assertNotNull(kaufOrderTradeJobDTO.getMenge());
        Assert.assertNotNull(kaufOrderTradeJobDTO.getTradeVersuchAm());

        //Zu diesem Job sollte es nun eine KaufAktion geben
        List<TradeAktionDTO> alleTradeAktionen = tradeAktionService.holeAlleTradeAktionen();
        Assert.assertNotNull(alleTradeAktionen);

        List<TradeAktionDTO> relevanteTradeAktionen = tradeAktionService.filterTradeAktionDTOList(alleTradeAktionen, kaufOrderTradeJobDTO.getId());
        Assert.assertNotNull(relevanteTradeAktionen);
        Assert.assertEquals(relevanteTradeAktionen.size(), 1);

        TradeAktionDTO kaufAktionDTO = relevanteTradeAktionen.get(0);
        Assert.assertNotNull(kaufAktionDTO);
        Assert.assertEquals(kaufAktionDTO.getCryptoWaehrungReferenz(), TradeJobTestDataHelper.BTC);
        Assert.assertEquals(kaufAktionDTO.getCryptoWaehrung(), TradeJobTestDataHelper.WJC_KAUF);
        Assert.assertTrue(TradeUtil.getBigDecimal(2000).compareTo(kaufAktionDTO.getMenge()) == 1); //Menge an NeuCoins die gekauft werden soll, wegen Abschlägen bisschen weniger als 2000
        Assert.assertEquals(kaufAktionDTO.getTradeStatus(), TradeStatus.TRADE_PRUEFUNG_KAUF);
        Assert.assertNull(kaufAktionDTO.getErledigtAm());

        //-----------------------------------------------------------------
        //Kauf erfolgreich abgeschlossen -> es wird auf Verkauf gewechselt

        tradeAktionVerwaltungService.verarbeiteTradeAktionAufgaben(relevanteTradeAktionen, TradingPlattform.BINANCE);
        cleanInstances();


        alleTradeAktionen = tradeAktionService.holeAlleTradeAktionen();
        Assert.assertNotNull(alleTradeAktionen);

        List<TradeAktionDTO> checkTradeAktionen = tradeAktionService.filterTradeAktionDTOList(alleTradeAktionen, kaufOrderTradeJobDTO.getId());
        Assert.assertNotNull(checkTradeAktionen);
        Assert.assertEquals(checkTradeAktionen.size(), 1);

        TradeAktionDTO kaufAbgeschlossenAktionDTO = checkTradeAktionen.get(0);
        Assert.assertNotNull(kaufAbgeschlossenAktionDTO);
        Assert.assertEquals(kaufAbgeschlossenAktionDTO.getCryptoWaehrungReferenz(), TradeJobTestDataHelper.BTC);
        Assert.assertEquals(kaufAbgeschlossenAktionDTO.getCryptoWaehrung(), TradeJobTestDataHelper.WJC_KAUF);
        Assert.assertEquals(kaufAbgeschlossenAktionDTO.getMenge(), kaufAktionDTO.getMenge());
        Assert.assertEquals(kaufAbgeschlossenAktionDTO.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);
        Assert.assertNotNull(kaufAbgeschlossenAktionDTO.getErledigtAm());


        WechselTradeJobDTO verkaufTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(wechselTradeJobId);
        Assert.assertNotNull(verkaufTradeJobDTO);
        Assert.assertNotNull(verkaufTradeJobDTO.getZielwert());
        Assert.assertEquals(verkaufTradeJobDTO.getTradeStatus(), TradeStatus.ERSTELLT);
        Assert.assertNotNull(verkaufTradeJobDTO.getMenge());
        Assert.assertNull(verkaufTradeJobDTO.getTradeVersuchAm());
        Assert.assertEquals(verkaufTradeJobDTO.getTradeTyp(), TradeTyp.VERKAUF);
        Assert.assertEquals(verkaufTradeJobDTO.getTradeAktionEnum(), TradeAktionEnum.ORDER_VERKAUF);


        //-----------------------------------------------------------------
        //Verkauf wird ueberprueft
        //FuerTestzwecke wird die Waherung umgesetzt, Spitzenwert wird gesetzt um Abwaertstrend zu simulieren
        verkaufTradeJobDTO.setCryptoWaehrung(TradeJobTestDataHelper.WJC_VERKAUF);
        verkaufTradeJobDTO.setSpitzenwert(TradeUtil.getBigDecimal(1.5));
        verkaufTradeJobDTO.setGanzZahlig(true);
        wechselTradeJobService.aktualisiereWechselTradeJob(verkaufTradeJobDTO);
        cleanInstances();

        wechselTradeJobDTOList.clear();
        wechselTradeJobDTOList.add(verkaufTradeJobDTO);
        tradeJobVerwaltungService.verarbeiteBeobachtungsAufgaben(new ArrayList<>(), wechselTradeJobDTOList, TradingPlattform.BINANCE);
        cleanInstances();


        WechselTradeJobDTO verkaufOrderTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(wechselTradeJobId);
        Assert.assertNotNull(verkaufOrderTradeJobDTO);
        Assert.assertNotNull(verkaufOrderTradeJobDTO.getSpitzenwert());
        Assert.assertNotNull(verkaufOrderTradeJobDTO.getLetztwert());
        Assert.assertEquals(verkaufOrderTradeJobDTO.getTradeStatus(), TradeStatus.TRADE_PRUEFUNG_VERKAUF);
        Assert.assertNotNull(verkaufOrderTradeJobDTO.getMenge());
        Assert.assertNotNull(verkaufOrderTradeJobDTO.getTradeVersuchAm());

        //Zu diesem Job sollte es nun eine VerkaufAktion geben
        alleTradeAktionen = tradeAktionService.holeAlleTradeAktionen();
        Assert.assertNotNull(alleTradeAktionen);

        relevanteTradeAktionen = tradeAktionService.filterTradeAktionDTOList(alleTradeAktionen, kaufOrderTradeJobDTO.getId());
        Assert.assertNotNull(relevanteTradeAktionen);
        Assert.assertEquals(relevanteTradeAktionen.size(), 2);

        TradeAktionDTO verkaufAktionDTO = null;
        for (TradeAktionDTO tradeAktionDTO : relevanteTradeAktionen) {
            if (tradeAktionDTO.getTradeTyp() == TradeTyp.VERKAUF) {
                verkaufAktionDTO = tradeAktionDTO;
            }
        }

        Assert.assertNotNull(verkaufAktionDTO);
        Assert.assertEquals(verkaufAktionDTO.getCryptoWaehrung(), TradeJobTestDataHelper.WJC_VERKAUF);
        Assert.assertEquals(verkaufAktionDTO.getCryptoWaehrungReferenz(), TradeJobTestDataHelper.BTC);
        Assert.assertTrue(verkaufOrderTradeJobDTO.getMenge().compareTo(verkaufAktionDTO.getMenge()) == 1); //Menge an NeuCoins die verkauft werden soll, wegen Runden auf ganze Coins ein bisschen weniger
        Assert.assertTrue(verkaufOrderTradeJobDTO.getLetztwert().compareTo(verkaufAktionDTO.getPreisProEinheit()) == 1);
        Assert.assertTrue(verkaufOrderTradeJobDTO.getVorgesehenerVerkaufwert().equals(verkaufAktionDTO.getPreisProEinheit()));
        Assert.assertEquals(verkaufAktionDTO.getTradeStatus(), TradeStatus.TRADE_PRUEFUNG_VERKAUF);
        Assert.assertNull(verkaufAktionDTO.getErledigtAm());

        //-----------------------------------------------------------------
        //Verkauf erfolgreich abgeschlossen -> es wird auf Verkauf gewechselt

        List<TradeAktionDTO> verkaufTradeAktionList = new ArrayList<>();
        verkaufTradeAktionList.add(verkaufAktionDTO);
        tradeAktionVerwaltungService.verarbeiteTradeAktionAufgaben(verkaufTradeAktionList, TradingPlattform.BINANCE);
        cleanInstances();


        TradeAktionDTO verkaufAbgeschlossenAktionDTO = tradeAktionService.holeTradeAktion(verkaufAktionDTO.getId());
        Assert.assertNotNull(verkaufAbgeschlossenAktionDTO);

        Assert.assertEquals(verkaufAbgeschlossenAktionDTO.getCryptoWaehrung(), TradeJobTestDataHelper.WJC_VERKAUF);
        Assert.assertEquals(verkaufAbgeschlossenAktionDTO.getCryptoWaehrungReferenz(), TradeJobTestDataHelper.BTC);
        Assert.assertEquals(verkaufAbgeschlossenAktionDTO.getMenge(), verkaufAktionDTO.getMenge());
        Assert.assertEquals(verkaufAbgeschlossenAktionDTO.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);
        Assert.assertNotNull(verkaufAbgeschlossenAktionDTO.getErledigtAm());


        WechselTradeJobDTO kaufTradeJobDTO = wechselTradeJobService.holeWechselTradeJob(wechselTradeJobId);
        Assert.assertNotNull(kaufTradeJobDTO);
        Assert.assertNull(kaufTradeJobDTO.getZielwert());
        Assert.assertEquals(kaufTradeJobDTO.getTradeStatus(), TradeStatus.ERSTELLT);
        Assert.assertNull(kaufTradeJobDTO.getMenge());
        Assert.assertNull(kaufTradeJobDTO.getTradeVersuchAm());
        Assert.assertEquals(kaufTradeJobDTO.getTradeTyp(), TradeTyp.KAUF);
        Assert.assertEquals(kaufTradeJobDTO.getTradeAktionEnum(), TradeAktionEnum.ORDER_KAUF);
    }

}
