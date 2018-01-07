package at.vulperium.cryptobot.utils;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.enums.Trend;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class TrendEnumTest extends ContainerTest {

    private BigDecimal value1 = TradeUtil.getBigDecimal(0.01);
    private BigDecimal value2 = TradeUtil.getBigDecimal(0.05);

    @Test
    public void testErmittleAbsolutTrend() {
        Trend trendAufwaerts = Trend.ermittleAbsolutTrend(value2, value1);
        Assert.assertEquals(trendAufwaerts, Trend.AUFWAERTS);

        Trend trendAbwaerts = Trend.ermittleAbsolutTrend(value1, value2);
        Assert.assertEquals(trendAbwaerts, Trend.ABWAERTS);

        Trend trendKonstant = Trend.ermittleAbsolutTrend(value1, value1);
        Assert.assertEquals(trendKonstant, Trend.KONSTANT);

        Trend trendFalsch = Trend.ermittleAbsolutTrend(value1, null);
        Assert.assertNull(trendFalsch);
    }
}
