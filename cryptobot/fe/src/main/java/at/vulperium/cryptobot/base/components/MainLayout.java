package at.vulperium.cryptobot.base.components;

import at.vulperium.cryptobot.mainbar.MenuBarComponent;
import at.vulperium.cryptobot.mainbar.TopBarComponent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class MainLayout extends HorizontalLayout {

    private ComponentContainer content;
    private final MenuBarComponent menuBarComponent;
    private final TopBarComponent topBarComponent;

    public MainLayout(MenuBarComponent menuBarComponent, TopBarComponent topBarComponent) {
        this.menuBarComponent = menuBarComponent;
        this.topBarComponent = topBarComponent;

        initMainLayout();
        initMainComponents(menuBarComponent, topBarComponent);
    }

    private void initMainLayout() {
        setSizeFull();
        setMargin(false);
        setSpacing(false);

        //Styles setzen
    }

    private void initMainComponents(MenuBarComponent menuBarComponent, TopBarComponent topBarComponent) {

        //Menu einfuegen
        addComponent(menuBarComponent);

        //Content
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);

        //Header
        mainLayout.addComponent(topBarComponent);

        //Content
        content = new CssLayout();
        content.addStyleName("view-content");
        content.setSizeFull();


        mainLayout.addComponent(content);

        mainLayout.setExpandRatio(content, 1.0f);

        addComponent(mainLayout);
        setExpandRatio(mainLayout, 1.0f);
    }

    public ComponentContainer getContentContainer() {
        return content;
    }

    public MenuBarComponent getMenuBarComponent() {
        return menuBarComponent;
    }

    public TopBarComponent getTopBarComponent() {
        return topBarComponent;
    }
}
