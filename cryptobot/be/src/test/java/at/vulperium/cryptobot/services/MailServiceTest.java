package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.ContainerTest;
import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.testdatahelper.TradeJobTestDataHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ace on 07.01.2018.
 */

public class MailServiceTest extends ContainerTest {

    private @Inject MailService mailService;
    private @Inject BenachrichtigungService benachrichtigungService;

    private TradeJobTestDataHelper tradeJobTestDataHelper = new TradeJobTestDataHelper();

    @Test(enabled = false)
    public void testSendMail() {
        //Testen von Senden von Mails
        //Ueberpruefn der Einstellungen
        ConfigValue mailSendenAktiv = new ConfigValue("mailBenachrichtigung");
        Assert.assertEquals(mailSendenAktiv.get(), "true");

        ConfigValue mailSenderUser = new ConfigValue("mailSenderUser");
        Assert.assertNotEquals(mailSenderUser.get(), "test@mail.com");

        ConfigValue mailSenderPass = new ConfigValue("mailSenderPass");
        Assert.assertNotEquals(mailSenderPass.get(), "XXX");

        ConfigValue mailEmpfaenger = new ConfigValue("mailEmpfaenger");
        Assert.assertNotEquals(mailEmpfaenger.get(), "test@mail.com");

        boolean result = mailService.versendeMail("Test-Mail - Crypto", "Dies ist eine Test-Mail von der App Crypto (Unit-Test) \n Vielen Dank!");
        Assert.assertTrue(result);
    }

    @Test(enabled = false)
    public void testBenachrichtigung() {
        //Ueberpruefn der Einstellungen
        ConfigValue mailSendenAktiv = new ConfigValue("mailBenachrichtigung");
        Assert.assertEquals(mailSendenAktiv.get(), "true");

        ConfigValue mailSenderUser = new ConfigValue("mailSenderUser");
        Assert.assertNotEquals(mailSenderUser.get(), "test@mail.com");

        ConfigValue mailSenderPass = new ConfigValue("mailSenderPass");
        Assert.assertNotEquals(mailSenderPass.get(), "XXX");

        ConfigValue mailEmpfaenger = new ConfigValue("mailEmpfaenger");
        Assert.assertNotEquals(mailEmpfaenger.get(), "test@mail.com");

        TradeJobDTO tj1 = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.VERKAUF_ZIEL, TradeStatus.ABGESCHLOSSEN);
        TradeJobDTO tj2 = tradeJobTestDataHelper.erzeugeSimpleTradeJobDTO(TradeAktionEnum.KAUF_ZIEL, TradeStatus.ABGESCHLOSSEN);

        List<TradeJobDTO> tradeJobDTOList = new ArrayList<>();
        tradeJobDTOList.add(tj1);
        tradeJobDTOList.add(tj2);

        benachrichtigungService.versendeBenachrichtigung(tradeJobDTOList, BenachrichtigungTyp.MAIL, TradingPlattform.BINANCE);
    }
}
