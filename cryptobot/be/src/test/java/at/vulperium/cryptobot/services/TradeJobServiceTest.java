package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
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
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();

        Assert.assertNotNull(id);
    }


    @Test
    public void testHoleAlleTradeJobs() {
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ERSTELLT);
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        List<TradeJobDTO> tradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(tradeJobDTOList);
        Assert.assertTrue(!tradeJobDTOList.isEmpty());

        boolean checked = checkListNachId(id, tradeJobDTOList);
        Assert.assertTrue(checked);
    }

    @Test
    public void testFilterTradeJobs() {
        TradeJobDTO verkaufTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ABGESCHLOSSEN);

        TradeJobDTO kaufTradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.ERSTELLT);
        Long verkaufId = tradeJobService.speichereTradeJob(verkaufTradeJobDTO);
        Long kaufId = tradeJobService.speichereTradeJob(kaufTradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(verkaufId);
        Assert.assertNotNull(kaufId);

        List<TradeJobDTO> alleTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(alleTradeJobDTOList);
        Assert.assertTrue(!alleTradeJobDTOList.isEmpty());

        //Filtern nach TradeStatusTyp
        List<TradeJobDTO> kaufTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, TradeTyp.KAUF);
        boolean checked = checkListNachId(kaufId, kaufTradejobDTOList);
        Assert.assertTrue(checked);
        checked = checkListNachId(verkaufId, kaufTradejobDTOList);
        Assert.assertFalse(checked);

        List<TradeJobDTO> verkaufTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, TradeTyp.VERKAUF);
        checked = checkListNachId(kaufId, verkaufTradejobDTOList);
        Assert.assertFalse(checked);
        checked = checkListNachId(verkaufId, verkaufTradejobDTOList);
        Assert.assertTrue(checked);

        //Filtern nach erledigt
        List<TradeJobDTO> erledigtTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, true);
        checked = checkListNachId(verkaufId, erledigtTradejobDTOList);
        Assert.assertTrue(checked);
        checked = checkListNachId(kaufId, erledigtTradejobDTOList);
        Assert.assertFalse(checked);

        List<TradeJobDTO> nichtErledigtTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, false);
        checked = checkListNachId(kaufId, nichtErledigtTradejobDTOList);
        Assert.assertTrue(checked);
        checked = checkListNachId(verkaufId, nichtErledigtTradejobDTOList);
        Assert.assertFalse(checked);

        //Filtern nach TradingPlattform
        List<TradeJobDTO> binanceTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, TradingPlattform.BINANCE);
        checked = checkListNachId(verkaufId, binanceTradejobDTOList);
        Assert.assertTrue(checked);
        checked = checkListNachId(kaufId, binanceTradejobDTOList);
        Assert.assertTrue(checked);

        List<TradeJobDTO> allTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, TradingPlattform.ALLE);
        checked = checkListNachId(kaufId, allTradejobDTOList);
        Assert.assertFalse(checked);
        checked = checkListNachId(verkaufId, allTradejobDTOList);
        Assert.assertFalse(checked);
    }

    @Test
    public void testErledigeTradeJob() {
        TradeJobDTO tradeJobDTO = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.BEOBACHTUNG);
        Long id = tradeJobService.speichereTradeJob(tradeJobDTO);
        cleanInstances();
        Assert.assertNotNull(id);

        List<TradeJobDTO> alleTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(alleTradeJobDTOList);
        Assert.assertTrue(!alleTradeJobDTOList.isEmpty());

        List<TradeJobDTO> nichtErledigtTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, false);
        boolean checked = checkListNachId(id, nichtErledigtTradejobDTOList);
        Assert.assertTrue(checked);

        //Erledigen des Jobs
        boolean erledigt = tradeJobService.erledigeTradeJob(id);
        cleanInstances();
        Assert.assertTrue(erledigt);

        alleTradeJobDTOList = tradeJobService.holeAlleTradeJobs();
        Assert.assertNotNull(alleTradeJobDTOList);
        Assert.assertTrue(!alleTradeJobDTOList.isEmpty());

        List<TradeJobDTO> erledigtTradejobDTOList = tradeJobService.filterTradeJobDTOList(alleTradeJobDTOList, true);
        checked = checkListNachId(id, erledigtTradejobDTOList);
        Assert.assertTrue(checked);
    }


    private boolean checkListNachId(Long id, List<TradeJobDTO> tradeJobDTOList) {
        for (TradeJobDTO jobDTO : tradeJobDTOList) {
            if (jobDTO.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }
}
