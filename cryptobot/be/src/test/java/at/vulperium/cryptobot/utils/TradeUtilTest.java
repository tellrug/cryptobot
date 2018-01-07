package at.vulperium.cryptobot.utils;

import at.vulperium.cryptobot.ContainerTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class TradeUtilTest extends ContainerTest {

    private BigDecimal value1 = TradeUtil.getBigDecimal(0.1);
    private BigDecimal value2 = TradeUtil.getBigDecimal(0.06);

    @Test
    public void testDiffProzent() {
        BigDecimal prozentDiff1 = TradeUtil.diffProzent(value2, value1);
        Assert.assertEquals(prozentDiff1, TradeUtil.getBigDecimal(-40).setScale(4, BigDecimal.ROUND_HALF_EVEN));

        BigDecimal prozentDiff2 = TradeUtil.diffProzent(TradeUtil.getBigDecimal(1.1), TradeUtil.getBigDecimal(1.0));
        Assert.assertEquals(prozentDiff2, TradeUtil.getBigDecimal(10).setScale(4, BigDecimal.ROUND_HALF_EVEN));
    }


    @Test
    public void testDiffAbsolut() {
        BigDecimal absolutDiff1 = TradeUtil.diffAbsolut(value1, value2);
        Assert.assertEquals(absolutDiff1, TradeUtil.getBigDecimal(0.04));

        BigDecimal absolutDiff2 = TradeUtil.diffAbsolut(value2, value1);
        Assert.assertEquals(absolutDiff2, TradeUtil.getBigDecimal(-0.04));
    }
}
