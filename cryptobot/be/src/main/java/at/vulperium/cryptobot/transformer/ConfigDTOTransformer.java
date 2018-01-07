package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.ConfigDTO;
import at.vulperium.cryptobot.entities.CryptobotConfig;
import at.vulperium.cryptobot.enums.ConfigEnum;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
@ApplicationScoped
public class ConfigDTOTransformer implements TransformBothDirections<CryptobotConfig, ConfigDTO> {

    private final static Logger logger = LoggerFactory.getLogger(ConfigDTOTransformer.class);

    @Override
    public CryptobotConfig transformInverse(ConfigDTO source) {
        throw new RuntimeException("Es kann keine neue Konfiguration angelegt werden!");
    }

    @Override
    public CryptobotConfig transformInverse(ConfigDTO source, CryptobotConfig target) {
        target.setValue(source.getConfigValue());
        return target;
    }

    @Override
    public ConfigDTO transform(CryptobotConfig source) {
        ConfigEnum configEnum = ConfigEnum.getByKey(source.getId());
        if (configEnum == null) {
            logger.warn("Der ConfigEintrag zu configKey={} ist nicht im ConfigEnum enthalten!", source.getId());
            return null;
        }

        return new ConfigDTO(configEnum, source.getValue());
    }

    @Override
    public ConfigDTO transform(CryptobotConfig source, ConfigDTO target) {
        target.setConfigValue(source.getValue());
        return target;
    }
}
