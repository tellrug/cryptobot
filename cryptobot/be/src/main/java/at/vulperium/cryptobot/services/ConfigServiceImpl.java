package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.ConfigDTO;
import at.vulperium.cryptobot.entities.CryptobotConfig;
import at.vulperium.cryptobot.transformer.ConfigDTOTransformer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
@ApplicationScoped
public class ConfigServiceImpl implements ConfigService {

    private @Inject EntityManager em;
    private @Inject ConfigDTOTransformer transformer;

    @Override
    public List<ConfigDTO> holeAlleConfigDTOs() {
        TypedQuery<CryptobotConfig> query = em.createNamedQuery(CryptobotConfig.QRY_FIND_ALL, CryptobotConfig.class);
        List<CryptobotConfig> resultList =query.getResultList();

        List<ConfigDTO> configDTOList = new ArrayList<>();
        for (CryptobotConfig cryptobotConfig : resultList) {
            ConfigDTO configDTO = transformer.transform(cryptobotConfig);
            if (configDTO != null) {
                configDTOList.add(configDTO);
            }
        }
        return configDTOList;
    }

    @Override
    public List<ConfigDTO> holeAlleAnonymeConfigDTOs() {
        List<ConfigDTO> alleConfigDTOs = holeAlleConfigDTOs();
        return alleConfigDTOs.stream().filter(e -> e.getConfigEnum().isAnonym()).collect(Collectors.toList());
    }

}
