package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.entities.TradeAktion;
import at.vulperium.cryptobot.enums.TradeAktionStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by Ace on 26.12.2017.
 */
@ApplicationScoped
public class TradeAktionDTOTransformer implements TransformBothDirections<TradeAktion, TradeAktionDTO> {

    @Override
    public TradeAktion transformInverse(TradeAktionDTO source) {
        return transformInverse(source, new TradeAktion());
    }

    @Override
    public TradeAktionDTO transform(TradeAktion source) {
        return transform(source, new TradeAktionDTO());
    }

    @Override
    public TradeAktion transformInverse(TradeAktionDTO source, TradeAktion target) {

        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setErledigtAm(source.getErstelltAm().toDate());
        target.setErledigtAm(source.getErledigtAm() == null ?  null : source.getErledigtAm().toDate());
        target.setMenge(source.getMenge());
        target.setKaufwert(source.getKaufwert());
        target.setLetztwert(source.getLetztwert());
        target.setZielwert(source.getZielwert());
        target.setReferenzCryptoWaehrung(source.getCryptoWaehrungReferenz());
        target.setStatus(source.getTradeAktionStatus().getCode());
        target.setTradingplattform(source.getTradingPlattform().getCode());

        return target;
    }

    @Override
    public TradeAktionDTO transform(TradeAktion source, TradeAktionDTO target) {

        target.setId(source.getId());
        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setErstelltAm(LocalDateTime.fromDateFields(source.getErstelltAm()));
        target.setErledigtAm(source.getErledigtAm() == null ? null : LocalDateTime.fromDateFields(source.getErledigtAm()));
        target.setMenge(source.getMenge());
        target.setKaufwert(source.getKaufwert());
        target.setLetztwert(source.getLetztwert());
        target.setZielwert(source.getZielwert());
        target.setCryptoWaehrungReferenz(source.getReferenzCryptoWaehrung());
        target.setTradeAktionStatus(TradeAktionStatus.getByCode(source.getStatus()));
        target.setTradingPlattform(TradingPlattform.getByCode(source.getTradingplattform()));

        return target;
    }
}
