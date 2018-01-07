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
public class TradeKaufServiceTest extends ContainerTest {

    private @Inject TradeKaufService tradeKaufService;
    private @Inject TradeJobService tradeJobService;

    private TradeJobTestDataHelper tradeJobTestDataHelper = new TradeJobTestDataHelper();

    @Test
    public void testVerarbeiteKaufAktion_zielNichtErreicht_keineAktionDurchfuehren() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOKauf(false);
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.0035));


        tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
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
    public void testVerarbeiteKaufAktion_zielErreicht_beobachteTrend() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOKauf(false);
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.002));

        tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
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
    public void testVerarbeiteKaufAktion_zielErreicht_kaufen() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOKauf(false);
        tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.001));
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.002));


        tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        TradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeJobStatus(), TradeJobStatus.KAUF_ZIEL_ERREICHT);
        Assert.assertNotNull(checkTradeJob.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
    }

    @Test
    public void testVerarbeiteKaufAktion_nichtKaufen() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOKauf(false);
        tradeJobDTO.setLetztwert(TradeUtil.getBigDecimal(0.001));
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.004));


        tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
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
