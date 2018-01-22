package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.jobs.TradeJobService;
import at.vulperium.cryptobot.testdatahelper.TradeJobTestDataHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class TradeJobServiceTest extends ContainerTest {

    private @Inject TradeJobService tradeJobService;

    private TradeJobTestDataHelper tradeJobTestDataHelper = new TradeJobTestDataHelper();

    @Test
    public void testSpeichereTradeJob() {
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();

        Assert.assertNotNull(id);
    }


    @Test
    public void testHoleAlleTradeJobs() {
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        List<SimpelTradeJobDTO> simpelTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(simpelTradeJobDTOList);
        Assert.assertTrue(!simpelTradeJobDTOList.isEmpty());

        boolean checked = checkListNachId(id, simpelTradeJobDTOList);
        Assert.assertTrue(checked);
    }

    @Test
    public void testFilterTradeJobs() {
        SimpelTradeJobDTO verkaufSimpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ABGESCHLOSSEN);

        SimpelTradeJobDTO kaufSimpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.ERSTELLT);
        Long verkaufId = tradeJobService.speichereTradeJob(verkaufSimpelTradeJobDTO);
        Long kaufId = tradeJobService.speichereTradeJob(kaufSimpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(verkaufId);
        Assert.assertNotNull(kaufId);

        List<SimpelTradeJobDTO> alleSimpelTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(alleSimpelTradeJobDTOList);
        Assert.assertTrue(!alleSimpelTradeJobDTOList.isEmpty());

        //Filtern nach TradeStatusTyp
        List<SimpelTradeJobDTO> kaufTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, TradeTyp.KAUF);
        boolean checked = checkListNachId(kaufId, kaufTradejobDTOListSimpel);
        Assert.assertTrue(checked);
        checked = checkListNachId(verkaufId, kaufTradejobDTOListSimpel);
        Assert.assertFalse(checked);

        List<SimpelTradeJobDTO> verkaufTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, TradeTyp.VERKAUF);
        checked = checkListNachId(kaufId, verkaufTradejobDTOListSimpel);
        Assert.assertFalse(checked);
        checked = checkListNachId(verkaufId, verkaufTradejobDTOListSimpel);
        Assert.assertTrue(checked);

        //Filtern nach erledigt
        List<SimpelTradeJobDTO> erledigtTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, true);
        checked = checkListNachId(verkaufId, erledigtTradejobDTOListSimpel);
        Assert.assertTrue(checked);
        checked = checkListNachId(kaufId, erledigtTradejobDTOListSimpel);
        Assert.assertFalse(checked);

        List<SimpelTradeJobDTO> nichtErledigtTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, false);
        checked = checkListNachId(kaufId, nichtErledigtTradejobDTOListSimpel);
        Assert.assertTrue(checked);
        checked = checkListNachId(verkaufId, nichtErledigtTradejobDTOListSimpel);
        Assert.assertFalse(checked);

        //Filtern nach TradingPlattform
        List<SimpelTradeJobDTO> binanceTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, TradingPlattform.BINANCE);
        checked = checkListNachId(verkaufId, binanceTradejobDTOListSimpel);
        Assert.assertTrue(checked);
        checked = checkListNachId(kaufId, binanceTradejobDTOListSimpel);
        Assert.assertTrue(checked);

        List<SimpelTradeJobDTO> allTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, TradingPlattform.ALLE);
        checked = checkListNachId(kaufId, allTradejobDTOListSimpel);
        Assert.assertFalse(checked);
        checked = checkListNachId(verkaufId, allTradejobDTOListSimpel);
        Assert.assertFalse(checked);
    }

    @Test
    public void testErledigeTradeJob() {
        SimpelTradeJobDTO simpelTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        Long id = tradeJobService.speichereTradeJob(simpelTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        List<SimpelTradeJobDTO> alleSimpelTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(alleSimpelTradeJobDTOList);
        Assert.assertTrue(!alleSimpelTradeJobDTOList.isEmpty());

        List<SimpelTradeJobDTO> nichtErledigtTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, false);
        boolean checked = checkListNachId(id, nichtErledigtTradejobDTOListSimpel);
        Assert.assertTrue(checked);

        //Erledigen des Jobs
        boolean erledigt = tradeJobService.erledigeTradeJob(id);
        cleanInstances();
        Assert.assertTrue(erledigt);

        alleSimpelTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(alleSimpelTradeJobDTOList);
        Assert.assertTrue(!alleSimpelTradeJobDTOList.isEmpty());

        List<SimpelTradeJobDTO> erledigtTradejobDTOListSimpel = tradeJobService.filterTradeJobDTOList(alleSimpelTradeJobDTOList, true);
        checked = checkListNachId(id, erledigtTradejobDTOListSimpel);
        Assert.assertTrue(checked);
    }


    private boolean checkListNachId(Long id, List<SimpelTradeJobDTO> simpelTradeJobDTOList) {
        for (SimpelTradeJobDTO jobDTO : simpelTradeJobDTOList) {
            if (jobDTO.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }
}
