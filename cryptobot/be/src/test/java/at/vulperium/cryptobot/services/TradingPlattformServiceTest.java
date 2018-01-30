package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import junit.framework.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by 02ub0400 on 17.01.2018.
 */
public class TradingPlattformServiceTest extends ContainerTest {

    private @Inject TradingPlattformService tradingPlattformService;

    @Test
    public void testTradingPlattform() {
        //Laden der WsCryptoCoinMap
        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = tradingPlattformService.holeWSCryptoCoinMap(TradingPlattform.BINANCE);
        Assert.assertNotNull(wsCryptoCoinDTOMap);
        Assert.assertTrue(wsCryptoCoinDTOMap.containsKey("LTCBTC"));


        //Laden der HoldingOrderInfos
        HoldingDTO holdingDTO = tradingPlattformService.ermittleHoldingInformationen(TradingPlattform.BINANCE);
        Assert.assertNotNull(holdingDTO);
        org.testng.Assert.assertEquals(holdingDTO.getTradingPlattform(), TradingPlattform.BINANCE);
        Assert.assertTrue(holdingDTO.getHoldingMap().containsKey("LTC"));
    }

}
