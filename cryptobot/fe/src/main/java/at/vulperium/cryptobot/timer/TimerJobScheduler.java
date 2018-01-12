package at.vulperium.cryptobot.timer;


import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.events.TradeVerarbeitungEvent;
import at.vulperium.cryptobot.utils.ConfigUtil;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by 02ub0400 on 02.01.2018.
 */

@Singleton
@Startup
@Lock(LockType.READ)
public class TimerJobScheduler {

    private final static Logger logger = LoggerFactory.getLogger(TimerJobScheduler.class);

    //in Millisekunden
    private final static ConfigValue timerIntervall = new ConfigValue("timerIntervall");

    private @Resource TimerService timerService;

    private @Inject Event<TradeVerarbeitungEvent> tradeVerarbeitungEvent;

    @PostConstruct
    private void initTimer() {
        //timerService.createTimer(3000L, 1000L, "test");
        logger.info("Timer fuer TimerJobs wird gestartet...");
        System.out.println("Timer fuer TimerJobs wird gestartet... - Intervall: " + timerIntervall.get());
        //Starten des Timers
        startTimer();
    }

    /**
     * Timer ist abgelaufen - Es werden alle offenen Aufgaben gesammelt und durchgefuehrt
     *
     * @param timer
     */
    @Timeout
    public void timeout(Timer timer) {
        //Feuern eines Events zum starten der Verarbeitung
        TradeVerarbeitungEvent tradeVerarbeitung = new TradeVerarbeitungEvent(false, LocalDateTime.now());
        tradeVerarbeitungEvent.fire(tradeVerarbeitung);

        //Erneutes starten des Timers
        startTimer();
    }

    private void startTimer() {
        //Starten des Timers
        Duration intervall = new Duration(Minutes.minutes(ConfigUtil.toInteger(timerIntervall)).toStandardDuration());
        timerService.createSingleActionTimer(intervall.getMillis(), new TimerConfig(new TimerEvent(), false));
    }


    private class TimerEvent implements Serializable {
        private static final long serialVersionUID = 505878810448499142L;
    }
}
