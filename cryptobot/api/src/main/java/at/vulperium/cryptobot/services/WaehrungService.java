package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.WaehrungKurzDTO;

import java.util.List;

/**
 * Created by Ace on 26.12.2017.
 */
public interface WaehrungService {

    List<WaehrungKurzDTO> holeAlleWaehrungen();

    WaehrungKurzDTO holeWaehrungKurzDTO(Long cryptoWaehrungId);

    Long speichereCryptoWaehrung(WaehrungKurzDTO waehrungKurzDTO);

    Boolean aktualisiereWaehrung(WaehrungKurzDTO waehrungKurzDTO);

    List<WaehrungKurzDTO> filterWaehrungList(List<WaehrungKurzDTO> waehrungKurzDTOList, boolean gueltig);

    Boolean setzeWaehrungUngueltig(Long cryptoWaehrungId);
}
