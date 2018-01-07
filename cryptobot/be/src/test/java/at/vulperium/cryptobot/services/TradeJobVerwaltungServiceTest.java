package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.testdatahelper.TradeJobTestDataHelper;
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

    private TradeJobTestDataHelper tradeJobTestDataHelper = new TradeJobTestDataHelper();

    @Test
    public void testVerarbeiteTradeAufgaben() {
        //Offenen VerkaufTradejob anlegen
        TradeJobDTO verkaufTradeJobDTO1 = tradeJobTestDataHelper.erzeugeTradeJobDTOVerkauf(true);
        verkaufTradeJobDTO1.setCryptoWaehrung("VK1");
        verkaufTradeJobDTO1.setTradeJobStatus(TradeJobStatus.VERKAUF_ZIEL);
        Long verkaufTradeJobDTO1id = tradeJobService.speichereTradeJob(verkaufTradeJobDTO1);
        Assert.assertNotNull(verkaufTradeJobDTO1id);

        TradeJobDTO verkaufTradeJobDTO2 = tradeJobTestDataHelper.erzeugeTradeJobDTOVerkauf();
        verkaufTradeJobDTO2.setCryptoWaehrung("VK2");
        Long verkaufTradeJobDTO2id = tradeJobService.speichereTradeJob(verkaufTradeJobDTO2);
        Assert.assertNotNull(verkaufTradeJobDTO2id);

        //Offenen KaufTradejob anlegen
        TradeJobDTO kaufTradeJobDTO = tradeJobTestDataHelper.erzeugeTradeJobDTOKauf(true);
        kaufTradeJobDTO.setCryptoWaehrung("K1");
        kaufTradeJobDTO.setTradeJobStatus(TradeJobStatus.KAUF_ZIEL);
        Long kaufTradeJobDTOid = tradeJobService.speichereTradeJob(kaufTradeJobDTO);
        Assert.assertNotNull(kaufTradeJobDTOid);
        cleanInstances();

        List<TradeJobDTO> tradeJobDTOList = new ArrayList<>();
        tradeJobDTOList.add(verkaufTradeJobDTO1);
        tradeJobDTOList.add(verkaufTradeJobDTO2);
        tradeJobDTOList.add(kaufTradeJobDTO);

        tradeJobVerwaltungService.verarbeiteTradeAufgaben(tradeJobDTOList, TradingPlattform.BINANCE);
        cleanInstances();

        TradeJobDTO checkVK1 = tradeJobService.holeTradeJob(verkaufTradeJobDTO1.getId());
        Assert.assertNotNull(checkVK1);
        Assert.assertNotNull(checkVK1.getErledigtAm());
        Assert.assertEquals(checkVK1.getTradeJobStatus(), TradeJobStatus.VERKAUF_ZIEL_ERREICHT);

        TradeJobDTO checkVK2 = tradeJobService.holeTradeJob(verkaufTradeJobDTO2.getId());
        Assert.assertNotNull(checkVK2);
        Assert.assertNull(checkVK2.getErledigtAm());
        Assert.assertEquals(checkVK2.getTradeJobStatus(), TradeJobStatus.VERKAUF_ZIEL);


        TradeJobDTO checkK1 = tradeJobService.holeTradeJob(kaufTradeJobDTO.getId());
        Assert.assertNotNull(checkK1);
        Assert.assertNotNull(checkK1.getErledigtAm());
        Assert.assertEquals(checkK1.getTradeJobStatus(), TradeJobStatus.KAUF_ZIEL_ERREICHT);
    }
}
