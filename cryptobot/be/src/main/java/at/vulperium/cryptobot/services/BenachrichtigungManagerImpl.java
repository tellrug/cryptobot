package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.BenachrichtigungDTO;
import at.vulperium.cryptobot.enums.BenachrichtigungTyp;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 22.02.2018.
 */
@ApplicationScoped
public class BenachrichtigungManagerImpl implements BenachrichtigungManager{

    private static final Logger logger = LoggerFactory.getLogger(BenachrichtigungManagerImpl.class);

    private @Inject BenachrichtigungService benachrichtigungService;

    private Map<BenachrichtigungTyp, List<BenachrichtigungDTO>> arbeitskorb = new HashMap<>();

    public synchronized void registriereBenachrichtigung(BenachrichtigungDTO benachrichtigungDTO) {
        Validate.notNull(benachrichtigungDTO, "BenachrichtigungDTO ist null.");
        Validate.notNull(benachrichtigungDTO.getBenachrichtigungTyp(), "BenachrichtigungTyp ist null.");

        if (!arbeitskorb.containsKey(benachrichtigungDTO.getBenachrichtigungTyp())) {
            arbeitskorb.put(benachrichtigungDTO.getBenachrichtigungTyp(), new ArrayList<>());
        }
        arbeitskorb.get(benachrichtigungDTO.getBenachrichtigungTyp()).add(benachrichtigungDTO);
    }

    public synchronized void fuehreBenachrichtigungDurch() {
        //Durchfuehren der Benachrichtigungen nach BenachrichtigungsTyp
        for (BenachrichtigungTyp benachrichtigungTyp : arbeitskorb.keySet()) {
            List<BenachrichtigungDTO> benachrichtigungDTOList = arbeitskorb.get(benachrichtigungTyp);

            //Versenden aller Benachrichtigungen (Huer koennte noch nach User aufgeteilt werden)
            benachrichtigungService.versendeBenachrichtigungen(benachrichtigungDTOList, benachrichtigungTyp);
        }

        logger.info("Alle BenachrichtigungsAUfgaben wurden erledigt. Arbeitskorb wird geleert.");
        //Leeren der BenachrichtigungsMap
        arbeitskorb.clear();
    }
}
