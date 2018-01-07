package at.vulperium.cryptobot.base.components;

import at.vulperium.cryptobot.util.CryptoStyles;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class ComponentProducer {

    public static Label erstelleTrennlinieHorizontal() {
        Label trennlinieHorizontal = new Label();
        trennlinieHorizontal.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        trennlinieHorizontal.setStyleName(CryptoStyles.TRENNLINIE_HORIZONTAL);
        return trennlinieHorizontal;
    }

    public static Label erstelleTrennlinieVertikal() {
        Label trennlinieVertikal = new Label();
        trennlinieVertikal.setHeight(100f, Sizeable.Unit.PERCENTAGE);
        trennlinieVertikal.setStyleName(CryptoStyles.TRENNLINIE_VERTIKAL);
        return trennlinieVertikal;
    }

    public static Window erstelleSimplesWindow(float heightPerc, float widthPerc) {
        Window window = new Window();
        window.setHeight(heightPerc, Sizeable.Unit.PERCENTAGE);
        window.setWidth(widthPerc, Sizeable.Unit.PERCENTAGE);
        window.setClosable(false);
        window.setModal(true);
        window.setDraggable(false);
        window.setResizable(false);
        window.setStyleName(CryptoStyles.WINDOW_CAPTION);
        return window;
    }

    public static Label erstelleLabelHtml(String text) {
        Label label = erstelleLabel(text);
        label.setContentMode(ContentMode.HTML);
        return label;
    }

    public static Label erstelleLabel(String text) {
        return new Label(text);
    }
}
