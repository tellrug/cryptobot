package at.vulperium.cryptobot.mainbar;


import at.vulperium.cryptobot.base.MenuStyles;
import at.vulperium.cryptobot.mainbar.badge.BadgeInfo;
import at.vulperium.cryptobot.mainbar.badge.BadgeInfoComponent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 02ub0400 on 21.09.2017.
 */
public class MenuBarComponent extends CustomComponent {

    private static final long serialVersionUID = -5642549799619420270L;

    public static final String ID = "menu-bar";
    public static final String NOTIFICATION_BADGE_ID = "menu-notifications-badge-";

    private MenuBarItem selectedMenuBarItem = null;
    private Map<MenuBarItem, MenuItemButton> menuItemButtonMap;

    public MenuBarComponent() {
    }

    public void postConstruct() {
        setPrimaryStyleName("valo-menu");
        setId(ID);
        setSizeUndefined();

        setCompositionRoot(buildContent());
    }

    private Component buildContent() {

        CssLayout menuContent = new CssLayout();

        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName(ValoTheme.MENU_PART_LARGE_ICONS);
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        //Hinzufuegen der Components im Menu
        menuContent.addComponent(buildTitelLayout());
        //TODO einbauen wenn Login moeglich
        //menuContent.addComponent(buildUserMenu());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }

    private Component buildTitelLayout() {
        Label titelLabel = new Label(VaadinIcons.MONEY_EXCHANGE.getHtml() + "<strong>CryptoBot</strong>", ContentMode.HTML);
        titelLabel.setSizeUndefined();

        HorizontalLayout wrapperLayout = new HorizontalLayout();
        wrapperLayout.addComponent(titelLabel);
        wrapperLayout.setComponentAlignment(titelLabel, Alignment.MIDDLE_CENTER);
        wrapperLayout.addStyleName("valo-menu-title");
        wrapperLayout.setSpacing(false);

        return wrapperLayout;
    }

    /*
    private Component buildUserMenu() {
        MenuBar userSettings = new MenuBar();
        userSettings.addStyleName("user-menu");
        //userSettings.addStyleName(BaseStyles.XXLARGE_STATUS_ICON);


        //TODO richtiges Icon einbinden - Groesse beruecksichtigen
        MenuBar.MenuItem userSettingsItem = userSettings.addItem("", VaadinIcons.USER, null);

        //Eigenes Profil bearbeiten
        if (userInfo.getBerechtigungen().contains(BerechtigungEnum.PROFIL_BEARBEITEN)) {
            userSettingsItem.addItem("Profil bearbeiten", VaadinIcons.PENCIL, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {

                }
            });
        }

        //Setzen des Usernamens
        userSettingsItem.setText(userInfo.getName());

        //USER-NEUANLAGE
        if (userInfo.getBerechtigungen().contains(BerechtigungEnum.USER_NEUANLAGE)) {
            userSettingsItem.addItem("User-Neuanlage", VaadinIcons.USER_STAR, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    BenutzerAnlageWindow userAnlageWindow = new BenutzerAnlageWindow();
                    UI.getCurrent().addWindow(userAnlageWindow.getWindow());
                }
            });
        }

        //USER_BEARBEITUNG
        if (userInfo.getBerechtigungen().contains(BerechtigungEnum.USER_BEARBEITUNG)) {
            userSettingsItem.addItem("User bearbeiten", VaadinIcons.USERS, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    BenutzerBearbeitungComponent userBearbeitungWindow = new BenutzerBearbeitungComponent();
                    UI.getCurrent().addWindow(userBearbeitungWindow.getWindow());
                }
            });
        }

        userSettingsItem.addSeparator();
        userSettingsItem.addItem("Logout", VaadinIcons.POWER_OFF, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                LogoutWindow logoutWindow = new LogoutWindow(userInfo.getUsername());
                UI.getCurrent().addWindow(logoutWindow);
            }
        });

        return userSettings;
    }
    */

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");

        menuItemButtonMap = new HashMap<>();

        for (MenuBarItem menuBarItem : MenuBarItem.values()) {

            /* TODO
            if (!checkBerechtigung(menuBarItem)) {
                //Keine Berechtigung!
                continue;
            }
            */

            //Erstellen des menuItemButtons mit BAdge falls notwendig
            MenuItemButton menuItemButton = new MenuItemButton(menuBarItem);
            menuItemButtonMap.put(menuBarItem, menuItemButton);

            Component menuItemComponent = buildBadgeWrapper(menuItemButton);
            menuItemsLayout.addComponent(menuItemComponent);
        }

        return menuItemsLayout;
    }

