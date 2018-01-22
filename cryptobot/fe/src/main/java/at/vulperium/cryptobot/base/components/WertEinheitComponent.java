package at.vulperium.cryptobot.base.components;


import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import java.io.Serializable;

/**
 * Created by Ace on 20.01.2018.
 */
public class WertEinheitComponent extends HorizontalLayout {

    private TextField tfWert;
    private Label labelEinheit;

    public WertEinheitComponent(String einheit, String placeholder) {
        tfWert = new TextField();
        labelEinheit = new Label();

        init(einheit, placeholder);
    }

    public TextField getWertTextfield() {
        return tfWert;
    }

    public Label getEinheitLabel() {
        return labelEinheit;
    }

    private void init(String einheit, String placeholder) {
        setWidth(100, Sizeable.Unit.PERCENTAGE);

        tfWert.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfWert.setPlaceholder(placeholder);
        tfWert.setValueChangeMode(ValueChangeMode.BLUR);

        labelEinheit.setValue(einheit);

        addComponent(tfWert);
        addComponent(labelEinheit);

        setExpandRatio(tfWert, 0.8f);
        setExpandRatio(labelEinheit, 0.2f);

        setComponentAlignment(labelEinheit, Alignment.MIDDLE_LEFT);
    }
}
