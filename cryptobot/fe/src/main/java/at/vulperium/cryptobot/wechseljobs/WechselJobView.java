package at.vulperium.cryptobot.wechseljobs;

import at.vulperium.cryptobot.base.AbstractViewController;
import at.vulperium.cryptobot.base.ViewId;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradejobs.TradeJobComponent;
import at.vulperium.cryptobot.wechseljobs.service.WechselTradeJobViewService;
import at.vulperium.cryptobot.wechseljobs.vo.WechselTradeJobVO;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIView;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
@Theme("cryptotheme")
@CDIView(value = ViewId.WECHSEL_JOBS)
public class WechselJobView extends AbstractViewController {

    private @Inject WechselTradeJobViewService wechselTradeJobViewService;

    private List<WechselTradeJobVO> wechselTradeJobVOList;

    @Override
    protected Component createCustomLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();

        Layout resultLayout = initTradeJobsLayout();

        mainLayout.addComponent(resultLayout);

        return mainLayout;
    }

    @Override
    protected void ladeViewDaten(ViewChangeEvent viewChangeEvent) {
        wechselTradeJobVOList = wechselTradeJobViewService.holeAlleWechselTradejobVOs();
    }

    @Override
    protected String getPageCaption() {
        return "Wechsel-Jobs";
    }

    @Override
    protected String getIDKey() {
        return ViewId.WECHSEL_JOBS;
    }


    private Layout initTradeJobsLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(false);
        verticalLayout.setSizeFull();
        verticalLayout.setWidth(100f, Sizeable.Unit.PERCENTAGE);

        //Uebersicht
        WechselJobComponent wechselJobComponent = new WechselJobComponent(wechselTradeJobVOList);
        //TopBar
        //Component funktionsLeiste = erstelleFunktionsleiste(tradeJobComponent);
        verticalLayout.addComponent(wechselJobComponent);
        return verticalLayout;
    }
}
