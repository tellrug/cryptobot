package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TimerJobEnum;
import org.joda.time.LocalDateTime;

import java.io.Serializable;


/**
 * Created by 02ub0400 on 15.01.2018.
 */
public class TimerJobDTO implements Serializable{

    private Long id;
    private LocalDateTime erstelltAm;
    private LocalDateTime naechsteDurchfuehrungAm;
    private LocalDateTime letzteDurchfuehrungAm;
    private TimerJobEnum timerJobEnum;

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLetzteDurchfuehrungAm() {
        return letzteDurchfuehrungAm;
    }

    public void setLetzteDurchfuehrungAm(LocalDateTime letzteDurchfuehrungAm) {
        this.letzteDurchfuehrungAm = letzteDurchfuehrungAm;
    }

    public LocalDateTime getNaechsteDurchfuehrungAm() {
        return naechsteDurchfuehrungAm;
    }

    public void setNaechsteDurchfuehrungAm(LocalDateTime naechsteDurchfuehrungAm) {
        this.naechsteDurchfuehrungAm = naechsteDurchfuehrungAm;
    }

    public TimerJobEnum getTimerJobEnum() {
        return timerJobEnum;
    }

    public void setTimerJobEnum(TimerJobEnum timerJobEnum) {
        this.timerJobEnum = timerJobEnum;
    }
}
