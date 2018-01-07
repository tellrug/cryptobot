package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobStatus;
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
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOVerkauf();
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.007));


        tradeVerkaufService.verarbeiteVerkaufAktion(tradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        TradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeJobStatus(), tradeJobDTO.getTradeJobStatus());
        Assert.assertEquals(checkTradeJob.getErledigtAm(), tradeJobDTO.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
    }

    @Test
    public void testVerarbeiteVerkaufAktion_zielErreicht_beobachteAufwaertstrend() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOVerkauf();
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.010));


        tradeVerkaufService.verarbeiteVerkaufAktion(tradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        TradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeJobStatus(), tradeJobDTO.getTradeJobStatus());
        Assert.assertEquals(checkTradeJob.getErledigtAm(), tradeJobDTO.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
    }

    @Test
    public void testVerarbeiteVerkaufAktion_zielErreicht_verkaufen() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOVerkauf();
        tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.010));
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.009));


        tradeVerkaufService.verarbeiteVerkaufAktion(tradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        TradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeJobStatus(), TradeJobStatus.VERKAUF_ZIEL_ERREICHT);
        Assert.assertNotNull(checkTradeJob.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
    }

    @Test
    public void testVerarbeiteVerkaufAktion_nichtVerkaufen() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOVerkauf();
        tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.010));
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.003));


        tradeVerkaufService.verarbeiteVerkaufAktion(tradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        TradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeJobStatus(), tradeJobDTO.getTradeJobStatus());
        Assert.assertNull(checkTradeJob.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
    }
}
