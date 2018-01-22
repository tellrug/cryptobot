package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TimerJobDTO;
import at.vulperium.cryptobot.entities.TimerJob;
import at.vulperium.cryptobot.enums.TimerJobEnum;
import at.vulperium.cryptobot.transformer.TimerJobDTOTransformer;
import org.apache.commons.lang.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.joda.time.LocalDateTime;

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
public class TimerJobServiceImpl implements TimerJobService {

    private @Inject EntityManager entityManager;
    private @Inject TimerJobDTOTransformer transformer;


    @Override
    public TimerJobDTO holeTimerJob(Long timerJobId) {
        Validate.notNull(timerJobId, "timerjobId ist null.");

        TimerJob timerJob = entityManager.find(TimerJob.class, timerJobId);
        if (timerJob == null) {
            return null;
        }
        return transformer.transform(timerJob);
    }

    @Override
    public List<TimerJobDTO> holeAlleTimerJobs() {
        TypedQuery<TimerJob> query = entityManager.createNamedQuery(TimerJob.QRY_FIND_ALL, TimerJob.class);
        return query.getResultList().stream().map(timerJob -> transformer.transform(timerJob)).collect(Collectors.toList());
    }

    @Override
    public List<TimerJobDTO> holeTimerJob(TimerJobEnum timerJobEnum) {
        Validate.notNull(timerJobEnum, "timerJobEnum ist null.");

        List<TimerJobDTO> alleTimerJobs = holeAlleTimerJobs();
        return alleTimerJobs.stream().filter(timerJob -> timerJob.getTimerJobEnum() == timerJobEnum).collect(Collectors.toList());
    }

    @Override
    public boolean aktualisiereDurchfuehrungInfo(Long timerjobId, LocalDateTime naechsteDurchfuehrungAm) {
        Validate.notNull(timerjobId, "timerjobId ist null.");
        Validate.notNull(naechsteDurchfuehrungAm, "naechsteDurchfuehrungAm ist null.");

        TimerJob timerJob = entityManager.find(TimerJob.class, timerjobId);
        if (timerJob == null) {
            return false;
        }

        timerJob.setLetzteDurchfuehrungAm(LocalDateTime.now().toDate());
        timerJob.setNaechsteDurchfuehrungAm(naechsteDurchfuehrungAm.toDate());
        entityManager.merge(timerJob);
        return true;
    }
}
