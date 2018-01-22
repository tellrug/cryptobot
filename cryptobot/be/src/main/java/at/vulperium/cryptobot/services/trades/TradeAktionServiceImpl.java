package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.entities.TradeAktion;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.transformer.TradeAktionDTOTransformer;
import org.apache.commons.lang.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 15.01.2018.
 */
@ApplicationScoped
@Transactional
public class TradeAktionServiceImpl implements TradeAktionService {

    private final static Logger logger = LoggerFactory.getLogger(TradeAktionServiceImpl.class);

    private @Inject EntityManager entityManager;
    private @Inject TradeAktionDTOTransformer transformer;

    @Override
    public Long speichereTradeAktion(TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");

        if (tradeAktionDTO.getId() != null) {
            throw new IllegalStateException("TradeAktion kann nicht gespeichert werden. Id bereits vorhanden.");
        }

        if (tradeAktionDTO.getErstelltAm() == null) {
            tradeAktionDTO.setErstelltAm(LocalDateTime.now());
        }

        TradeAktion tradeAktion = transformer.transformInverse(tradeAktionDTO);
        entityManager.persist(tradeAktion);

        tradeAktionDTO.setId(tradeAktion.getId());
        return tradeAktionDTO.getId();
    }

    @Override
    public boolean aktualisiereTradeAktion(TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");

        if (tradeAktionDTO.getId() == null) {
            //Id ist nicht vorhanden
            throw new IllegalStateException("TradeAktion kann nicht aktualisiert werden. Id ist nicht vorhanden.");
        }

        TradeAktion tradeAktion = entityManager.find(TradeAktion.class, tradeAktionDTO.getId());
        if (tradeAktion == null) {
            return false;
        }

        transformer.transformInverse(tradeAktionDTO, tradeAktion);
        entityManager.merge(tradeAktion);
        entityManager.flush();
        return true;
    }

    @Override
    public List<TradeAktionDTO> holeAlleTradeAktionen() {
        TypedQuery<TradeAktion> query = entityManager.createNamedQuery(TradeAktion.QRY_FIND_ALL, TradeAktion.class);
        return query.getResultList().stream().map(tradeAktion -> transformer.transform(tradeAktion)).collect(Collectors.toList());
    }

    @Override
    public TradeAktionDTO holeTradeAktion(Long tradeAktionId) {
        Validate.notNull(tradeAktionId, "tradeAktionId ist null.");

        TradeAktion tradeAktion = entityManager.find(TradeAktion.class, tradeAktionId);
        if (tradeAktion == null) {
            return null;
        }

        return transformer.transform(tradeAktion);
    }

    @Override
    public List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, TradeTyp tradeTyp) {
        Validate.notNull(tradeAktionDTOList, "tradeAktionDTOList ist null.");
        Validate.notNull(tradeTyp, "tradeTyp ist null.");
        return tradeAktionDTOList.stream().filter(e -> e.getTradeTyp() == tradeTyp).collect(Collectors.toList());
    }

    @Override
    public List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, boolean erledigt) {
        Validate.notNull(tradeAktionDTOList, "tradeJobDTOList ist null.");
        return tradeAktionDTOList.stream().filter(e -> (e.getErledigtAm() != null) == erledigt).collect(Collectors.toList());
    }

    @Override
    public List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(tradeAktionDTOList, "tradeAktionDTOList ist null.");
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        return tradeAktionDTOList.stream().filter(e -> e.getTradingPlattform() == tradingPlattform).collect(Collectors.toList());
    }

    @Override
    public List<TradeAktionDTO> filterTradeAktionDTOList(List<TradeAktionDTO> tradeAktionDTOList, Long jobId) {
        Validate.notNull(tradeAktionDTOList, "tradeAktionDTOList ist null.");
        Validate.notNull(jobId, "jobId ist null.");
        return tradeAktionDTOList.stream().filter(e -> e.getTradeJobId() != null && e.getTradeJobId().equals(jobId)).collect(Collectors.toList());
    }
}
