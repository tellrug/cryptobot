package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.CryptoWaehrungKurzDTO;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;

public class CryptoWaehrungServiceTest extends ContainerTest {

    private @Inject CryptoWaehrungService cryptoWaehrungService;

    @Test
    public void testSpeichereCryptoWaehrung() {

        CryptoWaehrungKurzDTO cryptoWaehrungKurzDTO = new CryptoWaehrungKurzDTO();
        cryptoWaehrungKurzDTO.setGueltig(true);
        cryptoWaehrungKurzDTO.setBezeichnung("TestCoin");
        cryptoWaehrungKurzDTO.setKurzbezeichnung("TC");

        Long id = cryptoWaehrungService.speichereCryptoWaehrung(cryptoWaehrungKurzDTO);
        cleanInstances();

        Assert.assertNotNull(id);
        CryptoWaehrungKurzDTO testCryptoWaehrungKurzDTO = cryptoWaehrungService.holeCryptoWaehrung(id);

        Assert.assertNotNull(testCryptoWaehrungKurzDTO);
        Assert.assertEquals(testCryptoWaehrungKurzDTO.getId(), id);
        Assert.assertEquals(testCryptoWaehrungKurzDTO.getBezeichnung(), cryptoWaehrungKurzDTO.getBezeichnung());
        Assert.assertEquals(testCryptoWaehrungKurzDTO.getKurzbezeichnung(), cryptoWaehrungKurzDTO.getKurzbezeichnung());
        Assert.assertEquals(testCryptoWaehrungKurzDTO.isGueltig(), cryptoWaehrungKurzDTO.isGueltig());
    }
}
