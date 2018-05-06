package at.vulperium.cryptobot.base.components;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.messagebundles.UtilityMessages;
import at.vulperium.cryptobot.utils.TradeUtil;
import at.vulperium.cryptobot.validators.BigDecimalValidator;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 19.02.2018.
 */
public abstract class AbstractTradejobBearbeitenWindow extends AbstractTradeJobComponent {

    protected final boolean bearbeitungsModus;
    protected WertEinheitComponent aktuellerWertComp;
    protected Label labelZielEinheit;

    private UtilityMessages utilityMessages = BeanProvider.getContextualReference(UtilityMessages.class);

    private Window window = null;
    private SimpleWindowLayout simpleWinLayout;

    private Binder<AbstractTradeJobDTO> requestBinder = new Binder<>();
    private AbstractTradeJobDTO abstractTradeJobDTO;

    private VerticalLayout contentRequestLayout;


    public AbstractTradejobBearbeitenWindow(AbstractTradeJobDTO abstractTradeJobDTO, boolean bearbeitungsModus) {
        this.bearbeitungsModus = bearbeitungsModus;
        this.abstractTradeJobDTO = abstractTradeJobDTO;
    }

    protected void initWindow() {
        window = ComponentProducer.erstelleSimplesWindow(55f, 30f);
        window.setContent(initLayout());
    }

    private Component initLayout() {
        simpleWinLayout = new SimpleWindowLayout(ermittleTitel(bearbeitungsModus), VaadinIcons.PENCIL, utilityMessages.speichern(), utilityMessages.abbrechen());

        Layout neuerRequestLayout = initNeuerRequestLayout();

        //Hinzufuegen des Inhalts
        simpleWinLayout.getContentLayout().setSizeFull();
        simpleWinLayout.getContentLayout().addComponent(neuerRequestLayout);

        simpleWinLayout.addClickListenerToButton2((Button.ClickListener) event -> closeWindow());
        return simpleWinLayout.getWindowLayout();
    }

    private Layout initNeuerRequestLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(false);

        //HeaderLayout
        initRequestHeaderLayout(abstractTradeJobDTO, bearbeitungsModus);
        Layout headerLayout = getHeaderLayout();
        verticalLayout.addComponent(headerLayout);

        //ContentLayout
        contentRequestLayout = new VerticalLayout();
        contentRequestLayout.setSizeFull();
        contentRequestLayout.setSpacing(true);
        contentRequestLayout.setMargin(false);

        verticalLayout.addComponent(contentRequestLayout);

        verticalLayout.setExpandRatio(headerLayout, 5.0f);
        verticalLayout.setExpandRatio(contentRequestLayout, 5.0f);

        return verticalLayout;
    }

    protected void initAktuellerWertLayout() {
        aktuellerWertComp = new WertEinheitComponent(getRefSymbolValue(), "z.B.: '0.001'");

        requestBinder.forField(aktuellerWertComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss ein aktueller Wert eingegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<AbstractTradeJobDTO, String>) tradeJobDTO -> getBigDecimalAsString(tradeJobDTO.getKaufwert()),
                        (Setter<AbstractTradeJobDTO, String>) (tradeJobDTO, value) -> tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(value)));
    }

    protected HorizontalLayout initZielLayout() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidth(100, Sizeable.Unit.PERCENTAGE);

        TextField tfRelativesZiel = new TextField();
        TextField tfAbsolutesZiel = new TextField();

        tfRelativesZiel.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfRelativesZiel.setPlaceholder("z.B.: '1.3'");
        tfRelativesZiel.setValueChangeMode(ValueChangeMode.BLUR);
        requestBinder.forField(tfRelativesZiel)
                .withValidator(StringUtils::isNotEmpty, "Es muss ein Zielwert angegeben werden.")
                .withValidator(new BigDecimalValidator());
        tfRelativesZiel.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            if (StringUtils.isEmpty(aktuellerWertComp.getWertTextfield().getValue())) {
                return;
            }

            //wert auslesen und absoluten Zielwert berechnen
            BigDecimal absolutesZiel = TradeUtil.getBigDecimal(aktuellerWertComp.getWertTextfield().getValue()).multiply(TradeUtil.getBigDecimal(valueChangeEvent.getValue()));
            if (!absolutesZiel.toString().equals(tfAbsolutesZiel.getValue())) {
                tfAbsolutesZiel.setValue(absolutesZiel.toString());
            }
        });


        tfAbsolutesZiel.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfAbsolutesZiel.setPlaceholder("z.B.: '0.0005'");
        tfAbsolutesZiel.setValueChangeMode(ValueChangeMode.BLUR);
        requestBinder.forField(tfAbsolutesZiel)
                .withValidator(StringUtils::isNotEmpty, "Es muss ein Zielwert angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<AbstractTradeJobDTO, String>) tradeJobDTO -> getBigDecimalAsString(tradeJobDTO.getZielwert()),
                        (Setter<AbstractTradeJobDTO, String>) (tradeJobDTO, value) -> tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(value)));

        labelZielEinheit = new Label();
        labelZielEinheit.setWidth(100, Sizeable.Unit.PERCENTAGE);
        labelZielEinheit.setValue(getRefSymbolValue());

        h.addComponent(tfRelativesZiel);
        h.addComponent(tfAbsolutesZiel);
        h.addComponent(labelZielEinheit);

        h.setComponentAlignment(labelZielEinheit, Alignment.MIDDLE_LEFT);

        h.setExpandRatio(tfRelativesZiel, 0.5f);
        h.setExpandRatio(tfAbsolutesZiel, 0.5f);
        h.setExpandRatio(labelZielEinheit, 0.2f);
        return h;
    }

    protected String getBigDecimalAsString(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public void closeWindow() {
        if (window != null) {
            window.close();
        }
    }

    public Window getWindow() {
        return window;
    }

    public void addAbschlussClickListener(Button.ClickListener buttonClickListener) {
        simpleWinLayout.addClickListenerToButton1(buttonClickListener);
    }


    public boolean istEingabeVollstaendig() {
        boolean abstractEingabeVollstaendig = super.istAbstracteEingabeVollstaendig();

        //Ueberpruefen ob restliche Daten richtig eingegeben wurden
        BinderValidationStatus requestValidationStatus = requestBinder.validate();

        boolean istEingabeVollstaendig = abstractEingabeVollstaendig && requestValidationStatus.isOk();
        if (istEingabeVollstaendig) {
            requestBinder.writeBeanIfValid(abstractTradeJobDTO);
        }
        return istEingabeVollstaendig;
    }

    @Override
    protected void jobChanged(TradeAktionEnum tradeAktionEnum) {
        //Aktualisieren der Componente
        contentRequestLayout.removeAllComponents();
        contentRequestLayout.addComponent(initContentRequestLayout(tradeAktionEnum));
    }

    protected abstract Layout initContentRequestLayout(TradeAktionEnum tradeAktionEnum);

    protected abstract void symbolChanged(String value);

    protected abstract void referenzSymbolChanged(String value);

    protected abstract String ermittleTitel(boolean bearbeitungsModus);
}
