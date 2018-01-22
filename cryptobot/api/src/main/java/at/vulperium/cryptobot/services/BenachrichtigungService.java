package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface BenachrichtigungService {

    void versendeBenachrichtigung(List<SimpelTradeJobDTO> simpelTradeJobDTOList, BenachrichtigungTyp benachrichtigungTyp, TradingPlattform tradingPlattform);

    void versendeBenachrichtigung(SimpelTradeJobDTO simpelTradeJobDTO, BenachrichtigungTyp benachrichtigungTyp);

    String erstelleBenachrichtigungsBetreff(List<SimpelTradeJobDTO> simpelTradeJobDTOList, TradingPlattform tradingPlattform);

    String erstelleBenachrichtigungsBetreff(SimpelTradeJobDTO simpelTradeJobDTO);

    String erstelleBenachrichtigungsText(SimpelTradeJobDTO simpelTradeJobDTO);
}
