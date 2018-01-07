package at.vulperium.cryptobot.mainbar;

import at.vulperium.cryptobot.base.ViewId;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;

/**
 * Created by 02ub0400 on 24.09.2017.
 */
public enum MenuBarItem {

    DASHBOARD("Dashboard", VaadinIcons.PIE_BAR_CHART, ViewId.DASHBOARD),
    TRADE_JOBS("Jobs", VaadinIcons.CALC_BOOK, ViewId.TRADE_JOBS),
    TRADE_AKTION("Aktionen", VaadinIcons.TOUCH, ViewId.TRADE_AKTIONEN);

    private String bezeichnung;
    private Resource icon;
    private String viewId;


    MenuBarItem(String bezeichnung, Resource icon, String viewId) {
        this.bezeichnung = bezeichnung;
        this.icon = icon;
        this.viewId = viewId;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Resource getIcon() {
        return icon;
    }

    public String getViewId() {
        return viewId;
    }
}
