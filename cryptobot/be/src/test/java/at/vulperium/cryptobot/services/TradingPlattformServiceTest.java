package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.HoldingOrderDTO;
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
        HoldingOrderDTO holdingOrderDTO= tradingPlattformService.ermittleHoldingOrderInformationen(TradingPlattform.BINANCE);
        Assert.assertNotNull(holdingOrderDTO);
        org.testng.Assert.assertEquals(holdingOrderDTO.getTradingPlattform(), TradingPlattform.BINANCE);
        Assert.assertTrue(holdingOrderDTO.getHoldingMap().containsKey("LTC"));
    }

}
