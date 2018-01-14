package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import at.vulperium.cryptobot.services.trades.TradeKaufService;
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
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.ERSTELLT);
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
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), tradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getErledigtAm(), tradeJobDTO.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.ERSTELLT);
    }

    @Test
    public void testVerarbeiteKaufAktion_zielErreicht_beobachteTrend() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        //erzeugen
        WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
        wsCryptoCoinDTO.setSymbol(TradeJobTestDataHelper.SYMBOL);
        wsCryptoCoinDTO.setPrice(TradeUtil.getBigDecimal(0.001));

        tradeKaufService.verarbeiteKaufAktion(tradeJobDTO, wsCryptoCoinDTO);
        cleanInstances();

        //Holen des TradeJobs
        TradeJobDTO checkTradeJob = tradeJobService.holeTradeJob(id);
        Assert.assertNotNull(checkTradeJob);
        Assert.assertEquals(checkTradeJob.getLetztwert(), wsCryptoCoinDTO.getPrice());
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), tradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getErledigtAm(), tradeJobDTO.getErledigtAm());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.BEOBACHTUNG);
    }

    @Test
    public void testVerarbeiteKaufAktion_zielErreicht_kaufen() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.BEOBACHTUNG);
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
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), checkTradeJob.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);
        Assert.assertNotNull(checkTradeJob.getErledigtAm());
    }

    @Test
    public void testVerarbeiteKaufAktion_nichtKaufen() {

        //Offenen Tradejob anlegen
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.BEOBACHTUNG);
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
        Assert.assertEquals(checkTradeJob.getTradeAktionEnum(), tradeJobDTO.getTradeAktionEnum());
        Assert.assertEquals(checkTradeJob.getZielwert(), tradeJobDTO.getZielwert());
        Assert.assertEquals(checkTradeJob.getTradeStatus(), TradeStatus.BEOBACHTUNG);
        Assert.assertNull(checkTradeJob.getErledigtAm());
    }
}
