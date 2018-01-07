package at.vulperium.cryptobot.base.navigator;

import at.vulperium.cryptobot.base.AbstractViewController;
import at.vulperium.cryptobot.base.components.MainLayout;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.UI;
import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class CryptobotNavigator extends Navigator {

    private CDIViewProvider cdiViewProvider = BeanProvider.getContextualReference(CDIViewProvider.class);

    public CryptobotNavigator(final MainLayout mainLayout) {
        super(UI.getCurrent(), mainLayout.getContentContainer());

        initViewChangeListener();
        initViewProviders();
    }

    private void initViewChangeListener() {
        addViewChangeListener(new BaseViewChangeListener() {

            private static final long serialVersionUID = -5160063869821138442L;

            @Override
            public boolean beforeViewChange(ViewChangeEvent viewChangeEvent) {
                View view = cdiViewProvider.getView(viewChangeEvent.getViewName());

                //Aktualisieren der Badge-Infos
                //aktualisiereBadgeInfo();

                if (view instanceof AbstractViewController) {
                    return ((AbstractViewController) view).beforeViewChange(viewChangeEvent);
                }
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                //TODO hier ist was zu tun
            }
        });

        //Aktualisieren der Badge-Infos
        //aktualisiereBadgeInfo();
    }


    private void initViewProviders() {
        addProvider(cdiViewProvider);
    }
}
