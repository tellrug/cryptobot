package at.vulperium.cryptobot.events;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public class TradeVerarbeitungEvent implements Serializable {

    private LocalDateTime startVerarbeitung;
    private boolean manuellerCheck;

    public TradeVerarbeitungEvent(boolean manuellerCheck, LocalDateTime startVerarbeitung) {
        this.manuellerCheck = manuellerCheck;
        this.startVerarbeitung = startVerarbeitung;
    }

    public boolean isManuellerCheck() {
        return manuellerCheck;
    }

    public LocalDateTime getStartVerarbeitung() {
        return startVerarbeitung;
    }
}
