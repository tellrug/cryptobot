package at.vulperium.cryptobot.messagebundles;

import org.apache.deltaspike.core.api.message.MessageBundle;

/**
 * Created by 02ub0400 on 21.08.2017.
 */
@MessageBundle
public interface UtilityMessages {

    String titel();
    String abbrechen();
    String ok();
    String speichern();
    String loeschen();
    String abmelden();
}
