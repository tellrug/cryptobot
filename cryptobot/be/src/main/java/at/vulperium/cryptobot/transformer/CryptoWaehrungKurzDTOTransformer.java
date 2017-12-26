package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.CryptoWaehrungKurzDTO;
import at.vulperium.cryptobot.entities.CryptoWaehrung;
import at.vulperium.cryptobot.services.TransformBothDirections;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by Ace on 26.12.2017.
 */
@ApplicationScoped
public class CryptoWaehrungKurzDTOTransformer implements TransformBothDirections<CryptoWaehrung, CryptoWaehrungKurzDTO> {

    @Override
    public CryptoWaehrung transformInverse(CryptoWaehrungKurzDTO source) {
        return transformInverse(source, new CryptoWaehrung());
    }

    @Override
    public CryptoWaehrungKurzDTO transform(CryptoWaehrung source) {
        return transform(source, new CryptoWaehrungKurzDTO());
    }

    @Override
    public CryptoWaehrung transformInverse(CryptoWaehrungKurzDTO source, CryptoWaehrung target) {

        target.setBezeichnung(source.getBezeichnung());
        target.setKurzbezeichnung(source.getKurzbezeichnung());
        target.setGueltig(source.isGueltig());

        return target;
    }

    @Override
    public CryptoWaehrungKurzDTO transform(CryptoWaehrung source, CryptoWaehrungKurzDTO target) {

        target.setId(source.getId());
        target.setBezeichnung(source.getBezeichnung());
        target.setKurzbezeichnung(source.getKurzbezeichnung());
        target.setGueltig(source.getGueltig() == null ? false : source.getGueltig());

        return target;
    }
}
