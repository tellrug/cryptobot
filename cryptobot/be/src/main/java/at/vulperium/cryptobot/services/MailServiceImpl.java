package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.utils.ConfigUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
@ApplicationScoped
public class MailServiceImpl implements MailService {

    public static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final ConfigValue mailBenachrichtigung = new ConfigValue("mailBenachrichtigung");
    private static final ConfigValue mailHost = new ConfigValue("mailHost");
    private static final ConfigValue mailPort = new ConfigValue("mailPort");
    private static final ConfigValue mailSenderUser = new ConfigValue("mailSenderUser");
    private static final ConfigValue mailSenderPass = new ConfigValue("mailSenderPass");
    private static final ConfigValue mailEmpfaenger = new ConfigValue("mailEmpfaenger");

    public boolean versendeMail(String betreff, String text) {
        Validate.notNull(betreff, "betreff ist null");
        Validate.notNull(text, "text ist null");

        //Ueberpruefen ob Mails geschickt werden sollen
        if (!ConfigUtil.toBoolean(mailBenachrichtigung)) {
            //Es soll keine Mail-Benachrichtigung erfolgen
            logger.warn("Es ist eine Mail-Benachrichtigung vorhanden, jedoch ist dieser Service derzeit DEAKTIVIERT.");
            return false;
        }

        //daweil wird nur Gmail unterstuetzt
        return versendeMailFromGmail(betreff, text);
    }

    /*
    private void versendeMailFromGmail(String betreff, String text) {
        String from = mailSenderUser.get();
        String host = mailHost.get();
        String pass = mailSenderPass.get();

        Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", mailPort.get());
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);

        versendeMail(session, message, from, mailEmpfaenger.get(), host, pass, betreff, text);
    }

    private void versendeMail(Session session, MimeMessage message, String from, String to, String host, String pass, String betreff, String text) {
        Validate.notNull(session, "session ist null");
        Validate.notNull(message, "message ist null");
        Validate.notNull(from, "from ist null");
        Validate.notNull(to, "to ist null");
        Validate.notNull(host, "host ist null");
        Validate.notNull(pass, "pass ist null");
        Validate.notNull(betreff, "betreff ist null");
        Validate.notNull(text, "text ist null");

        try {
            //Sender
            message.setFrom(new InternetAddress(from));

            //Empfaenger
            InternetAddress toAddress = new InternetAddress(to);
            message.addRecipient(Message.RecipientType.TO, toAddress);

            //Nachricht
            message.setSubject(betreff);
            message.setText(text);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException e) {
            logger.error("Fehler beim Versenden von Mail: AddressException: ", e);
        }
        catch (MessagingException e) {
            logger.error("Fehler beim Versenden von Mail: MessagingException: ", e);
        }
        logger.info("Mail erfolgreich versendet.");
    }
    */


    private boolean versendeMailFromGmail(String betreff, String text) {
        String from = mailSenderUser.get();
        String host = mailHost.get();
        String pass = mailSenderPass.get();

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "false");
            props.put("mail.smtp.ssl.enable", "true");

            Session session = Session.getInstance(props, new EmailAuth(from, pass));
            Message msg = new MimeMessage(session);

            InternetAddress fromAddress = new InternetAddress(from, "CryptoBot-Vulperium");
            msg.setFrom(fromAddress);

            InternetAddress toAddress = new InternetAddress(mailEmpfaenger.get());

            msg.setRecipient(Message.RecipientType.TO, toAddress);

            msg.setSubject(betreff);
            msg.setText(text);
            /*
            msg.setContent("<html>\n" +
                    "<body>\n" +
                    "\n" +
                    "<a href=\"http://pushpalankajaya.blogspot.com\">\n" +
                    "This is a link</a>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>", "text/html");
                    */
            Transport.send(msg);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return false;
        }
        catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }
        logger.info("Mail erfolgreich versendet.");
        return true;
    }

    private final static class EmailAuth extends Authenticator {

        private String user;
        private String pass;

        public EmailAuth(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, pass);
        }
    }
}



