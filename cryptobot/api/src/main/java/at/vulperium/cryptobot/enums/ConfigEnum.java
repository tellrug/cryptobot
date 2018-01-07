package at.vulperium.cryptobot.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public enum ConfigEnum {

    TIMER_INTERVALL("timerIntervall", "Intervall in Min", true),
    BENACHRICHTIGUNG_MAIL("mailBenachrichtigung", "Benachrichtigung Mail", true),
    ;


    ConfigEnum(String key, String bezeichnung, boolean anonym) {
        this.key = key;
        this.bezeichnung = bezeichnung;
        this.anonym = anonym;
    }

    private String key;
    private String bezeichnung;
    private boolean anonym;

    public String getKey() {
        return key;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public boolean isAnonym() {
        return anonym;
    }

    public static ConfigEnum getByKey(String key) {
        for (ConfigEnum configEnum : ConfigEnum.values()) {
            if (configEnum.getKey().equals(key)) {
                return configEnum;
            }
        }
        //Config ist nicht im Enum enthalten
        return null;
    }

    public static List<ConfigEnum> getAnonymeConfigEnumList() {
        List<ConfigEnum> configEnumList = new ArrayList<>();
        for (ConfigEnum configEnum : ConfigEnum.values()) {
            if (configEnum.isAnonym()) {
                configEnumList.add(configEnum);
            }
        }

        return configEnumList;
    }
}
