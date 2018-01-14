package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.WaehrungKurzDTO;
import at.vulperium.cryptobot.entities.Waehrung;
import at.vulperium.cryptobot.transformer.WaehrungKurzDTOTransformer;
import org.apache.commons.lang.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ace on 26.12.2017.
 */
@ApplicationScoped
@Transactional
public class WaehrungServiceImpl implements WaehrungService {

    private @Inject EntityManager em;
    private @Inject WaehrungKurzDTOTransformer transformer;

    @Override
    public List<WaehrungKurzDTO> holeAlleWaehrungen() {

        TypedQuery<Waehrung> query = em.createNamedQuery(Waehrung.QRY_FIND_ALL, Waehrung.class);
        List<Waehrung> resultList =query.getResultList();

        List<WaehrungKurzDTO> waehrungKurzDTOList = new ArrayList<>();
        for (Waehrung waehrung : resultList) {
            waehrungKurzDTOList.add(transformer.transform(waehrung));
        }

        return waehrungKurzDTOList;
    }

    @Override
    public WaehrungKurzDTO holeWaehrungKurzDTO(Long cryptoWaehrungId) {
        Validate.notNull(cryptoWaehrungId, "cryptoWaehrungId ist null.");

        Waehrung waehrung = em.find(Waehrung.class, cryptoWaehrungId);
        if (waehrung == null) {
            return null;
        }

        return transformer.transform(waehrung);
    }

    @Override
    public Long speichereCryptoWaehrung(WaehrungKurzDTO waehrungKurzDTO) {
        Validate.notNull(waehrungKurzDTO, "cryptoWaehrungKurzDTO ist null.");

        if (waehrungKurzDTO.getId() != null) {
            //Id ist bereits vorhanden
            throw new IllegalStateException("CryptoWaehrung kann nicht gespeichert werden. Id bereits vorhanden.");
        }

        //Transformieren
        Waehrung waehrung = transformer.transformInverse(waehrungKurzDTO);

        em.persist(waehrung);
        waehrungKurzDTO.setId(waehrung.getId());

        return waehrung.getId();
    }

    @Override
    public Boolean aktualisiereWaehrung(WaehrungKurzDTO waehrungKurzDTO) {
        Validate.notNull(waehrungKurzDTO, "cryptoWaehrungKurzDTO ist null.");

        if (waehrungKurzDTO.getId() == null) {
            //Id ist nicht vorhanden
            throw new IllegalStateException("CryptoWaehrung kann nicht aktualisiert werden. Id ist nicht vorhanden.");
        }

        Waehrung waehrung = em.find(Waehrung.class, waehrungKurzDTO.getId());
        if (waehrung == null) {
            return false;
        }

        transformer.transformInverse(waehrungKurzDTO, waehrung);
        em.merge(waehrung);
        return true;
    }

    @Override
    public List<WaehrungKurzDTO> filterWaehrungList(List<WaehrungKurzDTO> waehrungKurzDTOList, boolean gueltig) {
        Validate.notNull(waehrungKurzDTOList, "cryptoWaehrungKurzDTOList ist null.");
        return waehrungKurzDTOList.stream().filter(e -> e.isGueltig() == gueltig ).collect(Collectors.toList());
    }

    @Override
    public Boolean setzeWaehrungUngueltig(Long cryptoWaehrungId) {
        Validate.notNull(cryptoWaehrungId, "cryptoWaehrungId ist null.");

        Waehrung waehrung = em.find(Waehrung.class, cryptoWaehrungId);
        if (waehrung == null) {
            return false;
        }

        waehrung.setGueltig(false);
        em.merge(waehrung);
        return true;
    }
}
