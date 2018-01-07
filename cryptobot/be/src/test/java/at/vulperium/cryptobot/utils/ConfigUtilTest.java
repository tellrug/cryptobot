package at.vulperium.cryptobot.utils;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.config.ConfigValue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Ace on 08.01.2018.
 */
public class ConfigUtilTest extends ContainerTest {

    @Test
    public void testConfigUtil() {
        ConfigValue configValue = new ConfigValue("timerIntervall");
        Assert.assertEquals(configValue.get(), "1");

        Assert.assertEquals(ConfigUtil.toInteger(configValue), 1);
    }
}
