package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.dtos.BenachrichtigungDTO;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
@ApplicationScoped
public class BenachrichtigungServiceImpl implements BenachrichtigungService {

    private @Inject MailService mailService;

    private static final String NEW_LINE = "\n";
    private static final String TRENNER = "============================================================";

    private static final ConfigValue reportBetreff = new ConfigValue("reportBetreff");

    @Override
    public void versendeBenachrichtigungen(List<BenachrichtigungDTO> benachrichtigungDTOList, BenachrichtigungTyp benachrichtigungTyp) {
        if (CollectionUtils.isEmpty(benachrichtigungDTOList)) {
            return;
        }

        if (benachrichtigungTyp == BenachrichtigungTyp.MAIL) {
            String betreff = erstelleBenachrichtigungsBetreff(benachrichtigungDTOList, TradingPlattform.BINANCE);

            StringBuilder textStrBuilder = new StringBuilder();
            for (BenachrichtigungDTO benachrichtigungDTO : benachrichtigungDTOList) {
                String tmpText = benachrichtigungDTO.getText();
                textStrBuilder.append(tmpText);
                textStrBuilder.append(NEW_LINE);
                textStrBuilder.append(TRENNER);
                textStrBuilder.append(NEW_LINE);
            }
            String text = textStrBuilder.toString();
            mailService.versendeMail(betreff, text);
            return;
        }
        throw new IllegalStateException("Benachrichtigung von mehreren TradeJobs fehlgeschlagen. BenachrichtigungTyp="
                + benachrichtigungTyp + " nicht vorgesehen!");
    }

    @Override
    public String erstelleBenachrichtigungsBetreff(List<BenachrichtigungDTO> benachrichtigungDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(reportBetreff.get());
        for (BenachrichtigungDTO benachrichtigungDTO : benachrichtigungDTOList) {
            stringBuilder.append(benachrichtigungDTO.getBetreff()).append(" * ");
        }
        stringBuilder.append(" - ").append(tradingPlattform.name());
        return stringBuilder.toString();
    }

    @Override
    public BenachrichtigungDTO erstelleBenachrichtigungsDTO(AbstractTradeJobDTO abstractTradeJobDTO, BenachrichtigungTyp benachrichtigungTyp) {

        BenachrichtigungDTO benachrichtigungDTO = new BenachrichtigungDTO();
        if (BenachrichtigungTyp.MAIL == benachrichtigungTyp) {
            String betreff = erstelleBenachrichtigungsBetreff(abstractTradeJobDTO);
            String text = erstelleBenachrichtigungsText(abstractTradeJobDTO);

            benachrichtigungDTO.setBetreff(betreff);
            benachrichtigungDTO.setText(text);
            benachrichtigungDTO.setBenachrichtigungTyp(benachrichtigungTyp);
        }
        else {
            throw new IllegalStateException("Momentan wird nur Mail-Benachrichtigung unterstuetzt!");
        }

        return benachrichtigungDTO;
    }

    private String erstelleBenachrichtigungsBetreff(AbstractTradeJobDTO abstractTradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        if (abstractTradeJobDTO instanceof WechselTradeJobDTO) {
            WechselTradeJobDTO wechselTradeJobDTO = (WechselTradeJobDTO) abstractTradeJobDTO;
            stringBuilder
                    .append(reportBetreff.get())                                            //Betreff-Prefix
                    .append("WJ  ")
                    .append(wechselTradeJobDTO.getCryptoWaehrung())                         //Coin
                    .append(" ")
                    .append(wechselTradeJobDTO.getTradeAktionEnum().name())                 //Ereignis
                    .append(" - ")
                    .append(wechselTradeJobDTO.getTradingPlattform().name());               //Trading-Plattform
        }
        else if (abstractTradeJobDTO instanceof SimpelTradeJobDTO) {
            SimpelTradeJobDTO simpelTradeJobDTO = (SimpelTradeJobDTO) abstractTradeJobDTO;
            stringBuilder
                    .append(reportBetreff.get())                                            //Betreff-Prefix
                    .append("SJ  ")
                    .append(simpelTradeJobDTO.getCryptoWaehrung())                          //Coin
                    .append(" ")
                    .append(simpelTradeJobDTO.getTradeAktionEnum().name())                  //Ereignis
                    .append(" - ")
                    .append(simpelTradeJobDTO.getTradingPlattform().name());               //Trading-Plattform
        }
        return stringBuilder.toString();
    }


