package at.vulperium.cryptobot.base.components;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.util.CryptoStyles;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by 02ub0400 on 20.02.2018.
 */
public abstract class AbstractTradeJobComponent {

    private VerticalLayout headerLayout = new VerticalLayout();
    private Binder<AbstractTradeJobDTO> requestBinder = new Binder<>();
    private AbstractTradeJobDTO abstractTradeJobDTO;

    private TextField tfSymbol;
    private TextField tfRefSymbol;

    public Layout getHeaderLayout() {
        return headerLayout;
    }

    public void initRequestHeaderLayout(AbstractTradeJobDTO abstractTradeJobDTO, boolean bearbeitungsModus) {
        this.abstractTradeJobDTO = abstractTradeJobDTO;

        headerLayout.setSpacing(true);
        headerLayout.setMargin(false);

        //Inhalt dder Componente
        Layout contentLayout = initContentLayout(bearbeitungsModus);

        //Trennlinie
        Label trennlinie = ComponentProducer.erstelleTrennlinieHorizontal();
        trennlinie.setSizeFull();

        headerLayout.addComponent(contentLayout);
        headerLayout.addComponent(trennlinie);

        headerLayout.setExpandRatio(contentLayout, 95.0f);
        headerLayout.setExpandRatio(trennlinie, 5.0f);
    }

    private Layout initContentLayout(boolean bearbeitungsModus) {
        GridLayout neuerRequestLayout = new GridLayout(2, 4);
        neuerRequestLayout.setSpacing(true);
        neuerRequestLayout.setSizeFull();
        neuerRequestLayout.setMargin(false);

        //SYMBOL
        Label labelSymbol = new Label(VaadinIcons.COINS.getHtml() + " <strong>Symbol</strong>");
        labelSymbol.setContentMode(ContentMode.HTML);

        HorizontalLayout symbolLayout = initSymbolLayout();
        neuerRequestLayout.addComponent(labelSymbol, 0, 0);
        neuerRequestLayout.addComponent(symbolLayout, 1, 0);

        neuerRequestLayout.setComponentAlignment(labelSymbol, Alignment.MIDDLE_LEFT);

        //ART der TradeAktion
        Label labelJobStatus = new Label(VaadinIcons.CLIPBOARD_PULSE.getHtml() + " <strong>Job-Art</strong>");
        labelJobStatus.setContentMode(ContentMode.HTML);

        ComboBox<TradeAktionEnum> tradeJobStatusCombobox = new ComboBox<>();
        tradeJobStatusCombobox.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tradeJobStatusCombobox.setEmptySelectionAllowed(false);

        tradeJobStatusCombobox.addValueChangeListener((HasValue.ValueChangeListener<TradeAktionEnum>) valueChangeEvent -> {
            //Job hat sich geaendert
            jobChanged(valueChangeEvent.getValue());
        });

        //setzen der richtigen Items
        tradeJobStatusCombobox.setItems(ermittleRelevanteTradeAktionList());
        tradeJobStatusCombobox.setItemCaptionGenerator(TradeAktionEnum::getAnzeigetext);
        requestBinder.forField(tradeJobStatusCombobox).bind(AbstractTradeJobDTO::getTradeAktionEnum, AbstractTradeJobDTO::setTradeAktionEnum);
        if (bearbeitungsModus) {
            tradeJobStatusCombobox.setSelectedItem(abstractTradeJobDTO.getTradeAktionEnum());
            tradeJobStatusCombobox.setEnabled(false);
        }


        neuerRequestLayout.addComponent(labelJobStatus, 0, 1);
        neuerRequestLayout.addComponent(tradeJobStatusCombobox, 1, 1);

        neuerRequestLayout.setComponentAlignment(labelJobStatus, Alignment.MIDDLE_LEFT);


        //PLATTFORM
        Label labelPlattform = new Label(VaadinIcons.GLOBE_WIRE.getHtml() + " <strong>Plattform</strong>");
        labelPlattform.setContentMode(ContentMode.HTML);

        ComboBox<TradingPlattform> plattformCombobox = new ComboBox<>();
        plattformCombobox.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        plattformCombobox.setEmptySelectionAllowed(false);

        //setzen der richtigen Items
        plattformCombobox.setItems(ermittleRelevanteTradingPlattformList());
        requestBinder.forField(plattformCombobox).bind(AbstractTradeJobDTO::getTradingPlattform, AbstractTradeJobDTO::setTradingPlattform);
        if (bearbeitungsModus) {
            plattformCombobox.setSelectedItem(abstractTradeJobDTO.getTradingPlattform());
            plattformCombobox.setEnabled(false);
        }

        neuerRequestLayout.addComponent(labelPlattform, 0, 2);
        neuerRequestLayout.addComponent(plattformCombobox, 1, 2);

        neuerRequestLayout.setComponentAlignment(labelPlattform, Alignment.MIDDLE_LEFT);

        neuerRequestLayout.setColumnExpandRatio(0, 0.2f);
        neuerRequestLayout.setColumnExpandRatio(1, 0.8f);

        neuerRequestLayout.setRowExpandRatio(3, 1f);

        return neuerRequestLayout;
    }

