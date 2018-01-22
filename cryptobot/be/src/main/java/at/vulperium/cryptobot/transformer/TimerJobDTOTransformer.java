package at.vulperium.cryptobot.transformer;

import at.vulperium.cryptobot.dtos.TimerJobDTO;
import at.vulperium.cryptobot.entities.TimerJob;
import at.vulperium.cryptobot.enums.TimerJobEnum;
import at.vulperium.cryptobot.services.TransformBothDirections;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;


/**
 * Created by 02ub0400 on 15.01.2018.
 */
@ApplicationScoped
public class TimerJobDTOTransformer implements TransformBothDirections<TimerJob, TimerJobDTO> {


    @Override
    public TimerJob transformInverse(TimerJobDTO source) {
        return transformInverse(source, new TimerJob());
    }

    @Override
    public TimerJob transformInverse(TimerJobDTO source, TimerJob target) {

        target.setErstelltAm(source.getErstelltAm().toDate());
        target.setNaechsteDurchfuehrungAm(source.getNaechsteDurchfuehrungAm().toDate());
        target.setLetzteDurchfuehrungAm(source.getLetzteDurchfuehrungAm().toDate());
        target.setBezeichnung(source.getTimerJobEnum().name());

        return target;
    }

    @Override
    public TimerJobDTO transform(TimerJob source) {
        return transform(source, new TimerJobDTO());
    }

    @Override
    public TimerJobDTO transform(TimerJob source, TimerJobDTO target) {

        target.setId(source.getId());
        target.setErstelltAm(LocalDateTime.fromDateFields(source.getErstelltAm()));
        target.setNaechsteDurchfuehrungAm( source.getNaechsteDurchfuehrungAm() != null ? LocalDateTime.fromDateFields(source.getNaechsteDurchfuehrungAm()) : null);
        target.setLetzteDurchfuehrungAm(source.getLetzteDurchfuehrungAm() != null ? LocalDateTime.fromDateFields(source.getLetzteDurchfuehrungAm()): null);
        target.setTimerJobEnum(TimerJobEnum.valueOf(source.getBezeichnung()));

        return target;
    }
}
