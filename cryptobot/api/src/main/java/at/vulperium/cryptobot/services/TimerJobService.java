package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TimerJobDTO;
import at.vulperium.cryptobot.enums.TimerJobEnum;
import org.joda.time.LocalDateTime;

import java.util.List;

/**
 * Created by 02ub0400 on 15.01.2018.
 */
public interface TimerJobService {

    TimerJobDTO holeTimerJob(Long timerJobId);

    List<TimerJobDTO> holeAlleTimerJobs();

    List<TimerJobDTO> holeTimerJob(TimerJobEnum timerJobEnum);

    boolean aktualisiereDurchfuehrungInfo(Long timerjobId, LocalDateTime naechsteDurchfuehrungAm);
}
