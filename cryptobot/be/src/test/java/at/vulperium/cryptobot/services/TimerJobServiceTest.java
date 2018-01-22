package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.dtos.TimerJobDTO;
import at.vulperium.cryptobot.enums.TimerJobEnum;
import junit.framework.Assert;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by 02ub0400 on 17.01.2018.
 */
public class TimerJobServiceTest extends ContainerTest {

    private @Inject TimerJobService timerJobService;

    @Test
    public void testHoleAlleTimerJobs() {
        List<TimerJobDTO> alleTimerJobDTOList = timerJobService.holeAlleTimerJobs();

        Assert.assertNotNull(alleTimerJobDTOList);
        Assert.assertEquals(alleTimerJobDTOList.size(), 2);
    }

    @Test
    public void testAktualisiereDurchfuehrungInfo() {

        List<TimerJobDTO> timerJobDTOList = timerJobService.holeTimerJob(TimerJobEnum.TRADE_BEOBACHTUNG);
        Assert.assertNotNull(timerJobDTOList);
        Assert.assertEquals(timerJobDTOList.size(), 1);

        TimerJobDTO timerJobDTO = timerJobDTOList.get(0);

        Assert.assertNotNull(timerJobDTO);
        Assert.assertEquals(timerJobDTO.getTimerJobEnum(), TimerJobEnum.TRADE_BEOBACHTUNG);
        Assert.assertNotNull(timerJobDTO.getErstelltAm());
        Assert.assertNotNull(timerJobDTO.getLetzteDurchfuehrungAm());
        Assert.assertNull(timerJobDTO.getNaechsteDurchfuehrungAm());

        LocalDateTime next = LocalDateTime.now().plusDays(1);

        timerJobService.aktualisiereDurchfuehrungInfo(timerJobDTO.getId(), next);
        cleanInstances();


        TimerJobDTO checkTimerjobDTO = timerJobService.holeTimerJob(timerJobDTO.getId());

        Assert.assertNotNull(checkTimerjobDTO);
        Assert.assertEquals(checkTimerjobDTO.getTimerJobEnum(), timerJobDTO.getTimerJobEnum());
        Assert.assertEquals(checkTimerjobDTO.getErstelltAm(), timerJobDTO.getErstelltAm());
        Assert.assertNotNull(checkTimerjobDTO.getLetzteDurchfuehrungAm());
        Assert.assertEquals(checkTimerjobDTO.getNaechsteDurchfuehrungAm(), next);
    }
}
