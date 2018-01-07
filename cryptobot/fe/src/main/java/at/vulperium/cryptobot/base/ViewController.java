package at.vulperium.cryptobot.base;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;

/**
 * Created by 02ub0400 on 22.09.2017.
 */
public interface ViewController extends View {

    void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent);

    Component getUIComponent();

    void createViewLayout();

    boolean viewLoaded();

    @Override
    Component getViewComponent();
}
