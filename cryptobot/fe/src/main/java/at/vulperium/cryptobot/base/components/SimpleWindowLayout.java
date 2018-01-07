package at.vulperium.cryptobot.base.components;


import at.vulperium.cryptobot.util.CryptoStyles;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by 02ub0400 on 17.08.2017.
 */
public class SimpleWindowLayout extends AbstractWindowLayout {

    private static final long serialVersionUID = 7258701270145653472L;

    private VerticalLayout mainLayout;
    private VerticalLayout headerLayout;
    private VerticalLayout contentLayout;
    private VerticalLayout bottomLayout;

    private Button button1;
    private Button button2;


    public SimpleWindowLayout(String bezeichnung, Resource icon) {
        initSimpleWindowLayout(bezeichnung, icon, null, null);
    }

    public SimpleWindowLayout(String bezeichnung, Resource icon, String button1Caption) {
        initSimpleWindowLayout(bezeichnung, icon, button1Caption, null);
    }

    public SimpleWindowLayout(String bezeichnung, Resource icon, String button1Caption, String button2Caption) {
        initSimpleWindowLayout(bezeichnung, icon, button1Caption, button2Caption);
    }


    private void initSimpleWindowLayout(String bezeichnung, Resource icon, String button1Caption, String button2Caption) {
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        //HeaderLayout
        initHeaderLayout(bezeichnung, icon);

        //ContentLayout
        initContentLayout();

        //ButtonLayout
        initButtonLayout(button1Caption, button2Caption);

        mainLayout.addComponent(headerLayout);
        mainLayout.setComponentAlignment(headerLayout, Alignment.TOP_CENTER);
        mainLayout.addComponent(contentLayout);
        mainLayout.addComponent(bottomLayout);

        //mainLayout.setExpandRatio(headerLayout, 0.1f);
        mainLayout.setExpandRatio(contentLayout, 1f);
        //mainLayout.setExpandRatio(bottomLayout, 0.15f);
    }

    private void initHeaderLayout(String bezeichnung, Resource icon) {
        headerLayout = new VerticalLayout();
        headerLayout.setSpacing(false);
        headerLayout.setMargin(false);
        headerLayout.setWidth(100f, Sizeable.Unit.PERCENTAGE);

        //Feste Hoehe da immer gleich
        headerLayout.setHeight(30, Sizeable.Unit.PIXELS);

        //Bezeichnung
        HorizontalLayout bezeichnungLayout = new HorizontalLayout();
        bezeichnungLayout.setWidth(100f, Sizeable.Unit.PERCENTAGE);

        Label bezeichnungLabel = new Label(bezeichnung);
        bezeichnungLabel.setStyleName(CryptoStyles.SUB_CAPTION);

        //Icons
        Label iconLabel = new Label();
        if (icon != null) {
            iconLabel.setIcon(icon);
            iconLabel.setHeight(100f, Sizeable.Unit.PERCENTAGE);
        }

        bezeichnungLayout.addComponentsAndExpand(bezeichnungLabel);
        bezeichnungLayout.addComponent(iconLabel);

        //Horizontale Trennlinie
        Label trennlinieHorizontal = ComponentProducer.erstelleTrennlinieHorizontal();

        //HeaderLayout
        headerLayout.addComponent(bezeichnungLayout);
        headerLayout.addComponent(trennlinieHorizontal);

        headerLayout.setExpandRatio(bezeichnungLayout, 0.9f);
        headerLayout.setExpandRatio(trennlinieHorizontal, 0.1f);
    }

    private void initContentLayout() {
        contentLayout = new VerticalLayout();
        contentLayout.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        contentLayout.setSpacing(false);
        contentLayout.setMargin(false);
    }

    private void initButtonLayout(String button1Caption, String button2Caption) {
        bottomLayout = new VerticalLayout();
        bottomLayout.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        bottomLayout.setHeight(40, Sizeable.Unit.PIXELS);
        bottomLayout.setSpacing(false);
        bottomLayout.setMargin(false);

        if (button1Caption != null || button2Caption != null) {
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setHeight(100f, Sizeable.Unit.PERCENTAGE);
            buttonLayout.setSpacing(true);
            //buttonLayout.setMargin(new MarginInfo(false, false, true, false));
            if (button1Caption != null) {
                button1 = new Button(button1Caption);
                buttonLayout.addComponent(button1);
                buttonLayout.setComponentAlignment(button1, Alignment.MIDDLE_CENTER);
            }
            if (button2Caption != null) {
                button2 = new Button(button2Caption);
                buttonLayout.addComponent(button2);
                buttonLayout.setComponentAlignment(button2, Alignment.MIDDLE_CENTER);
            }

            bottomLayout.addComponent(buttonLayout);
            bottomLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
        }
    }

    @Override
    public Layout getBottomLayout() {
        return bottomLayout;
    }

    @Override
    public Layout getWindowLayout() {
        return mainLayout;
    }

    @Override
    public Layout getHeaderLayout() {
        return headerLayout;
    }

    @Override
    public Layout getContentLayout() {
        return contentLayout;
    }

    public void addClickListenerToButton1(Button.ClickListener clickListener) {
        if (button1 == null) {
            //Fehler
            throw new IllegalStateException("Es gibt keinen Button1!");
        }

        button1.addClickListener(clickListener);
    }

    public void addClickListenerToButton2(Button.ClickListener clickListener) {
        if (button2 == null) {
            //Fehler
            throw new IllegalStateException("Es gibt keinen Button2!");
        }

        button2.addClickListener(clickListener);
    }
}
