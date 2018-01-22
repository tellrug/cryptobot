package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.entities.WechselTradeJob;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeTyp;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
@ApplicationScoped
public class WechselTradeJobDTOTransformer extends AbstractTradeJobDTOTransformer<WechselTradeJob, WechselTradeJobDTO> {

    @Override
    public WechselTradeJob transformInverse(WechselTradeJobDTO source) {
        return transformInverse(source, new WechselTradeJob());
    }

    @Override
    public WechselTradeJob transformInverse(WechselTradeJobDTO source, WechselTradeJob target) {
        //Transformieren vom allgemeinen TradeJob
        super.transformInverse(source, target);

        target.setKaufwertGrenze(source.getKaufwertGrenze());
        target.setMengeReferenzwert(source.getMengeReferenzwert());
        target.setMinimalZielSatz(source.getMinimalZielSatz());
        target.setVorgesehenerVerkaufwert(source.getVorgesehenerVerkaufwert());

        target.setTradeVersuchAm(source.getTradeVersuchAm() != null ? source.getTradeVersuchAm().toDate() : null);
        target.setTradeTyp(source.getTradeTyp().name());

        return target;
    }

    @Override
    public WechselTradeJobDTO transform(WechselTradeJob source) {
        return transform(source, new WechselTradeJobDTO());
    }

    @Override
    public WechselTradeJobDTO transform(WechselTradeJob source, WechselTradeJobDTO target) {
        //Transformieren vom allgemeinen TradeJob
        super.transform(source, target);

        target.setKaufwertGrenze(source.getKaufwertGrenze());
        target.setMengeReferenzwert(source.getMengeReferenzwert());
        target.setMinimalZielSatz(source.getMinimalZielSatz());
        target.setVorgesehenerVerkaufwert(source.getVorgesehenerVerkaufwert());

        target.setTradeVersuchAm(source.getTradeVersuchAm() != null ? LocalDateTime.fromDateFields(source.getTradeVersuchAm()) : null);
        target.setTradeTyp(TradeTyp.valueOf(source.getTradeTyp()));

        target.setTradeJobTyp(TradeJobTyp.WECHSEL);
        return target;
    }
}
