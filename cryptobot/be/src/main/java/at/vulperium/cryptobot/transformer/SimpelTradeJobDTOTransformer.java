package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.entities.SimpelTradeJob;
import at.vulperium.cryptobot.enums.TradeJobTyp;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by Ace on 26.12.2017.
 */
@ApplicationScoped
public class SimpelTradeJobDTOTransformer extends AbstractTradeJobDTOTransformer<SimpelTradeJob, SimpelTradeJobDTO>  {

    @Override
    public SimpelTradeJob transformInverse(SimpelTradeJobDTO source) {
        return transformInverse(source, new SimpelTradeJob());
    }

    @Override
    public SimpelTradeJobDTO transform(SimpelTradeJob source) {
        return transform(source, new SimpelTradeJobDTO());
    }

    @Override
    public SimpelTradeJob transformInverse(SimpelTradeJobDTO source, SimpelTradeJob target) {
        super.transformInverse(source, target);
        return target;
    }

    @Override
    public SimpelTradeJobDTO transform(SimpelTradeJob source, SimpelTradeJobDTO target) {
        super.transform(source, target);
        target.setTradeJobTyp(TradeJobTyp.SIMPEL);
        return target;
    }
}
