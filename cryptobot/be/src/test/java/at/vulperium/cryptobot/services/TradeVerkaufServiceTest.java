package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import at.vulperium.cryptobot.services.trades.TradeVerkaufService;
import at.vulperium.cryptobot.testdatahelper.TradeJobTestDataHelper;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class TradeVerkaufServiceTest extends ContainerTest {

    private @Inject TradeVerkaufService tradeVerkaufService;
    private @Inject TradeJobService tradeJobService;

    private TradeJobTestDataHelper tradeJobTestDataHelper = new TradeJobTestDataHelper();

    @Test
    public void testVerarbeiteVerkaufAktion_zielNichtErreicht_keineAktionDurchfuehren() {

        //Offenen Tradejob anlegen
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.006));


        tradeVerkaufService.verarbeiteVerkaufAktion(simpelTradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        SimpelTradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), simpelTradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getErledigtAm(), simpelTradeJobDTO.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), simpelTradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.ERSTELLT);
    }

    @Test
    public void testVerarbeiteVerkaufAktion_zielErreicht_beobachteAufwaertstrend() {

        //Offenen Tradejob anlegen
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.010));


        tradeVerkaufService.verarbeiteVerkaufAktion(simpelTradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        SimpelTradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), simpelTradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getErledigtAm(), simpelTradeJobDTO.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), simpelTradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.BEOBACHTUNG);

    }

    @Test
    public void testVerarbeiteVerkaufAktion_zielErreicht_verkaufen() {

        //Offenen Tradejob anlegen
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.008));


        tradeVerkaufService.verarbeiteVerkaufAktion(simpelTradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        SimpelTradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), simpelTradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getZielwert(), simpelTradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);
        Assert.assertNotNull(checkTradeJob.getErledigtAm());
    }

    @Test
    public void testVerarbeiteVerkaufAktion_nichtVerkaufen() {

        //Offenen Tradejob anlegen
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.0065));


        tradeVerkaufService.verarbeiteVerkaufAktion(simpelTradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        SimpelTradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), simpelTradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getZielwert(), simpelTradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.BEOBACHTUNG);
        Assert.assertNull(checkTradeJob.getErledigtAm());
    }
}
