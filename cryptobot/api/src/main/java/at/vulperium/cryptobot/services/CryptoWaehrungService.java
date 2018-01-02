package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.CryptoWaehrungKurzDTO;

import java.util.List;

/**
 * Created by Ace on 26.12.2017.
 */
public interface CryptoWaehrungService {

    List<CryptoWaehrungKurzDTO> holeAlleCryptoWaehrungen();

    CryptoWaehrungKurzDTO holeCryptoWaehrung(Long cryptoWaehrungId);

    Long speichereCryptoWaehrung(CryptoWaehrungKurzDTO cryptoWaehrungKurzDTO);

    Boolean aktualisiereCryptoWaehrung(CryptoWaehrungKurzDTO cryptoWaehrungKurzDTO);

    List<CryptoWaehrungKurzDTO> filterCryptoWaehrungList(List<CryptoWaehrungKurzDTO> cryptoWaehrungKurzDTOList, boolean gueltig);

    Boolean setzeCryptoWaehrungUngueltig(Long cryptoWaehrungId);
}
