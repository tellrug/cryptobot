package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.dtos.BenachrichtigungDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;

import java.util.List;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public interface BenachrichtigungService {

    void versendeBenachrichtigungen(List<BenachrichtigungDTO> benachrichtigungDTOList, BenachrichtigungTyp benachrichtigungTyp);

    String erstelleBenachrichtigungsBetreff(List<BenachrichtigungDTO> benachrichtigungDTOList, TradingPlattform tradingPlattform);

    BenachrichtigungDTO erstelleBenachrichtigungsDTO(AbstractTradeJobDTO abstractTradeJobDTO, BenachrichtigungTyp benachrichtigungTyp);

}
