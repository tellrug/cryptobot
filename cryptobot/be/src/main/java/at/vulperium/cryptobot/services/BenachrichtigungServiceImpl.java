package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.TradeJobDTO;
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
    public void versendeBenachrichtigung(List<TradeJobDTO> tradeJobDTOList, BenachrichtigungTyp benachrichtigungTyp, TradingPlattform tradingPlattform) {
        if (CollectionUtils.isEmpty(tradeJobDTOList)) {
            return;
        }

        if (benachrichtigungTyp == BenachrichtigungTyp.MAIL) {
            String betreff = erstelleBenachrichtigungsBetreff(tradeJobDTOList, tradingPlattform);

            StringBuilder textStrBuilder = new StringBuilder();
            for (TradeJobDTO tradeJobDTO : tradeJobDTOList) {
                String tmpText = erstelleBenachrichtigungsText(tradeJobDTO);
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
    public void versendeBenachrichtigung(TradeJobDTO tradeJobDTO, BenachrichtigungTyp benachrichtigungTyp) {
        if (benachrichtigungTyp == BenachrichtigungTyp.MAIL) {
            //versende Mail-Benachrichtigung
            String betreff = erstelleBenachrichtigungsBetreff(tradeJobDTO);
            String text = erstelleBenachrichtigungsText(tradeJobDTO);
            mailService.versendeMail(betreff, text);
            return;
        }
        //TODO weitere BenachrichtigungsTypen hinzufuegen
        throw new IllegalStateException("Benachrichtigung von TradeJob=" + tradeJobDTO.getId() + " fehlgeschlagen. BenachrichtigungTyp="
                + benachrichtigungTyp + " nicht vorgesehen!");
    }

    @Override
    public String erstelleBenachrichtigungsBetreff(List<TradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(reportBetreff.get());
        for (TradeJobDTO tradeJobDTO : tradeJobDTOList) {
            stringBuilder.append(tradeJobDTO.getCryptoWaehrung()).append(" ");
        }
        stringBuilder.append( " - ").append(tradingPlattform.name());
        return stringBuilder.toString();
    }

    @Override
    public String erstelleBenachrichtigungsBetreff(TradeJobDTO tradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(reportBetreff.get())                                            //Betreff-Prefix
                .append(tradeJobDTO.getCryptoWaehrung())                                //Coin
                .append(" ")
                .append(tradeJobDTO.getTradeAktionEnum().name())                         //Ereignis
                .append(" - ")
                .append(tradeJobDTO.getTradingPlattform().name());                      //Trading-Plattform

        return stringBuilder.toString();
    }

    @Override
    public String erstelleBenachrichtigungsText(TradeJobDTO tradeJobDTO) {

        String tradeAktionInformation;
        if (tradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
            tradeAktionInformation = erstelleKaufBenachrichtigungsText(tradeJobDTO);
        }
        else if (tradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
            tradeAktionInformation = erstelleVerkaufBenachrichtigungsText(tradeJobDTO);
        }
        else {
            throw new IllegalStateException("Fehlerhafter TradeTyp= " + tradeJobDTO.getTradeAktionEnum().getTradeTyp() + " bei TradeJob=" + tradeJobDTO.getId());
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Trade: ").append(tradeJobDTO.getCryptoWaehrung()).append(" <--> ").append(tradeJobDTO.getCryptoWaehrungReferenz())
                .append("   -   ").append(tradeJobDTO.getTradingPlattform().name())
                .append(NEW_LINE)
                .append(tradeAktionInformation);
        return stringBuilder.toString();
    }

    private String erstelleKaufBenachrichtigungsText(TradeJobDTO tradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("TradeAktion: Kauf von ").append(tradeJobDTO.getMenge()).append(" ").append(tradeJobDTO.getCryptoWaehrung())
                .append(NEW_LINE)
                .append("Zielwert: ").append(tradeJobDTO.getZielwert()).append(" ").append(tradeJobDTO.getCryptoWaehrungReferenz())
                .append(NEW_LINE)
                .append("AktuellerWert: ").append(tradeJobDTO.getLetztwert()).append(" ").append(tradeJobDTO.getCryptoWaehrungReferenz())
                .append("Erledigt am: ").append(tradeJobDTO.getErledigtAm());
        return stringBuilder.toString();
    }

    private String erstelleVerkaufBenachrichtigungsText(TradeJobDTO tradeJobDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("TradeAktion: Verkauf von ").append(tradeJobDTO.getMenge()).append(" ").append(tradeJobDTO.getCryptoWaehrung())
                .append(NEW_LINE)
                .append("Kaufwert: ").append(tradeJobDTO.getKaufwert()).append(" ").append(tradeJobDTO.getCryptoWaehrungReferenz())
                .append(NEW_LINE)
                .append("Zielwert: ").append(tradeJobDTO.getZielwert()).append(" ").append(tradeJobDTO.getCryptoWaehrungReferenz())
                .append(NEW_LINE)
                .append("AktuellerWert: ").append(tradeJobDTO.getLetztwert()).append(" ").append(tradeJobDTO.getCryptoWaehrungReferenz())
                .append("Erledigt am: ").append(tradeJobDTO.getErledigtAm());
        return stringBuilder.toString();
    }

}