    private HorizontalLayout initSymbolLayout() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidth(100, Sizeable.Unit.PERCENTAGE);

        VerticalLayout symbolLayout = new VerticalLayout();
        symbolLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
        symbolLayout.setMargin(false);

        //SYMBOL
        tfSymbol = new TextField();
        tfSymbol.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfSymbol.setPlaceholder("z.B.: 'XVG'");
        tfSymbol.setDescription("Ziel-W\u00e4hrung");
        requestBinder.forField(tfSymbol)
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Symbol angegeben werden.")
                .bind((ValueProvider<AbstractTradeJobDTO, String>) AbstractTradeJobDTO::getCryptoWaehrung, (Setter<AbstractTradeJobDTO, String>) AbstractTradeJobDTO::setCryptoWaehrung);
        tfSymbol.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            String value = valueChangeEvent.getValue();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                value = "-";
            }

            //Symbol hat sich geaendert
            symbolChanged(value);
        });

        CheckBox checkBox = new CheckBox("ganzzahlig");
        checkBox.setWidth(100, Sizeable.Unit.PERCENTAGE);
        requestBinder.forField(checkBox)
                .bind((ValueProvider<AbstractTradeJobDTO, Boolean>) AbstractTradeJobDTO::isGanzZahlig, (Setter<AbstractTradeJobDTO, Boolean>) AbstractTradeJobDTO::setGanzZahlig);

        symbolLayout.addComponent(tfSymbol);
        symbolLayout.addComponent(checkBox);

        Label labelSymbolAustausch = new Label();
        labelSymbolAustausch.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        labelSymbolAustausch.setContentMode(ContentMode.HTML);
        labelSymbolAustausch.setValue(VaadinIcons.MONEY_EXCHANGE.getHtml());
        labelSymbolAustausch.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);
        labelSymbolAustausch.addStyleName("v-align-center");

        tfRefSymbol = new TextField();
        tfRefSymbol.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfRefSymbol.setPlaceholder("z.B.: 'BTC'");
        tfRefSymbol.setDescription("Referenz-W\u00e4hrung");
        requestBinder.forField(tfRefSymbol)
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Referenz-Symbol angegeben werden.")
                .bind((ValueProvider<AbstractTradeJobDTO, String>) AbstractTradeJobDTO::getCryptoWaehrungReferenz, (Setter<AbstractTradeJobDTO, String>) AbstractTradeJobDTO::setCryptoWaehrungReferenz);
        tfRefSymbol.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            String value = valueChangeEvent.getValue();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                value = "-";
            }

            //ReferenzSymbol hat sich geaendert
            referenzSymbolChanged(value);
        });

        h.addComponent(symbolLayout);
        h.addComponent(labelSymbolAustausch);
        h.addComponent(tfRefSymbol);

        h.setComponentAlignment(symbolLayout, Alignment.MIDDLE_CENTER);
        h.setComponentAlignment(labelSymbolAustausch, Alignment.MIDDLE_CENTER);
        h.setComponentAlignment(tfRefSymbol, Alignment.MIDDLE_CENTER);

        h.setExpandRatio(symbolLayout, 0.5f);
        h.setExpandRatio(labelSymbolAustausch, 0.2f);
        h.setExpandRatio(tfRefSymbol, 0.5f);

        return h;
    }

    public boolean istAbstracteEingabeVollstaendig() {
        //Ueberpruefen ob restliche Daten richtig eingegeben wurden
        BinderValidationStatus requestValidationStatus = requestBinder.validate();

        if (requestValidationStatus.isOk()) {
            requestBinder.writeBeanIfValid(abstractTradeJobDTO);
        }
        return requestValidationStatus.isOk();
    }

    protected abstract List<TradingPlattform> ermittleRelevanteTradingPlattformList();

    protected abstract void jobChanged(TradeAktionEnum tradeAktionEnum);

    protected abstract List<TradeAktionEnum> ermittleRelevanteTradeAktionList();

    protected abstract void symbolChanged(String value);

    protected abstract void referenzSymbolChanged(String value);

    public String getSymbolValue() {
        return getSymbolValue(tfSymbol);
    }

    public String getRefSymbolValue() {
        return getSymbolValue(tfRefSymbol);
    }

    private String getSymbolValue(TextField textField) {
        if (textField == null || StringUtils.isEmpty(textField.getValue())) {
            return "-";
        }
        return textField.getValue();
    }
}
