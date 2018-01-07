package at.vulperium.cryptobot.tradejobs;

import at.vulperium.cryptobot.base.AbstractViewController;
import at.vulperium.cryptobot.base.ViewId;
import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.tradejobs.vo.FilterVO;
import at.vulperium.cryptobot.util.FilterFunktionsComponent;
import at.vulperium.cryptobot.view.ConfigBearbeitenWindow;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by Ace on 05.01.2018.
 */
@Theme("cryptotheme")
@CDIView(value = ViewId.TRADE_JOBS)
public class TradeJobView extends AbstractViewController {

    @Override
    protected void ladeViewDaten(ViewChangeEvent viewChangeEvent) {

    }

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
    protected String getPageCaption() {
        return "Trade-Jobs";
    }

    @Override
    protected String getIDKey() {
        return ViewId.TRADE_JOBS;
    }

    private Layout initTradeJobsLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(false);
        verticalLayout.setSizeFull();
        verticalLayout.setWidth(100f, Sizeable.Unit.PERCENTAGE);

        //Uebersicht
        TradeJobComponent tradeJobComponent = new TradeJobComponent();
        //TopBar
        //Component funktionsLeiste = erstelleFunktionsleiste(tradeJobComponent);
        verticalLayout.addComponent(tradeJobComponent);
        return verticalLayout;
    }
}
