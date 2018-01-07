package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.ConfigDTO;

import java.util.List;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public interface ConfigService {

    List<ConfigDTO> holeAlleConfigDTOs();

    List<ConfigDTO> holeAlleAnonymeConfigDTOs();
}
