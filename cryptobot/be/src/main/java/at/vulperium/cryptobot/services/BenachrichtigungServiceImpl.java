package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;

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
    public void versendeBenachrichtigung(List<SimpelTradeJobDTO> simpelTradeJobDTOList, BenachrichtigungTyp benachrichtigungTyp, TradingPlattform tradingPlattform) {
        if (CollectionUtils.isEmpty(simpelTradeJobDTOList)) {
            return;
        }

        if (benachrichtigungTyp == BenachrichtigungTyp.MAIL) {
            String betreff = erstelleBenachrichtigungsBetreff(simpelTradeJobDTOList, tradingPlattform);

            StringBuilder textStrBuilder = new StringBuilder();
            for (SimpelTradeJobDTO simpelTradeJobDTO : simpelTradeJobDTOList) {
                String tmpText = erstelleBenachrichtigungsText(simpelTradeJobDTO);
                textStrBuilder.append(tmpText);
                textStrBuilder.append(NEW_LINE);
                textStrBuilder.append(TRENNER);
                textStrBuilder.append(NEW_LINE);
            }
            String text = textStrBuilder.toString();
            mailService.versendeMail(betreff, text);
            return;
        }
        //TODO weitere BenachrichtigungsTypen hinzufuegen
        throw new IllegalStateException("Benachrichtigung von mehreren TradeJobs fehlgeschlagen. BenachrichtigungTyp="
                + benachrichtigungTyp + " nicht vorgesehen!");
    }

    @Override
    public void versendeBenachrichtigung(SimpelTradeJobDTO simpelTradeJobDTO, BenachrichtigungTyp benachrichtigungTyp) {
        if (benachrichtigungTyp == BenachrichtigungTyp.MAIL) {
            //versende Mail-Benachrichtigung
            String betreff = erstelleBenachrichtigungsBetreff(simpelTradeJobDTO);
            String text = erstelleBenachrichtigungsText(simpelTradeJobDTO);
            mailService.versendeMail(betreff, text);
            return;
        }
        //TODO weitere BenachrichtigungsTypen hinzufuegen
        throw new IllegalStateException("Benachrichtigung von TradeJob=" + simpelTradeJobDTO.getId() + " fehlgeschlagen. BenachrichtigungTyp="
                + benachrichtigungTyp + " nicht vorgesehen!");
    }

    @Override
    public String erstelleBenachrichtigungsBetreff(List<SimpelTradeJobDTO> simpelTradeJobDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(reportBetreff.get());
        for (SimpelTradeJobDTO simpelTradeJobDTO : simpelTradeJobDTOList) {
            stringBuilder.append(simpelTradeJobDTO.getCryptoWaehrung()).append(" ");
        }
        stringBuilder.append( " - ").append(tradingPlattform.name());
        return stringBuilder.toString();
    }

    @Override
    public String erstelleBenachrichtigungsBetreff(SimpelTradeJobDTO simpelTradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(reportBetreff.get())                                            //Betreff-Prefix
                .append(simpelTradeJobDTO.getCryptoWaehrung())                                //Coin
                .append(" ")
                .append(simpelTradeJobDTO.getTradeAktionEnum().name())                         //Ereignis
                .append(" - ")
                .append(simpelTradeJobDTO.getTradingPlattform().name());                      //Trading-Plattform

        return stringBuilder.toString();
    }

    @Override
    public String erstelleBenachrichtigungsText(SimpelTradeJobDTO simpelTradeJobDTO) {

        String tradeAktionInformation;
        if (simpelTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
            tradeAktionInformation = erstelleKaufBenachrichtigungsText(simpelTradeJobDTO);
        }
        else if (simpelTradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
            tradeAktionInformation = erstelleVerkaufBenachrichtigungsText(simpelTradeJobDTO);
        }
        else {
            throw new IllegalStateException("Fehlerhafter TradeTyp= " + simpelTradeJobDTO.getTradeAktionEnum().getTradeTyp() + " bei TradeJob=" + simpelTradeJobDTO.getId());
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Trade: ").append(simpelTradeJobDTO.getCryptoWaehrung()).append(" <--> ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                .append("   -   ").append(simpelTradeJobDTO.getTradingPlattform().name())
                .append(NEW_LINE)
                .append(tradeAktionInformation);
        return stringBuilder.toString();
    }

    private String erstelleKaufBenachrichtigungsText(SimpelTradeJobDTO simpelTradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("TradeAktion: Kauf von ").append(simpelTradeJobDTO.getMenge()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrung())
                .append(NEW_LINE)
                .append("Zielwert: ").append(simpelTradeJobDTO.getZielwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                .append(NEW_LINE)
                .append("AktuellerWert: ").append(simpelTradeJobDTO.getLetztwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                .append("Erledigt am: ").append(simpelTradeJobDTO.getErledigtAm());
        return stringBuilder.toString();
    }

    private String erstelleVerkaufBenachrichtigungsText(SimpelTradeJobDTO simpelTradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("TradeAktion: Verkauf von ").append(simpelTradeJobDTO.getMenge()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrung())
                .append(NEW_LINE)
                .append("Kaufwert: ").append(simpelTradeJobDTO.getKaufwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                .append(NEW_LINE)
                .append("Zielwert: ").append(simpelTradeJobDTO.getZielwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                .append(NEW_LINE)
                .append("AktuellerWert: ").append(simpelTradeJobDTO.getLetztwert()).append(" ").append(simpelTradeJobDTO.getCryptoWaehrungReferenz())
                .append("Erledigt am: ").append(simpelTradeJobDTO.getErledigtAm());
        return stringBuilder.toString();
    }

}
