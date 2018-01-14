package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.WaehrungKurzDTO;
import at.vulperium.cryptobot.entities.Waehrung;
import at.vulperium.cryptobot.services.TransformBothDirections;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by Ace on 26.12.2017.
 */
@ApplicationScoped
public class WaehrungKurzDTOTransformer implements TransformBothDirections<Waehrung, WaehrungKurzDTO> {

    @Override
    public Waehrung transformInverse(WaehrungKurzDTO source) {
        return transformInverse(source, new Waehrung());
    }

    @Override
    public WaehrungKurzDTO transform(Waehrung source) {
        return transform(source, new WaehrungKurzDTO());
    }

    @Override
    public Waehrung transformInverse(WaehrungKurzDTO source, Waehrung target) {

        target.setBezeichnung(source.getBezeichnung());
        target.setKurzbezeichnung(source.getKurzbezeichnung());
        target.setGueltig(source.isGueltig());

        return target;
    }

    @Override
    public WaehrungKurzDTO transform(Waehrung source, WaehrungKurzDTO target) {

        target.setId(source.getId());
        target.setBezeichnung(source.getBezeichnung());
        target.setKurzbezeichnung(source.getKurzbezeichnung());
        target.setGueltig(source.getGueltig() == null ? false : source.getGueltig());

        return target;
    }
}
