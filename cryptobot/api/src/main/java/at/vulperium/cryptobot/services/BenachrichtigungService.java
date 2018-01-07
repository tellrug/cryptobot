package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface BenachrichtigungService {

    void versendeBenachrichtigung(List<TradeJobDTO> tradeJobDTOList, BenachrichtigungTyp benachrichtigungTyp, TradingPlattform tradingPlattform);

    void versendeBenachrichtigung(TradeJobDTO tradeJobDTO, BenachrichtigungTyp benachrichtigungTyp);

    String erstelleBenachrichtigungsBetreff(List<TradeJobDTO> tradeJobDTOList, TradingPlattform tradingPlattform);

    String erstelleBenachrichtigungsBetreff(TradeJobDTO tradeJobDTO);

    String erstelleBenachrichtigungsText(TradeJobDTO tradeJobDTO);
}
