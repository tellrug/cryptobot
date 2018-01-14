package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.entities.TradeJob;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.transformer.TradeJobDTOTransformer;
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
    private @Inject TradeJobDTOTransformer transformer;

    @Override
    public TradeJobDTO holeTradeJob(Long id) {
        Validate.notNull(id, "id ist null.");

        TradeJob tradeJob = em.find(TradeJob.class, id);
        if (tradeJob == null) {
            return null;
        }

        return transformer.transform(tradeJob);
    }

    @Override
    public List<TradeJobDTO> holeAlleTradeJobs() {
        TypedQuery<TradeJob> query = em.createNamedQuery(TradeJob.QRY_FIND_ALL, TradeJob.class);
        return query.getResultList().stream().map(tradeJob -> transformer.transform(tradeJob)).collect(Collectors.toList());
    }

    @Override
    public List<TradeJobDTO> filterTradeJobDTOList(List<TradeJobDTO> tradeJobDTOList, TradeTyp tradeTyp) {
        Validate.notNull(tradeJobDTOList, "tradeJobDTOList ist null.");
        Validate.notNull(tradeTyp, "tradeTyp ist null.");
        return tradeJobDTOList.stream().filter(e -> e.getTradeAktionEnum().getTradeTyp() == tradeTyp).collect(Collectors.toList());
    }

    @Override
    public List<TradeJobDTO> filterTradeJobDTOList(List<TradeJobDTO> tradeJobDTOList, boolean erledigt) {
        Validate.notNull(tradeJobDTOList, "tradeJobDTOList ist null.");
        return tradeJobDTOList.stream().filter(e -> (e.getErledigtAm() != null) == erledigt).collect(Collectors.toList());
    }

    @Override
    public List<TradeJobDTO> filterTradeJobDTOList(List<TradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(tradeJobDTOList, "tradeJobDTOList ist null.");
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        return tradeJobDTOList.stream().filter(e -> e.getTradingPlattform() == tradingPlattform).collect(Collectors.toList());
    }

    @Override
    public Long speichereTradeJob(TradeJobDTO tradeJobDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null.");

        if (tradeJobDTO.getId() != null) {
            throw new IllegalStateException("TradeJob kann nicht gespeichert werden. Id bereits vorhanden.");
        }

        if (tradeJobDTO.getErstelltAm() == null) {
            tradeJobDTO.setErstelltAm(LocalDateTime.now());
        }

        TradeJob tradeJob = transformer.transformInverse(tradeJobDTO);
        em.persist(tradeJob);
        tradeJobDTO.setId(tradeJob.getId());
        return tradeJob.getId();
    }

    @Override
    public boolean aktualisiereTradeJob(TradeJobDTO tradeJobDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null.");

        if (tradeJobDTO.getId() == null) {
            //Id ist nicht vorhanden
            throw new IllegalStateException("TradeJob kann nicht aktualisiert werden. Id ist nicht vorhanden.");
        }

        TradeJob tradeJob = em.find(TradeJob.class, tradeJobDTO.getId());
        if (tradeJob == null) {
            return false;
        }

        transformer.transformInverse(tradeJobDTO, tradeJob);
        em.merge(tradeJob);
        return true;
    }

    @Override
    public boolean erledigeTradeJob(Long tradeJobId) {
        Validate.notNull(tradeJobId, "tradeAktionId ist null.");

        TradeJob tradeJob = em.find(TradeJob.class, tradeJobId);
        if (tradeJob == null) {
            return false;
        }

        tradeJob.setErledigtAm(LocalDateTime.now().toDate());
        em.merge(tradeJob);
        return true;
    }
}
