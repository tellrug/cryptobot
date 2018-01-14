package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.TradeJobDTO;
import at.vulperium.cryptobot.entities.TradeJob;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by Ace on 26.12.2017.
 */
@ApplicationScoped
public class TradeJobDTOTransformer implements TransformBothDirections<TradeJob, TradeJobDTO> {

    @Override
    public TradeJob transformInverse(TradeJobDTO source) {
        return transformInverse(source, new TradeJob());
    }

    @Override
    public TradeJobDTO transform(TradeJob source) {
        return transform(source, new TradeJobDTO());
    }

    @Override
    public TradeJob transformInverse(TradeJobDTO source, TradeJob target) {

        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setErstelltAm(source.getErstelltAm().toDate());
        target.setErledigtAm(source.getErledigtAm() == null ?  null : source.getErledigtAm().toDate());
        target.setMenge(source.getMenge());
        target.setKaufwert(source.getKaufwert());
        target.setLetztwert(source.getLetztwert());
        target.setZielwert(source.getZielwert());
        target.setReferenzCryptoWaehrung(source.getCryptoWaehrungReferenz());
        target.setJobstatus(source.getTradeAktionEnum().getCode());
        target.setTradestatus(source.getTradeStatus().name());
        target.setTradingplattform(source.getTradingPlattform().getCode());

        return target;
    }

    @Override
    public TradeJobDTO transform(TradeJob source, TradeJobDTO target) {

        target.setId(source.getId());
        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setErstelltAm(LocalDateTime.fromDateFields(source.getErstelltAm()));
        target.setErledigtAm(source.getErledigtAm() == null ? null : LocalDateTime.fromDateFields(source.getErledigtAm()));
        target.setMenge(source.getMenge());
        target.setKaufwert(source.getKaufwert());
        target.setLetztwert(source.getLetztwert());
        target.setZielwert(source.getZielwert());
        target.setCryptoWaehrungReferenz(source.getReferenzCryptoWaehrung());
        target.setTradeAktionEnum(TradeAktionEnum.getByCode(source.getJobstatus()));
        target.setTradeStatus(TradeStatus.valueOf(source.getTradestatus()));
        target.setTradingPlattform(TradingPlattform.getByCode(source.getTradingplattform()));

        target.setTradeJobTyp(TradeJobTyp.SIMPEL);
        return target;
    }
}