    private Component buildBadgeWrapper(final MenuItemButton menuItemComponent) {
        CssLayout badgeWrapper = new CssLayout(menuItemComponent);
        badgeWrapper.addStyleName("badgewrapper");
        badgeWrapper.addStyleName(ValoTheme.MENU_ITEM);

        //Badge
        Component badgeLabel = menuItemComponent.getNotificationsBadge();
        if (badgeLabel != null) {
            badgeWrapper.addComponent(badgeLabel);
        }

        return badgeWrapper;
    }

    /*
    private boolean checkBerechtigung(MenuBarItem menuBarItem) {
        return userInfo.getBerechtigungen().contains(menuBarItem.getBerechtigung());
    }
    */


    public class MenuItemButton extends Button implements BadgeInfoComponent {

        private static final long serialVersionUID = -9090923679961536248L;

        private static final String STYLE_SELECTED = "selected";

        private Label notificationsBadge;
        private BadgeInfo badgeInfo = null;

        public MenuItemButton(MenuBarItem menuBarItem) {
            setPrimaryStyleName("valo-menu-item");
            setIcon(menuBarItem.getIcon());
            setCaption(menuBarItem.getBezeichnung());
            addClickListener(event -> {
                menuItemButtonMap.get(selectedMenuBarItem).removeStyleName(MenuStyles.SELECTED_MENU);
                selectedMenuBarItem = menuBarItem;
                addStyleName(MenuStyles.SELECTED_MENU);
                Notification.show("View wird geladen: " + menuBarItem.getViewId());

                //Navigation anstossen
                if (menuBarItem.getViewId() != null) {
                    UI.getCurrent().getNavigator().navigateTo(menuBarItem.getViewId());
                }
            });

            if (menuBarItem == MenuBarItem.DASHBOARD) {
                addStyleName(MenuStyles.SELECTED_MENU);
                selectedMenuBarItem = menuBarItem;
            }

            initBadgeLabel(menuBarItem);
            setBadgeVisible();


        }


        private void initBadgeLabel(MenuBarItem menuBarItem) {
            /*
            if (menuBarItem.getBadgeInfo() != null) {
                badgeInfo = menuBarItem.getBadgeInfo();

                notificationsBadge = ComponentProducer.erstelleLabel("");
                notificationsBadge.setId(NOTIFICATION_BADGE_ID + menuBarItem.getBezeichnung().toLowerCase());
                notificationsBadge.addStyleName(ValoTheme.MENU_BADGE);
                notificationsBadge.setWidthUndefined();

                BadgeInfoComponentManager.register(this);
            }
            */
        }

        public Label getNotificationsBadge() {
            return this.notificationsBadge;
        }

        public void setBadgeNumber(int badgeNumber) {
            setBadgeNumber(String.valueOf(badgeNumber));
        }

        public void setBadgeNumber(String badgeText) {
            if (notificationsBadge != null) {
                notificationsBadge.setValue(badgeText);
            }
            setBadgeVisible();
        }

        private void setBadgeVisible() {
            if (notificationsBadge != null) {
                if (StringUtils.isEmpty(notificationsBadge.getValue()) || notificationsBadge.getValue().equals(String.valueOf(0))) {
                    notificationsBadge.setVisible(false);
                }
                else {
                    notificationsBadge.setVisible(true);
                }
            }
        }

        /*
        @Override
        public void aktualisiereBadgeInfoInComponent(BadgeInfoVO badgeInfoVO) {
            setBadgeNumber(badgeInfoVO.getInfoText());
            for (String styles : badgeInfoVO.getStyles()) {
                notificationsBadge.addStyleName(styles);
            }

        }
        */

        @Override
        public BadgeInfo getBadgeInfo() {
            return badgeInfo;
        }
    }
}
