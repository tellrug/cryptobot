package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.trades.TradeAktionService;
import at.vulperium.cryptobot.testdatahelper.TradeAktionTestDataHelper;
import org.joda.time.LocalDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by 02ub0400 on 16.01.2018.
 */
public class TradeAktionServiceTest extends ContainerTest {

    private TradeAktionTestDataHelper testDataHelper = new TradeAktionTestDataHelper();

    private @Inject TradeAktionService tradeAktionService;

    @Test
    public void testTradeAktionService() {
        TradeAktionDTO tradeAktionDTO1 = testDataHelper.erzeugeSimpleTradeAktionDTO(TradeTyp.KAUF, TradeStatus.TRADE_PRUEFUNG_KAUF);
        TradeAktionDTO tradeAktionDTO2 = testDataHelper.erzeugeSimpleTradeAktionDTO(TradeTyp.KAUF, TradeStatus.ABGESCHLOSSEN);
        TradeAktionDTO tradeAktionDTO3 = testDataHelper.erzeugeSimpleTradeAktionDTO(TradeTyp.VERKAUF, TradeStatus.TRADE_PRUEFUNG_VERKAUF);


        Long id1 = tradeAktionService.speichereTradeAktion(tradeAktionDTO1);
        Long id2 = tradeAktionService.speichereTradeAktion(tradeAktionDTO2);
        Long id3 = tradeAktionService.speichereTradeAktion(tradeAktionDTO3);
        cleanInstances();

        Assert.assertNotNull(id1);
        Assert.assertNotNull(id2);
        Assert.assertNotNull(id3);

        //Alle Laden
        List<TradeAktionDTO> tradeAktionDTOList = tradeAktionService.holeAlleTradeAktionen();
        Assert.assertNotNull(tradeAktionDTOList);
        Assert.assertTrue(!tradeAktionDTOList.isEmpty());

        Assert.assertTrue(checkListNachId(id1, tradeAktionDTOList));
        Assert.assertTrue(checkListNachId(id2, tradeAktionDTOList));
        Assert.assertTrue(checkListNachId(id3, tradeAktionDTOList));


        //Filtern
        List<TradeAktionDTO> offeneTradeAktionDTOList = tradeAktionService.filterTradeAktionDTOList(tradeAktionDTOList, false);
        Assert.assertTrue(checkListNachId(id1, offeneTradeAktionDTOList));
        Assert.assertTrue(checkListNachId(id3, offeneTradeAktionDTOList));

        List<TradeAktionDTO> kaufTradeAktionDTOList = tradeAktionService.filterTradeAktionDTOList(tradeAktionDTOList, TradeTyp.KAUF);
        Assert.assertTrue(checkListNachId(id1, kaufTradeAktionDTOList));
        Assert.assertTrue(checkListNachId(id2, kaufTradeAktionDTOList));

        List<TradeAktionDTO> binanceTradeAktionDTOList = tradeAktionService.filterTradeAktionDTOList(tradeAktionDTOList, TradingPlattform.BINANCE);
        Assert.assertTrue(checkListNachId(id1, binanceTradeAktionDTOList));
        Assert.assertTrue(checkListNachId(id2, binanceTradeAktionDTOList));
        Assert.assertTrue(checkListNachId(id3, binanceTradeAktionDTOList));

        //Aktualisieren
        tradeAktionDTO1.setTradeStatus(TradeStatus.TRADE_FEHLGESCHLAGEN);
        tradeAktionDTO1.setErledigtAm(LocalDateTime.now());
        tradeAktionService.aktualisiereTradeAktion(tradeAktionDTO1);
        cleanInstances();

        TradeAktionDTO tradeAktionDTO = tradeAktionService.holeTradeAktion(tradeAktionDTO1.getId());
        Assert.assertNotNull(tradeAktionDTO);
        Assert.assertEquals(tradeAktionDTO.getId(), tradeAktionDTO1.getId());
        Assert.assertEquals(tradeAktionDTO.getErledigtAm(), tradeAktionDTO1.getErledigtAm());
        Assert.assertEquals(tradeAktionDTO.getTradeStatus(), tradeAktionDTO1.getTradeStatus());
    }


    private boolean checkListNachId(Long id, List<TradeAktionDTO> tradeAktionDTOList) {
        for (TradeAktionDTO aktionDTO : tradeAktionDTOList) {
            if (aktionDTO.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }
}

