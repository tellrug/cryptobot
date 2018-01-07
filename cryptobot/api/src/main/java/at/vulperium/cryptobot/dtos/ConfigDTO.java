package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.ConfigEnum;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class ConfigDTO implements Serializable {

    private ConfigEnum configEnum;
    private String configValue;

    public ConfigDTO(ConfigEnum configEnum, String configValue) {
        this.configEnum = configEnum;
        this.configValue = configValue;
    }

    public ConfigEnum getConfigEnum() {
        return configEnum;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
