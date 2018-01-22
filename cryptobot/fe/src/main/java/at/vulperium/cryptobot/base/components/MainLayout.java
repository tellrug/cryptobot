package at.vulperium.cryptobot.base.components;

import at.vulperium.cryptobot.mainbar.BottomBarComponent;
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
    private final BottomBarComponent bottomBarComponent;

    public MainLayout(MenuBarComponent menuBarComponent, TopBarComponent topBarComponent, BottomBarComponent bottomBarComponent) {
        this.menuBarComponent = menuBarComponent;
        this.topBarComponent = topBarComponent;
        this.bottomBarComponent = bottomBarComponent;

        initMainLayout();
        initMainComponents(menuBarComponent, topBarComponent, bottomBarComponent);
    }

    private void initMainLayout() {
        setSizeFull();
        setMargin(false);
        setSpacing(false);

        //Styles setzen
    }

    private void initMainComponents(MenuBarComponent menuBarComponent, TopBarComponent topBarComponent, BottomBarComponent bottomBarComponent) {

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

        //Bottom-Bar
        mainLayout.addComponent(bottomBarComponent);

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

    public BottomBarComponent getBottomBarComponent() {
        return bottomBarComponent;
    }
}
