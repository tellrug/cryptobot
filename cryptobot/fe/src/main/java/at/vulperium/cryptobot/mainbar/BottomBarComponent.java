package at.vulperium.cryptobot.mainbar;

import at.vulperium.cryptobot.dtos.TimerJobDTO;
import at.vulperium.cryptobot.enums.TimerJobEnum;
import at.vulperium.cryptobot.services.TimerJobService;
import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.util.ViewUtils;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.util.List;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public class BottomBarComponent extends HorizontalLayout {


    private TimerJobService timerJobService = BeanProvider.getContextualReference(TimerJobService.class);

    private Label wertBeobachtungsJob;
    private Label wertTradeAktionJob;

    public void postConstruct() {
        initLayout();
        //Content setzen
        initContent();

        //setzen der Werte
        aktualisiereWerte();
    }


    private void initLayout() {

        this.setWidth(100.0f, Unit.PERCENTAGE);
        this.setHeight(53.0f, Unit.PIXELS);
        this.setMargin(new MarginInfo(false, true, false, true));

        //Styles setzen
    }


    private void initContent() {
        //Platzhalter
        HorizontalLayout horizontalLayout = new HorizontalLayout();


        Component timerjobInfoComp = initTimeJobInfoComponent();
        addComponent(timerjobInfoComp);
        addComponent(horizontalLayout);

        setComponentAlignment(timerjobInfoComp, Alignment.MIDDLE_LEFT);
        setExpandRatio(timerjobInfoComp, 15.0f);
        setExpandRatio(horizontalLayout, 85.0f);
    }


    private Component initTimeJobInfoComponent() {
        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setWidth(100f, Unit.PERCENTAGE);

        //Letzter Beobachtungsjob
        Label beobachtungsJobLabel = new Label();
        beobachtungsJobLabel.setValue("Letzte Kurs-\u00dcberpr\u00fcfung:");
        beobachtungsJobLabel.addStyleName(CryptoStyles.BOLD_CAPTION);
        beobachtungsJobLabel.addStyleName("tiny");
        beobachtungsJobLabel.addStyleName("bold");


        wertBeobachtungsJob = new Label();
        wertBeobachtungsJob.addStyleName("tiny");


        gridLayout.addComponent(beobachtungsJobLabel, 0, 0);
        gridLayout.addComponent(wertBeobachtungsJob, 1, 0);

        gridLayout.setComponentAlignment(beobachtungsJobLabel, Alignment.MIDDLE_LEFT);
        gridLayout.setComponentAlignment(wertBeobachtungsJob, Alignment.MIDDLE_RIGHT);

        //Letzter TradeAktionJob
        Label tradeAktionJobLabel = new Label();
        tradeAktionJobLabel.setValue("Letzte Trade-\u00dcberpr\u00fcfung:");
        tradeAktionJobLabel.addStyleName(CryptoStyles.BOLD_CAPTION);
        tradeAktionJobLabel.addStyleName("tiny");
        tradeAktionJobLabel.addStyleName("bold");

        wertTradeAktionJob = new Label();
        wertTradeAktionJob.addStyleName("tiny");

        gridLayout.addComponent(tradeAktionJobLabel, 0, 1);
        gridLayout.addComponent(wertTradeAktionJob, 1, 1);

        gridLayout.setComponentAlignment(tradeAktionJobLabel, Alignment.MIDDLE_LEFT);
        gridLayout.setComponentAlignment(wertTradeAktionJob, Alignment.MIDDLE_RIGHT);


        gridLayout.setColumnExpandRatio(0, 0.4f);
        gridLayout.setColumnExpandRatio(1, 0.6f);

        return gridLayout;
    }


    public void aktualisiereWerte() {
        List<TimerJobDTO> alleTimerJobDTOList = timerJobService.holeAlleTimerJobs();

        for (TimerJobDTO timerJobDTO : alleTimerJobDTOList) {

            String wert = timerJobDTO.getLetzteDurchfuehrungAm() == null ? "-" : ViewUtils.dateTimeToStringOhneSkunden(timerJobDTO.getLetzteDurchfuehrungAm());
            if (timerJobDTO.getTimerJobEnum() == TimerJobEnum.TRADE_AKTION) {
                wertTradeAktionJob.setValue(wert);
            }
            if (timerJobDTO.getTimerJobEnum() == TimerJobEnum.TRADE_BEOBACHTUNG) {
                wertBeobachtungsJob.setValue(wert);
            }
        }
    }
}
