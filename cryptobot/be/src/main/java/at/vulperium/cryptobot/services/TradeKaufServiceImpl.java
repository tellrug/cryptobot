package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobStatus;
import at.vulperium.cryptobot.enums.Trend;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
@ApplicationScoped
public class TradeKaufServiceImpl extends AbstractTradeService implements TradeKaufService {

    public static final Logger logger = LoggerFactory.getLogger(TradeKaufServiceImpl.class);

    @Override
    public void verarbeiteKaufAktion(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        verarbeiteTradeJob(tradeJobDTO, wsCryptoCoinDTO);
    }

    @Override
    protected TradeJobStatus ermittleNeuenStatusWennErledigt(Long tradeAktionId, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.AUFWAERTS) {
                //Trend ist nun fallend --> Verkauf
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, Kurs steigt wieder. Es kann gekauft werden", tradeAktionId);
                return TradeJobStatus.KAUF_ABGESCHLOSSEN;
            }
            else {
                //Trend ist konstant oder abwaerts
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, jedoch wird mit KAUF noch gewartet.", tradeAktionId);
                //Nichts tun
                return null;
            }
        }

        //Ziel wurde noch nicht erreicht
        logger.info("Ziel fuer Trade tradeAktionId={} wurde noch nicht erreicht, es wird noch gewartet", tradeAktionId);
        //Keine Aktion
        return null;
    }

    @Override
    protected boolean ermittleZielErreicht(TradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei KAUF ist das Ziel erreicht wenn der aktuelle Kurs unter dem Zielkurs liegt
        return aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == -1 || aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 0;
    }
}
