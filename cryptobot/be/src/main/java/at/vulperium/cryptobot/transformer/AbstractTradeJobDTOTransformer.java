package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.entities.AbstractTradeJob;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.joda.time.LocalDateTime;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
public abstract class AbstractTradeJobDTOTransformer<K extends AbstractTradeJob, H extends AbstractTradeJobDTO> implements TransformBothDirections<K, H> {

    @Override
    public K transformInverse(H source) {
        throw new RuntimeException("Fehler bei Aufruf von abstrakten Transformer.");
    }

    @Override
    public K transformInverse(H source, K target) {

        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setErstelltAm(source.getErstelltAm().toDate());
        target.setErledigtAm(source.getErledigtAm() == null ? null : source.getErledigtAm().toDate());
        target.setMenge(source.getMenge());
        target.setKaufwert(source.getKaufwert());
        target.setLetztwert(source.getLetztwert());
        target.setZielwert(source.getZielwert());
        target.setReferenzCryptoWaehrung(source.getCryptoWaehrungReferenz());
        target.setTradeAktion(source.getTradeAktionEnum().getCode());
        target.setTradestatus(source.getTradeStatus().name());
        target.setTradingplattform(source.getTradingPlattform().getCode());

        return target;
    }

    @Override
    public H transform(K source) {
        throw new RuntimeException("Fehler bei Aufruf von abstrakten Transformer.");
    }

    @Override
    public H transform(K source, H target) {

        target.setId(source.getId());
        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setErstelltAm(LocalDateTime.fromDateFields(source.getErstelltAm()));
        target.setErledigtAm(source.getErledigtAm() == null ? null : LocalDateTime.fromDateFields(source.getErledigtAm()));
        target.setMenge(source.getMenge());
        target.setKaufwert(source.getKaufwert());
        target.setLetztwert(source.getLetztwert());
        target.setZielwert(source.getZielwert());
        target.setCryptoWaehrungReferenz(source.getReferenzCryptoWaehrung());
        target.setTradeAktionEnum(TradeAktionEnum.getByCode(source.getTradeAktion()));
        target.setTradeStatus(TradeStatus.valueOf(source.getTradestatus()));
        target.setTradingPlattform(TradingPlattform.getByCode(source.getTradingplattform()));

        return target;
    }
}
