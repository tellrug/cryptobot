package at.vulperium.cryptobot.config;

import at.vulperium.cryptobot.ContainerTest;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public class ConfigValueTest extends ContainerTest {

    @Test
    public void testConfigValue_WertVorhanden() {
        String valueAsString = ConfigResolver.getPropertyValue("testKey");
        Assert.assertEquals(valueAsString, "testWert");
    }

    @Test
    public void testConfigValue_WertNichtVorhanden() {
        String valueAsString = ConfigResolver.getPropertyValue("nichtVorhanden");
        Assert.assertNull(valueAsString);
    }
}
