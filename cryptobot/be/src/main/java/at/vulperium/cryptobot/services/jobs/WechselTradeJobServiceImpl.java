package at.vulperium.cryptobot.services.jobs;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.entities.WechselTradeJob;
import at.vulperium.cryptobot.services.trades.WechselTradeJobService;
import at.vulperium.cryptobot.transformer.AbstractTradeJobDTOTransformer;
import at.vulperium.cryptobot.transformer.WechselTradeJobDTOTransformer;
import org.apache.commons.lang.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
@ApplicationScoped
@Transactional
public class WechselTradeJobServiceImpl extends AbstractTradeJobServiceImpl<WechselTradeJob, WechselTradeJobDTO> implements WechselTradeJobService {

    private static final Logger logger = LoggerFactory.getLogger(WechselTradeJobServiceImpl.class);

    private @Inject WechselTradeJobDTOTransformer transformer;

    @Override
    public Long speicherNeuenWechselTradeJob(WechselTradeJobDTO wechselTradeJobDTO) {
        Validate.notNull(wechselTradeJobDTO, "wechselTradeJobDTO ist null.");
        return speichereTradeJob(wechselTradeJobDTO);
    }

    @Override
    public List<WechselTradeJobDTO> holeAlleWechselTradeJobs() {
        TypedQuery<WechselTradeJob> query = em.createNamedQuery(WechselTradeJob.QRY_FIND_ALL, WechselTradeJob.class);
        List<WechselTradeJob> resultlist = query.getResultList();

        return resultlist.stream().map(e -> transformer.transform(e)).collect(Collectors.toList());
    }

    @Override
    public WechselTradeJobDTO holeWechselTradeJob(Long wechselTradeJobId) {
        Validate.notNull(wechselTradeJobId, "wechselTradeJobId ist null.");
        return holeTradeJob(wechselTradeJobId, WechselTradeJob.class);
    }

    @Override
    public boolean erledigeWechselTradeJob(Long wechselTradeJobId) {
        Validate.notNull(wechselTradeJobId, "wechselTradeJobId ist null.");
        return erledigeTradeJob(wechselTradeJobId, WechselTradeJob.class);
    }

    @Override
    public boolean aktualisiereWechselTradeJob(WechselTradeJobDTO wechselTradeJobDTO) {
        Validate.notNull(wechselTradeJobDTO, "wechselTradeJobDTO ist null.");
        return aktualisiereTradeJob(wechselTradeJobDTO, WechselTradeJob.class);
    }

    @Override
    protected AbstractTradeJobDTOTransformer<WechselTradeJob, WechselTradeJobDTO> getTransformer() {
        return transformer;
    }
}
