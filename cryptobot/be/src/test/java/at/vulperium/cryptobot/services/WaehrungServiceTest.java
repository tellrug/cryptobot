package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.WaehrungKurzDTO;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;

public class WaehrungServiceTest extends ContainerTest {

    private @Inject WaehrungService waehrungService;

    @Test
    public void testSpeichereCryptoWaehrung() {

        WaehrungKurzDTO waehrungKurzDTO = new WaehrungKurzDTO();
        waehrungKurzDTO.setGueltig(true);
        waehrungKurzDTO.setBezeichnung("TestCoin");
        waehrungKurzDTO.setKurzbezeichnung("TC");

        Long id = waehrungService.speichereCryptoWaehrung(waehrungKurzDTO);
        cleanInstances();

        Assert.assertNotNull(id);
        WaehrungKurzDTO testWaehrungKurzDTO = waehrungService.holeWaehrungKurzDTO(id);

        Assert.assertNotNull(testWaehrungKurzDTO);
        Assert.assertEquals(testWaehrungKurzDTO.getId(), id);
        Assert.assertEquals(testWaehrungKurzDTO.getBezeichnung(), waehrungKurzDTO.getBezeichnung());
        Assert.assertEquals(testWaehrungKurzDTO.getKurzbezeichnung(), waehrungKurzDTO.getKurzbezeichnung());
        Assert.assertEquals(testWaehrungKurzDTO.isGueltig(), waehrungKurzDTO.isGueltig());
    }
}
