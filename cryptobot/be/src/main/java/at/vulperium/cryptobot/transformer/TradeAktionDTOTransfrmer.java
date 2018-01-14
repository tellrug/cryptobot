package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.entities.TradeAktion;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;


/**
 * Created by 02ub0400 on 11.01.2018.
 */
@ApplicationScoped
public class TradeAktionDTOTransfrmer implements TransformBothDirections<TradeAktion, TradeAktionDTO> {

    //private @Inject WaehrungService waehrungService;

    @Override
    public TradeAktion transformInverse(TradeAktionDTO source) {
        return transformInverse(source, new TradeAktion());
    }

    @Override
    public TradeAktion transformInverse(TradeAktionDTO source, TradeAktion target) {

        target.setVonMenge(source.getVonMenge());
        target.setZuMenge(source.getZuMenge());
        target.setVonWaehrung(source.getVonWaehrung());
        target.setZuWaehrung(source.getZuWaehrung());
        target.setErstelltAm(source.getErstelltAm().toDate());
        target.setUserId(source.getUserId());
        target.setStatus(source.getTradeStatus().name());
        target.setTradingplattform(source.getTradingPlattform().getCode());

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

        //WaehrungKurzDTO vonWaehrungKurzDTO = waehrungService.holeWaehrungKurzDTO(source.getVonWaehrung());
        //WaehrungKurzDTO zuWaehrungKurzDTO = waehrungService.holeWaehrungKurzDTO(source.getZuWaehrung());
        /*
        if (vonWaehrungKurzDTO == null || zuWaehrungKurzDTO == null) {
            //Fehler
            throw new RuntimeException("Fehler bei Transformation von TradeAktion=" + source.getId());
        }
        */

        target.setVonWaehrung(source.getVonWaehrung());
        target.setZuWaehrung(source.getZuWaehrung());
        target.setVonMenge(source.getVonMenge());
        target.setZuMenge(source.getZuMenge());
        target.setTradeStatus(TradeStatus.valueOf(source.getStatus()));
        target.setTradingPlattform(TradingPlattform.getByCode(source.getTradingplattform()));
        target.setUserId(source.getUserId());

        return null;
    }
}
