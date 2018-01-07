package at.vulperium.cryptobot.base;


import at.vulperium.cryptobot.base.navigator.BaseViewChangeListener;
import at.vulperium.cryptobot.mainbar.MenuBarComponent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 02ub0400 on 22.09.2017.
 */
public abstract class AbstractViewController implements ViewController, BaseViewChangeListener {

    private static final long serialVersionUID = -7912981376478715895L;
    private static final Logger logger = LoggerFactory.getLogger(AbstractViewController.class);

    //TODO falls mal das menu benoetigt wird...
    private MenuBarComponent menuBarComponent = BeanProvider.getContextualReference(MenuBarComponent.class);

    //protected @Inject UserInfo userInfo;
    protected boolean onFirstEnter = true;

    private Navigator navigator;

    /** daweil ein allgemeines Vertical Layout. Wenn notwendig kann ein konkretes Layout implementiert werden **/
    private VerticalLayout layout = new VerticalLayout();

    @Override
    public void createViewLayout() {
        //Initialisierung von allgemeinen LayoutKomponenten
        layout.removeAllComponents();
        layout.setSizeFull();
        layout.addComponent(createCustomLayout());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        logger.info("Enter View: {}", viewChangeEvent.getViewName());
        createViewLayout();

        navigator = viewChangeEvent.getNavigator();
    }

    @Override
    public Component getUIComponent() {
        return layout;
    }

    @Override
    public Component getViewComponent() {
        return layout;
    }

    @Override
    public boolean viewLoaded() {
        return !onFirstEnter;
    }

    @Override
    public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        //Hier werden jene Elemente geladen die fuer allgemein fuer die Views benoetigt werden

        //Hier werden jene Elemente geladen die spezifisch fuer eine View benoetigt werden
        ladeViewDaten(viewChangeEvent);
        return true;
    }

    @Override
    public void afterViewChange(final ViewChangeListener.ViewChangeEvent event) {
        //TODO hier ist was zu tun
    }

    /** Hier werden nur die notwendigen Daten geladen; keine Layouts erzeugt **/
    protected abstract void ladeViewDaten(ViewChangeListener.ViewChangeEvent viewChangeEvent);

    /** Erstellen des Layouts fuer die View **/
    protected abstract Component createCustomLayout();

    /** Gibt den Titel der Maske zurueck **/
    protected abstract String getPageCaption();

    /** Liefert die Id der View zurueck **/
    protected abstract String getIDKey();

    protected void navigateTo(String viewId) {
        navigator.navigateTo(viewId);
    }
}
