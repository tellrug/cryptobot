package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.BenachrichtigungDTO;

/**
 * Created by 02ub0400 on 22.02.2018.
 */
public interface BenachrichtigungManager {

    void registriereBenachrichtigung(BenachrichtigungDTO benachrichtigungDTO);

    void fuehreBenachrichtigungDurch();
}
