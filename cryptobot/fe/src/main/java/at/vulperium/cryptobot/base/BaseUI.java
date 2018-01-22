package at.vulperium.cryptobot.base;

import at.vulperium.cryptobot.base.components.MainLayout;
import at.vulperium.cryptobot.base.navigator.CryptobotNavigator;
import at.vulperium.cryptobot.mainbar.BottomBarComponent;
import at.vulperium.cryptobot.mainbar.MenuBarComponent;
import at.vulperium.cryptobot.mainbar.TopBarComponent;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
@Theme("cryptotheme")
public class BaseUI extends UI implements Serializable {

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        //setze den Titel der Seite
        getPage().setTitle("CryptoBot");

        final MenuBarComponent menuBarComponent = getMenuBarComponent();
        final TopBarComponent topBarComponent = getTopBarComponent();
        final BottomBarComponent bottomBarComponent = getBottomBarComponent();


        final MainLayout mainlayout = new MainLayout(menuBarComponent, topBarComponent, bottomBarComponent);

        //Initialisierung Navigator
        setupNavigator(mainlayout);
        setContent(mainlayout);
    }

    private void setupNavigator(final MainLayout mainLayout) {
        Navigator navigator = new CryptobotNavigator(mainLayout);

        //TODO Z hier wird ein Event geworfen nachdem sich der User eingeloggt hat??

        setNavigator(navigator);
        navigator.navigateTo(ViewId.DASHBOARD);
    }

    private TopBarComponent getTopBarComponent() {
        TopBarComponent topBarComponent = new TopBarComponent();
        BeanProvider.injectFields(topBarComponent);
        topBarComponent.postConstruct();
        return topBarComponent;
    }

    private MenuBarComponent getMenuBarComponent() {
        MenuBarComponent menuBarComponent = new MenuBarComponent();
        BeanProvider.injectFields(menuBarComponent);
        menuBarComponent.postConstruct();
        return menuBarComponent;
    }

    private BottomBarComponent getBottomBarComponent() {
        BottomBarComponent bottomBarComponent = new BottomBarComponent();
        BeanProvider.injectFields(bottomBarComponent);
        bottomBarComponent.postConstruct();
        return bottomBarComponent;
    }
}
