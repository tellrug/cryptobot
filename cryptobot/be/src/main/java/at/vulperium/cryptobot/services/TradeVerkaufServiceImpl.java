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


@ApplicationScoped
public class TradeVerkaufServiceImpl extends AbstractTradeService implements TradeVerkaufService {

    public static final Logger logger = LoggerFactory.getLogger(TradeVerkaufServiceImpl.class);

    @Override
    public void verarbeiteVerkaufAktion(TradeJobDTO tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        verarbeiteTradeJob(tradeJobDTO, wsCryptoCoinDTO);
    }

    protected boolean ermittleZielErreicht(TradeJobDTO tradeJobDTO, BigDecimal aktuellerKurs) {
        //Bei VERKAUF ist ziel erreicht wenn der aktuelle Kurs ueber dem Zielkurs liegt
        return aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 1 || aktuellerKurs.compareTo(tradeJobDTO.getZielwert()) == 0;
    }


    protected TradeJobStatus ermittleNeuenStatusWennErledigt(Long tradeAktionId, boolean zielErreicht, Trend trend) {
        if (zielErreicht) {
            if (trend == Trend.ABWAERTS) {
                //Trend ist nun fallend --> Verkauf
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, Kurs faellt wieder. Es kann verkauft werden", tradeAktionId);
                return TradeJobStatus.VERKAUF_ABGESCHLOSSEN;
            } else {
                //Trend ist konstant oder aufwaerts
                logger.info("Ziel fuer Trade tradeAktionId={} wurde erreicht, jedoch wird mit VERKAUF noch gewartet.", tradeAktionId);
                //Nichts tun
                return null;
            }
        }

        //Ziel wurde noch nicht erreicht
        logger.info("Ziel fuer Trade tradeAktionId={} wurde noch nicht erreicht, es wird noch gewartet", tradeAktionId);
        //Keine Aktion
        return null;
    }


}
