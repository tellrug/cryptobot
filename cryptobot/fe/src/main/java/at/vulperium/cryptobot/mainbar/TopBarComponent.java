package at.vulperium.cryptobot.mainbar;

import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.view.ConfigBearbeitenWindow;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;

/**
 * Created by 02ub0400 on 20.09.2017.
 */
public class TopBarComponent extends HorizontalLayout {

    private static final long serialVersionUID = 7236357621790096155L;

    //private @Inject UserInfo userInfo;

    public TopBarComponent() {
    }

    public void postConstruct() {
        initLayout();
        //Content setzen
        initContent();
    }

    private void initLayout() {

        this.setWidth(100.0f, Unit.PERCENTAGE);
        this.setHeight(53.0f, Unit.PIXELS);
        this.setMargin(new MarginInfo(false, true, false, false));

        //Styles setzen
    }

    private void initContent() {
        //Component fuer NotificationBar
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        addComponent(horizontalLayout);

        Component configButtonComponent = initConfigButtonComponent();


        //Component fuer Logout
        /*
        Component logoutComponent = initLogoutButton();
        addComponent(logoutComponent);

        setComponentAlignment(logoutComponent, Alignment.MIDDLE_RIGHT);

        setExpandRatio(logoutComponent, 1.0f);
        */

        addComponent(configButtonComponent);

        setComponentAlignment(configButtonComponent, Alignment.MIDDLE_RIGHT);

        setExpandRatio(configButtonComponent, 1.0f);
    }

    private Component initConfigButtonComponent() {
        NativeButton einstellungenButton = new NativeButton();
        einstellungenButton.setIcon(VaadinIcons.COGS);
        //einstellungenButton.addStyleName(CryptoStyles.DIM_STATUS_ICON);
        einstellungenButton.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);
        einstellungenButton.addStyleName(CryptoStyles.STANDARD_STATUS_ICON);
        einstellungenButton.addStyleName(CryptoStyles.ONLY_ICON_BUTTON);
        einstellungenButton.addClickListener((Button.ClickListener) clickEvent -> {
            ConfigBearbeitenWindow configBearbeitenWindow = new ConfigBearbeitenWindow();
            UI.getCurrent().addWindow(configBearbeitenWindow.getWindow());
        });

        return einstellungenButton;
    }

    /*
    private Component initLogoutButton() {
        VerticalLayout verticalLayout = new VerticalLayout();

        //UserInfo userInfo = BeanProvider.getContextualReference(UserInfo.class);

        NativeButton logoutButton = new NativeButton();
        logoutButton.setIcon(VaadinIcons.POWER_OFF);
        logoutButton.addStyleName(BaseStyles.DIM_STATUS_ICON);
        logoutButton.addStyleName(BaseStyles.XLARGE_STATUS_ICON);
        logoutButton.setHeight(100f, Unit.PERCENTAGE);

        logoutButton.addClickListener((Button.ClickListener) event -> {
            LogoutWindow logoutWindow = new LogoutWindow(userInfo.getUsername());
            UI.getCurrent().addWindow(logoutWindow);
        });
        logoutButton.setDescription(userInfo.getName() + " abmelden");

        verticalLayout.addComponent(logoutButton);
        verticalLayout.setMargin(false);

        return logoutButton;
    }
    */
}
