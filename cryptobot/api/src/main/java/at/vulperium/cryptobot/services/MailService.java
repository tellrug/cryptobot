package at.vulperium.cryptobot.services;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface MailService {

    boolean versendeMail(String betreff, String text);
}