    private String erstelleBenachrichtigungsText(AbstractTradeJobDTO abstractTradeJobDTO) {
        String tradeAktionInformation;
        if (abstractTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
            tradeAktionInformation = erstelleKaufBenachrichtigungsText(abstractTradeJobDTO);
        }
        else if (abstractTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
            tradeAktionInformation = erstelleVerkaufBenachrichtigungsText(abstractTradeJobDTO);
        }
        else {
            throw new IllegalStateException("Fehlerhafter TradeTyp= " + abstractTradeJobDTO.getTradeAktionEnum().getTradeTyp() + " bei TradeJob=" + abstractTradeJobDTO.getId());
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Trade: ").append(abstractTradeJobDTO.getCryptoWaehrung()).append(" <--> ").append(abstractTradeJobDTO.getCryptoWaehrungReferenz())
                .append("   -   ").append(abstractTradeJobDTO.getTradingPlattform().name())
                .append(NEW_LINE)
                .append(tradeAktionInformation);
        return stringBuilder.toString();
    }

    private String erstelleKaufBenachrichtigungsText(AbstractTradeJobDTO abstractTradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();

        if (abstractTradeJobDTO instanceof WechselTradeJobDTO) {
            WechselTradeJobDTO wechselTradeJobDTO = (WechselTradeJobDTO) abstractTradeJobDTO;
            stringBuilder
                    .append("TradeAktion: Kauf von ").append(wechselTradeJobDTO.getMenge()).append(" ").append(wechselTradeJobDTO.getCryptoWaehrung())
                    .append(NEW_LINE)
                    .append("Zielsatz: ").append(wechselTradeJobDTO.getMinimalZielSatz()).append(" ")
                    .append(NEW_LINE)
                    .append("Erledigt am: ").append(LocalDateTime.now());
        }
        else if (abstractTradeJobDTO instanceof SimpelTradeJobDTO) {
            SimpelTradeJobDTO simpelTradeJobDTO = (SimpelTradeJobDTO) abstractTradeJobDTO;
            stringBuilder
                    .append("TradeAktion: Kauf von ").append(simpelTradeJobDTO.getMenge()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrung())
                    .append(NEW_LINE)
                    .append("Zielwert: ").append(simpelTradeJobDTO.getZielwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                    .append(NEW_LINE)
                    .append("AktuellerWert: ").append(simpelTradeJobDTO.getLetztwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                    .append("Erledigt am: ").append(simpelTradeJobDTO.getErledigtAm());
        }
        return stringBuilder.toString();
    }

    private String erstelleVerkaufBenachrichtigungsText(AbstractTradeJobDTO abstractTradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();

        if (abstractTradeJobDTO instanceof WechselTradeJobDTO) {
            WechselTradeJobDTO wechselTradeJobDTO = (WechselTradeJobDTO) abstractTradeJobDTO;
            stringBuilder
                    .append("TradeAktion: Verkauf von ").append(wechselTradeJobDTO.getMenge()).append(" ").append(wechselTradeJobDTO.getCryptoWaehrung())
                    .append(NEW_LINE)
                    .append("Kaufwert: ").append(wechselTradeJobDTO.getKaufwert()).append(" ").append(wechselTradeJobDTO.getCryptoWaehrungReferenz())
                    .append(NEW_LINE)
                    .append("Zielwert: ").append(wechselTradeJobDTO.getZielwert()).append(" ").append(wechselTradeJobDTO.getCryptoWaehrungReferenz())
                    .append(NEW_LINE)
                    .append("LetztWert: ").append(wechselTradeJobDTO.getLetztwert()).append(" ").append(wechselTradeJobDTO.getCryptoWaehrungReferenz())
                    .append("Erledigt am: ").append(LocalDateTime.now());
        }
        else if (abstractTradeJobDTO instanceof SimpelTradeJobDTO) {
            SimpelTradeJobDTO simpelTradeJobDTO = (SimpelTradeJobDTO) abstractTradeJobDTO;
            stringBuilder
                    .append("TradeAktion: Verkauf von ").append(simpelTradeJobDTO.getMenge()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrung())
                    .append(NEW_LINE)
                    .append("Kaufwert: ").append(simpelTradeJobDTO.getKaufwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                    .append(NEW_LINE)
                    .append("Zielwert: ").append(simpelTradeJobDTO.getZielwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                    .append(NEW_LINE)
                    .append("AktuellerWert: ").append(simpelTradeJobDTO.getLetztwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                    .append("Erledigt am: ").append(simpelTradeJobDTO.getErledigtAm());
        }

        return stringBuilder.toString();
    }

}
