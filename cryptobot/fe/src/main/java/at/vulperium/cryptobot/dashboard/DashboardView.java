package at.vulperium.cryptobot.dashboard;

import at.vulperium.cryptobot.base.AbstractViewController;
import at.vulperium.cryptobot.base.ViewId;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by Ace on 05.01.2018.
 */
@Theme("cryptotheme")
@CDIView(value = ViewId.DASHBOARD)
public class DashboardView  extends AbstractViewController {

    @Override
    protected void ladeViewDaten(ViewChangeEvent viewChangeEvent) {

    }

    @Override
    protected Component createCustomLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();

        Layout tradeAktionLayout = initTradeAktionLayout();
        mainLayout.addComponent(tradeAktionLayout);

        return mainLayout;
    }

    @Override
    protected String getPageCaption() {
        return "Dashboard";
    }

    @Override
    protected String getIDKey() {
        return ViewId.DASHBOARD;
    }

    private Layout initTradeAktionLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(false);
        verticalLayout.setSizeFull();
        verticalLayout.setWidth(75f, Sizeable.Unit.PERCENTAGE);

        Label label = new Label("Hier wird das Dashboard angezeigt!");
        verticalLayout.addComponent(label);

        return verticalLayout;
    }
}
