package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.CryptoWaehrungKurzDTO;
import at.vulperium.cryptobot.entities.CryptoWaehrung;
import at.vulperium.cryptobot.transformer.CryptoWaehrungKurzDTOTransformer;
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
public class CryptoWaehrungServiceImpl implements CryptoWaehrungService {

    private @Inject EntityManager em;
    private @Inject CryptoWaehrungKurzDTOTransformer transformer;

    @Override
    public List<CryptoWaehrungKurzDTO> holeAlleCryptoWaehrungen() {

        TypedQuery<CryptoWaehrung> query = em.createNamedQuery(CryptoWaehrung.QRY_FIND_ALL, CryptoWaehrung.class);
        List<CryptoWaehrung> resultList =query.getResultList();

        List<CryptoWaehrungKurzDTO> cryptoWaehrungKurzDTOList = new ArrayList<>();
        for (CryptoWaehrung cryptoWaehrung : resultList) {
            cryptoWaehrungKurzDTOList.add(transformer.transform(cryptoWaehrung));
        }

        return cryptoWaehrungKurzDTOList;
    }

    @Override
    public CryptoWaehrungKurzDTO holeCryptoWaehrung(Long cryptoWaehrungId) {
        Validate.notNull(cryptoWaehrungId, "cryptoWaehrungId ist null.");

        CryptoWaehrung cryptoWaehrung = em.find(CryptoWaehrung.class, cryptoWaehrungId);
        if (cryptoWaehrung == null) {
            return null;
        }

        return transformer.transform(cryptoWaehrung);
    }

    @Override
    public Long speichereCryptoWaehrung(CryptoWaehrungKurzDTO cryptoWaehrungKurzDTO) {
        Validate.notNull(cryptoWaehrungKurzDTO, "cryptoWaehrungKurzDTO ist null.");

        if (cryptoWaehrungKurzDTO.getId() != null) {
            //Id ist bereits vorhanden
            throw new IllegalStateException("CryptoWaehrung kann nicht gespeichert werden. Id bereits vorhanden.");
        }

        //Transformieren
        CryptoWaehrung cryptoWaehrung = transformer.transformInverse(cryptoWaehrungKurzDTO);

        em.persist(cryptoWaehrung);
        cryptoWaehrungKurzDTO.setId(cryptoWaehrung.getId());

        return cryptoWaehrung.getId();
    }

    @Override
    public Boolean aktualisiereCryptoWaehrung(CryptoWaehrungKurzDTO cryptoWaehrungKurzDTO) {
        Validate.notNull(cryptoWaehrungKurzDTO, "cryptoWaehrungKurzDTO ist null.");

        if (cryptoWaehrungKurzDTO.getId() == null) {
            //Id ist nicht vorhanden
            throw new IllegalStateException("CryptoWaehrung kann nicht aktualisiert werden. Id ist nicht vorhanden.");
        }

        CryptoWaehrung cryptoWaehrung = em.find(CryptoWaehrung.class, cryptoWaehrungKurzDTO.getId());
        if (cryptoWaehrung == null) {
            return false;
        }

        transformer.transformInverse(cryptoWaehrungKurzDTO, cryptoWaehrung);
        em.merge(cryptoWaehrung);
        return true;
    }

    @Override
    public List<CryptoWaehrungKurzDTO> filterCryptoWaehrungList(List<CryptoWaehrungKurzDTO> cryptoWaehrungKurzDTOList, boolean gueltig) {
        Validate.notNull(cryptoWaehrungKurzDTOList, "cryptoWaehrungKurzDTOList ist null.");
        return cryptoWaehrungKurzDTOList.stream().filter(e -> e.isGueltig() == gueltig ).collect(Collectors.toList());
    }

    @Override
    public Boolean setzeCryptoWaehrungUngueltig(Long cryptoWaehrungId) {
        Validate.notNull(cryptoWaehrungId, "cryptoWaehrungId ist null.");

        CryptoWaehrung cryptoWaehrung = em.find(CryptoWaehrung.class, cryptoWaehrungId);
        if (cryptoWaehrung == null) {
            return false;
        }

        cryptoWaehrung.setGueltig(false);
        em.merge(cryptoWaehrung);
        return true;
    }
}
