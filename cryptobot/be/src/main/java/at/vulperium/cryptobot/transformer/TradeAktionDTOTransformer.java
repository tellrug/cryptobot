package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.entities.TradeAktion;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;


/**
 * Created by 02ub0400 on 11.01.2018.
 */
@ApplicationScoped
public class TradeAktionDTOTransformer implements TransformBothDirections<TradeAktion, TradeAktionDTO> {

    //private @Inject WaehrungService waehrungService;

    @Override
    public TradeAktion transformInverse(TradeAktionDTO source) {
        return transformInverse(source, new TradeAktion());
    }

    @Override
    public TradeAktion transformInverse(TradeAktionDTO source, TradeAktion target) {

        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setReferenzCryptoWaehrung(source.getCryptoWaehrungReferenz());
        target.setMenge(source.getMenge());
        target.setErstelltAm(source.getErstelltAm().toDate());
        target.setErledigtAm(source.getErledigtAm() != null ? source.getErledigtAm().toDate() : null);
        target.setUserId(source.getUserId());
        target.setStatus(source.getTradeStatus().name());
        target.setTradingplattform(source.getTradingPlattform().getCode());
        target.setTradeTyp(source.getTradeTyp().name());
        target.setReferenzTradeAktionId(source.getReferenzTradeAktionId());
        target.setTradeJobId(source.getTradeJobId());
        target.setTradeJobTyp(source.getTradeJobTyp() != null ? source.getTradeJobTyp().name() :  null);
        target.setPreisProEinheit(source.getPreisProEinheit());
        target.setCustomerOrderId(source.getCustomerOrderId());

        return target;
    }

    @Override
    public TradeAktionDTO transform(TradeAktion source) {
        return transform(source, new TradeAktionDTO());
    }

    @Override
    public TradeAktionDTO transform(TradeAktion source, TradeAktionDTO target) {

        target.setId(source.getId());
        target.setErstelltAm(LocalDateTime.fromDateFields(source.getErstelltAm()));
        target.setErledigtAm(source.getErledigtAm() != null ? LocalDateTime.fromDateFields(source.getErledigtAm()) : null);

        //WaehrungKurzDTO vonWaehrungKurzDTO = waehrungService.holeWaehrungKurzDTO(source.getVonWaehrung());
        //WaehrungKurzDTO zuWaehrungKurzDTO = waehrungService.holeWaehrungKurzDTO(source.getZuWaehrung());
        /*
        if (vonWaehrungKurzDTO == null || zuWaehrungKurzDTO == null) {
            //Fehler
            throw new RuntimeException("Fehler bei Transformation von TradeAktion=" + source.getId());
        }
        */

        target.setCryptoWaehrung(source.getCryptoWaehrung());
        target.setCryptoWaehrungReferenz(source.getReferenzCryptoWaehrung());
        target.setMenge(source.getMenge());
        target.setTradeStatus(TradeStatus.valueOf(source.getStatus()));
        target.setTradingPlattform(TradingPlattform.getByCode(source.getTradingplattform()));
        target.setUserId(source.getUserId());
        target.setTradeTyp(TradeTyp.valueOf(source.getTradeTyp()));
        target.setReferenzTradeAktionId(source.getReferenzTradeAktionId());
        target.setTradeJobId(source.getTradeJobId());
        target.setTradeJobTyp(source.getTradeJobTyp() != null ? TradeJobTyp.valueOf(source.getTradeJobTyp()) : null);
        target.setPreisProEinheit(source.getPreisProEinheit());
        target.setCustomerOrderId(source.getCustomerOrderId());

        return target;
    }
}
