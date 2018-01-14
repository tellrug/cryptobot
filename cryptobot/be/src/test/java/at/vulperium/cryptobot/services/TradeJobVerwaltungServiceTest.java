package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import at.vulperium.cryptobot.services.jobs.TradeJobVerwaltungService;
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
        TradeJobDTO verkaufTradeJobDTO1 = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        verkaufTradeJobDTO1.setCryptoWaehrung("VK1");
        Long verkaufTradeJobDTO1id = tradeJobService.speichereTradeJob(verkaufTradeJobDTO1);
        Assert.assertNotNull(verkaufTradeJobDTO1id);

        TradeJobDTO verkaufTradeJobDTO2 = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        verkaufTradeJobDTO2.setCryptoWaehrung("VK2");
        Long verkaufTradeJobDTO2id = tradeJobService.speichereTradeJob(verkaufTradeJobDTO2);
        Assert.assertNotNull(verkaufTradeJobDTO2id);

        //Offenen KaufTradejob anlegen
        TradeJobDTO kaufTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        kaufTradeJobDTO.setCryptoWaehrung("K1");
        Long kaufTradeJobDTOid = tradeJobService.speichereTradeJob(kaufTradeJobDTO);
        Assert.assertNotNull(kaufTradeJobDTOid);
        cleanInstances();

        List<TradeJobDTO> tradeJobDTOList = new ArrayList<>();
        tradeJobDTOList.add(verkaufTradeJobDTO1);
        tradeJobDTOList.add(verkaufTradeJobDTO2);
        tradeJobDTOList.add(kaufTradeJobDTO);

        tradeJobVerwaltungService.verarbeiteTradeAufgaben(tradeJobDTOList, new ArrayList<>(), TradingPlattform.BINANCE);
        cleanInstances();

        TradeJobDTO checkVK1 = tradeJobService.holeTradeJob(verkaufTradeJobDTO1.getId());
        Assert.assertNotNull(checkVK1);
        Assert.assertNotNull(checkVK1.getErledigtAm());
        Assert.assertEquals(checkVK1.getTradeAktionEnum(), TradeAktionEnum.VERKAUF_ZIEL);
        Assert.assertEquals(checkVK1.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);

        TradeJobDTO checkVK2 = tradeJobService.holeTradeJob(verkaufTradeJobDTO2.getId());
        Assert.assertNotNull(checkVK2);
        Assert.assertNull(checkVK2.getErledigtAm());
        Assert.assertEquals(checkVK2.getTradeAktionEnum(), TradeAktionEnum.VERKAUF_ZIEL);
        Assert.assertEquals(checkVK2.getTradeStatus(), TradeStatus.BEOBACHTUNG);


        TradeJobDTO checkK1 = tradeJobService.holeTradeJob(kaufTradeJobDTO.getId());
        Assert.assertNotNull(checkK1);
        Assert.assertNotNull(checkK1.getErledigtAm());
        Assert.assertEquals(checkK1.getTradeAktionEnum(), TradeAktionEnum.KAUF_ZIEL);
        Assert.assertEquals(checkK1.getTradeStatus(), TradeStatus.ABGESCHLOSSEN);
    }
}
