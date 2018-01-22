package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.entities.SimpelTradeJob;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.transformer.SimpelTradeJobDTOTransformer;
import org.apache.commons.lang.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class TradeJobServiceImpl implements TradeJobService {

    private @Inject EntityManager em;
    private @Inject SimpelTradeJobDTOTransformer transformer;

    @Override
    public SimpelTradeJobDTO holeTradeJob(Long id) {
        Validate.notNull(id, "id ist null.");

        SimpelTradeJob simpelTradeJob = em.find(SimpelTradeJob.class, id);
        if (simpelTradeJob == null) {
            return null;
        }

        return transformer.transform(simpelTradeJob);
    }

    @Override
    public List<SimpelTradeJobDTO> holeAlleTradeJobs() {
        TypedQuery<SimpelTradeJob> query = em.createNamedQuery(SimpelTradeJob.QRY_FIND_ALL, SimpelTradeJob.class);
        return query.getResultList().stream().map(tradeJob -> transformer.transform(tradeJob)).collect(Collectors.toList());
    }

    @Override
    public List<SimpelTradeJobDTO> filterTradeJobDTOList(List<SimpelTradeJobDTO> simpelTradeJobDTOList, TradeTyp tradeTyp) {
        Validate.notNull(simpelTradeJobDTOList, "tradeJobDTOList ist null.");
        Validate.notNull(tradeTyp, "tradeTyp ist null.");
        return simpelTradeJobDTOList.stream().filter(e -> e.getTradeAktionEnum().getTradeTyp() == tradeTyp).collect(Collectors.toList());
    }

    @Override
    public List<SimpelTradeJobDTO> filterTradeJobDTOList(List<SimpelTradeJobDTO> simpelTradeJobDTOList, boolean erledigt) {
        Validate.notNull(simpelTradeJobDTOList, "tradeJobDTOList ist null.");
        return simpelTradeJobDTOList.stream().filter(e -> (e.getErledigtAm() != null) == erledigt).collect(Collectors.toList());
    }

    @Override
    public List<SimpelTradeJobDTO> filterTradeJobDTOList(List<SimpelTradeJobDTO> simpelTradeJobDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(simpelTradeJobDTOList, "tradeJobDTOList ist null.");
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        return simpelTradeJobDTOList.stream().filter(e -> e.getTradingPlattform() == tradingPlattform).collect(Collectors.toList());
    }

    @Override
    public Long speichereTradeJob(SimpelTradeJobDTO simpelTradeJobDTO) {
        Validate.notNull(simpelTradeJobDTO, "tradeJobDTO ist null.");

        if (simpelTradeJobDTO.getId() != null) {
            throw new IllegalStateException("TradeJob kann nicht gespeichert werden. Id bereits vorhanden.");
        }

        if (simpelTradeJobDTO.getErstelltAm() == null) {
            simpelTradeJobDTO.setErstelltAm(LocalDateTime.now());
        }

        SimpelTradeJob simpelTradeJob = transformer.transformInverse(simpelTradeJobDTO);
        em.persist(simpelTradeJob);
        simpelTradeJobDTO.setId(simpelTradeJob.getId());
        return simpelTradeJob.getId();
    }

    @Override
    public boolean aktualisiereTradeJob(SimpelTradeJobDTO simpelTradeJobDTO) {
        Validate.notNull(simpelTradeJobDTO, "tradeJobDTO ist null.");

        if (simpelTradeJobDTO.getId() == null) {
            //Id ist nicht vorhanden
            throw new IllegalStateException("TradeJob kann nicht aktualisiert werden. Id ist nicht vorhanden.");
        }

        SimpelTradeJob simpelTradeJob = em.find(SimpelTradeJob.class, simpelTradeJobDTO.getId());
        if (simpelTradeJob == null) {
            return false;
        }

        transformer.transformInverse(simpelTradeJobDTO, simpelTradeJob);
        em.merge(simpelTradeJob);
        return true;
    }

    @Override
    public boolean erledigeTradeJob(Long tradeJobId) {
        Validate.notNull(tradeJobId, "tradeJobId ist null.");

        SimpelTradeJob simpelTradeJob = em.find(SimpelTradeJob.class, tradeJobId);
        if (simpelTradeJob == null) {
            return false;
        }

        simpelTradeJob.setErledigtAm(LocalDateTime.now().toDate());
        em.merge(simpelTradeJob);
        return true;
    }
}
