package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.entities.AbstractTradeJob;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.transformer.AbstractTradeJobDTOTransformer;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
public abstract class AbstractTradeJobServiceImpl<E extends AbstractTradeJob, D extends AbstractTradeJobDTO> {

    protected @Inject EntityManager em;

    protected D holeTradeJob(Long id, Class clazz) {
        Validate.notNull(id, "id ist null.");

        E tradeJob = (E) em.find(clazz, id);
        if (tradeJob == null) {
            return null;
        }

        return getTransformer().transform(tradeJob);
    }

    protected Long speichereTradeJob(D tradeJobDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null.");

        if (tradeJobDTO.getId() != null) {
            throw new IllegalStateException("TradeJob kann nicht gespeichert werden. Id bereits vorhanden.");
        }

        if (tradeJobDTO.getErstelltAm() == null) {
            tradeJobDTO.setErstelltAm(LocalDateTime.now());
        }

        E tradeJob = getTransformer().transformInverse(tradeJobDTO);
        em.persist(tradeJob);
        tradeJobDTO.setId(tradeJob.getId());
        return tradeJob.getId();
    }

    protected boolean aktualisiereTradeJob(D tradeJobDTO, Class clazz) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null.");

        if (tradeJobDTO.getId() == null) {
            //Id ist nicht vorhanden
            throw new IllegalStateException("TradeJob kann nicht aktualisiert werden. Id ist nicht vorhanden.");
        }

        E tradeJob = (E) em.find(clazz, tradeJobDTO.getId());
        if (tradeJob == null) {
            return false;
        }

        getTransformer().transformInverse(tradeJobDTO, tradeJob);
        em.merge(tradeJob);
        return true;
    }


    public List<D> filterTradeJobDTOList(List<D> tradeJobDTOList, TradeTyp tradeTyp) {
        Validate.notNull(tradeJobDTOList, "tradeJobDTOList ist null.");
        Validate.notNull(tradeTyp, "tradeStatusTyp ist null.");
        return tradeJobDTOList.stream().filter(e -> e.getTradeAktionEnum().getTradeTyp() == tradeTyp).collect(Collectors.toList());
    }

    public List<D> filterTradeJobDTOList(List<D> tradeJobDTOList, boolean erledigt) {
        Validate.notNull(tradeJobDTOList, "tradeJobDTOList ist null.");
        return tradeJobDTOList.stream().filter(e -> (e.getErledigtAm() != null) == erledigt).collect(Collectors.toList());
    }


    public List<D> filterTradeJobDTOList(List<D> tradeJobDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(tradeJobDTOList, "tradeJobDTOList ist null.");
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        return tradeJobDTOList.stream().filter(e -> e.getTradingPlattform() == tradingPlattform).collect(Collectors.toList());
    }

    protected boolean erledigeTradeJob(Long tradeJobId, Class clazz) {
        Validate.notNull(tradeJobId, "tradeAktionId ist null.");

        E tradeJob = (E) em.find(clazz, tradeJobId);
        if (tradeJob == null) {
            return false;
        }

        tradeJob.setErledigtAm(LocalDateTime.now().toDate());
        em.merge(tradeJob);
        return true;
    }


    protected abstract AbstractTradeJobDTOTransformer<E, D> getTransformer();
}
