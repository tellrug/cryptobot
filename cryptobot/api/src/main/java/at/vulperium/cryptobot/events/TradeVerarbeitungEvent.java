package at.vulperium.cryptobot.events;

import at.vulperium.cryptobot.enums.TimerJobEnum;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public class TradeVerarbeitungEvent implements Serializable {

    private Long timerjobId;
    private TimerJobEnum timerJobEnum;
    private boolean manuellerCheck;

    public TradeVerarbeitungEvent(Long timerJobId, TimerJobEnum timerJobEnum, boolean manuellerCheck) {
        this.timerjobId = timerJobId;
        this.timerJobEnum = timerJobEnum;
        this.manuellerCheck = manuellerCheck;
    }

    public boolean isManuellerCheck() {
        return manuellerCheck;
    }

    public Long getTimerjobId() {
        return timerjobId;
    }

    public TimerJobEnum getTimerJobEnum() {
        return timerJobEnum;
    }
}
