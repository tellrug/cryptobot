package at.vulperium.cryptobot.utils;

import at.vulperium.cryptobot.config.ConfigValue;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class ConfigUtil {

    public static boolean toBoolean(ConfigValue configValue) {
        return Boolean.parseBoolean(configValue.get());
    }

    public static int toInteger(ConfigValue configValue) {
        return Integer.parseInt(configValue.get());
    }
}
