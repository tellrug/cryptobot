package at.vulperium.cryptobot.tradejobs;

import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.messagebundles.UtilityMessages;
import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.base.components.SimpleWindowLayout;
import at.vulperium.cryptobot.utils.TradeUtil;
import at.vulperium.cryptobot.validators.BigDecimalValidator;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 02ub0400 on 04.01.2018.
 */
public class TradeJobBearbeitenWindow implements Serializable {

    private UtilityMessages utilityMessages = BeanProvider.getContextualReference(UtilityMessages.class);

    private Window window = null;
    private SimpleWindowLayout simpleWinLayout;

    private Label labelAktuellerWertEinheit;
    private Label labelMengeEinheit;
    private Label labelZielEinheit;
    private TextField tfAktuellerWert;

    private TradeJobVO tradeJobVO;
    private Binder<SimpelTradeJobDTO> requestBinder = new Binder<>();

    private final boolean bearbeitungsModus;

    public TradeJobBearbeitenWindow() {
        this.bearbeitungsModus = false;
        this.tradeJobVO = new TradeJobVO(new SimpelTradeJobDTO());

        tradeJobVO.getSimpelTradeJobDTO().setTradeStatus(TradeStatus.ERSTELLT);
        initWindow();
    }

    public TradeJobBearbeitenWindow(TradeJobVO tradeJobVO) {
        this.tradeJobVO = tradeJobVO;

        if (tradeJobVO == null || tradeJobVO.getSimpelTradeJobDTO() == null || tradeJobVO.getSimpelTradeJobDTO().getId() == null) {
            this.bearbeitungsModus = false;
        }
        else {
            this.bearbeitungsModus = true;
        }

        initWindow();
    }

    private void initWindow() {
        window = ComponentProducer.erstelleSimplesWindow(45f, 30f);
        window.setContent(initLayout());
    }

    private Component initLayout() {
        simpleWinLayout = new SimpleWindowLayout(ermittleTitel(), VaadinIcons.PENCIL, utilityMessages.speichern(), utilityMessages.abbrechen());

        Layout neuerRequestLayout = initNeuerRequestLayout();

        //Hinzufuegen des Inhalts
        simpleWinLayout.getContentLayout().setSizeFull();
        simpleWinLayout.getContentLayout().addComponent(neuerRequestLayout);

        simpleWinLayout.addClickListenerToButton2((Button.ClickListener) event -> closeWindow());
        return simpleWinLayout.getWindowLayout();
    }

    private Layout initNeuerRequestLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true, false, false, false));

        GridLayout neuerRequestLayout = new GridLayout(2, 6);
        neuerRequestLayout.setSpacing(true);
        neuerRequestLayout.setSizeFull();
        neuerRequestLayout.setMargin(false);

        requestBinder.setBean(tradeJobVO.getSimpelTradeJobDTO());

        //SYMBOL
        Label labelSymbol = new Label(VaadinIcons.COINS.getHtml() + " <strong>Symbol</strong>");
        labelSymbol.setContentMode(ContentMode.HTML);

        HorizontalLayout symbolLayout = initSymbolLayout();
        neuerRequestLayout.addComponent(labelSymbol, 0, 0);
        neuerRequestLayout.addComponent(symbolLayout, 1, 0);

        neuerRequestLayout.setComponentAlignment(labelSymbol, Alignment.MIDDLE_LEFT);

        //AKTUELLER WERT
        Label labelAktuellerWert = new Label(VaadinIcons.DOLLAR.getHtml() + "  <strong>Aktueller Wert</strong>");
        labelAktuellerWert.setContentMode(ContentMode.HTML);

        HorizontalLayout aktuellerWertLayout = initAktuellerWertLayout();
        neuerRequestLayout.addComponent(labelAktuellerWert, 0, 1);
        neuerRequestLayout.addComponent(aktuellerWertLayout, 1, 1);

        neuerRequestLayout.setComponentAlignment(labelAktuellerWert, Alignment.MIDDLE_LEFT);

        //ZIELWERT
        Label labelZiel = new Label(VaadinIcons.FLAG_CHECKERED.getHtml() + " <strong>Ziel</strong>");
        labelZiel.setContentMode(ContentMode.HTML);

        HorizontalLayout zielLayout = initZielLayout();
        neuerRequestLayout.addComponent(labelZiel, 0, 2);
        neuerRequestLayout.addComponent(zielLayout, 1, 2);

        neuerRequestLayout.setComponentAlignment(labelZiel, Alignment.MIDDLE_LEFT);


        //MENGE
        Label labelMenge = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Menge</strong>");
        labelMenge.setContentMode(ContentMode.HTML);

        HorizontalLayout mengeLayout = initMengeLayout();
        neuerRequestLayout.addComponent(labelMenge, 0, 3);
        neuerRequestLayout.addComponent(mengeLayout, 1, 3);

        neuerRequestLayout.setComponentAlignment(labelMenge, Alignment.MIDDLE_LEFT);


        //JOBSTATUS
        Label labelJobStatus = new Label(VaadinIcons.CLIPBOARD_PULSE.getHtml() + " <strong>Job-Status</strong>");
        labelJobStatus.setContentMode(ContentMode.HTML);

        ComboBox<TradeAktionEnum> tradeJobStatusCombobox = new ComboBox<>();
        tradeJobStatusCombobox.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tradeJobStatusCombobox.setEmptySelectionAllowed(false);

        //setzen der richtigen Items
        tradeJobStatusCombobox.setItems(ermittleRelevanteTradeJobStatusList());
        requestBinder.forField(tradeJobStatusCombobox).bind(SimpelTradeJobDTO::getTradeAktionEnum, SimpelTradeJobDTO::setTradeAktionEnum);


        neuerRequestLayout.addComponent(labelJobStatus, 0, 4);
        neuerRequestLayout.addComponent(tradeJobStatusCombobox, 1, 4);

        neuerRequestLayout.setComponentAlignment(labelJobStatus, Alignment.MIDDLE_LEFT);

        //PLATTFORM
        Label labelPlattform = new Label(VaadinIcons.GLOBE_WIRE.getHtml() + " <strong>Plattform</strong>");
        labelPlattform.setContentMode(ContentMode.HTML);

        ComboBox<TradingPlattform> plattformCombobox = new ComboBox<>();
        plattformCombobox.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        plattformCombobox.setEmptySelectionAllowed(false);

        //setzen der richtigen Items
        plattformCombobox.setItems(ermittleRelevanteTradingPlattformList());
        requestBinder.forField(plattformCombobox).bind(SimpelTradeJobDTO::getTradingPlattform, SimpelTradeJobDTO::setTradingPlattform);


        neuerRequestLayout.addComponent(labelPlattform, 0, 5);
        neuerRequestLayout.addComponent(plattformCombobox, 1, 5);

        neuerRequestLayout.setComponentAlignment(labelPlattform, Alignment.MIDDLE_LEFT);

        neuerRequestLayout.setColumnExpandRatio(0, 0.2f);
        neuerRequestLayout.setColumnExpandRatio(1, 0.8f);

        neuerRequestLayout.setRowExpandRatio(6, 1f);

        verticalLayout.addComponent(neuerRequestLayout);
        return verticalLayout;
    }

    private String ermittleTitel() {
        if (bearbeitungsModus) {
            return "Bearbeite Trade-Job";
        }
        else {
            return "Erstelle Trade-Job";
        }
    }

    private HorizontalLayout initAktuellerWertLayout() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidth(100, Sizeable.Unit.PERCENTAGE);

        tfAktuellerWert = new TextField();
        tfAktuellerWert.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfAktuellerWert.setPlaceholder("z.B.: '0.001'");
        tfAktuellerWert.setValueChangeMode(ValueChangeMode.BLUR);
        requestBinder.forField(tfAktuellerWert)
                .withValidator(StringUtils::isNotEmpty, "Es muss ein aktueller Wert eingegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<SimpelTradeJobDTO, String>) tradeJobDTO -> getBigDecimalAsString(tradeJobDTO.getKaufwert()),
                        (Setter<SimpelTradeJobDTO, String>) (tradeJobDTO, value) -> tradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(value)));

        labelAktuellerWertEinheit = new Label();
        labelAktuellerWertEinheit.setWidth(100, Sizeable.Unit.PERCENTAGE);

        h.addComponent(tfAktuellerWert);
        h.addComponent(labelAktuellerWertEinheit);

        h.setExpandRatio(tfAktuellerWert, 0.8f);
        h.setExpandRatio(labelAktuellerWertEinheit, 0.2f);

        h.setComponentAlignment(labelAktuellerWertEinheit, Alignment.MIDDLE_LEFT);
        return h;
    }

    private HorizontalLayout initMengeLayout() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidth(100, Sizeable.Unit.PERCENTAGE);

        TextField tfMenge = new TextField();
        tfMenge.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfMenge.setPlaceholder("z.B.: '20.5'");
        tfMenge.setValueChangeMode(ValueChangeMode.BLUR);
        requestBinder.forField(tfMenge)
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<SimpelTradeJobDTO, String>) tradeJobDTO -> getBigDecimalAsString(tradeJobDTO.getMenge()),
                        (Setter<SimpelTradeJobDTO, String>) (tradeJobDTO, value) -> tradeJobDTO.setMenge(TradeUtil.getBigDecimal(value)));

        labelMengeEinheit = new Label();
        labelMengeEinheit.setWidth(100, Sizeable.Unit.PERCENTAGE);

        h.addComponent(tfMenge);
        h.addComponent(labelMengeEinheit);

        h.setExpandRatio(tfMenge, 0.8f);
        h.setExpandRatio(labelMengeEinheit, 0.2f);

        h.setComponentAlignment(labelMengeEinheit, Alignment.MIDDLE_LEFT);
        return h;
    }

    private HorizontalLayout initSymbolLayout() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidth(100, Sizeable.Unit.PERCENTAGE);

        TextField tfSymbol = new TextField();
        tfSymbol.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfSymbol.setPlaceholder("z.B.: 'XVG'");
        tfSymbol.setDescription("Ziel-W\u00e4hrung");
        requestBinder.forField(tfSymbol)
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Symbol angegeben werden.")
                .bind((ValueProvider<SimpelTradeJobDTO, String>) SimpelTradeJobDTO::getCryptoWaehrung, (Setter<SimpelTradeJobDTO, String>) SimpelTradeJobDTO::setCryptoWaehrung);
        tfSymbol.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            String value = valueChangeEvent.getValue();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                value = "-";
            }

            //setzen in relevante Felder
            labelMengeEinheit.setValue(value);
        });

        Label labelSymbolAustausch = new Label();
        labelSymbolAustausch.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        labelSymbolAustausch.setContentMode(ContentMode.HTML);
        labelSymbolAustausch.setValue(VaadinIcons.MONEY_EXCHANGE.getHtml());
        labelSymbolAustausch.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);
        labelSymbolAustausch.addStyleName("v-align-center");

        TextField tfReferenzSymbol = new TextField();
        tfReferenzSymbol.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfReferenzSymbol.setPlaceholder("z.B.: 'BTC'");
        tfReferenzSymbol.setDescription("Referenz-W\u00e4hrung");
        requestBinder.forField(tfReferenzSymbol)
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Referenz-Symbol angegeben werden.")
                .bind((ValueProvider<SimpelTradeJobDTO, String>) SimpelTradeJobDTO::getCryptoWaehrungReferenz, (Setter<SimpelTradeJobDTO, String>) SimpelTradeJobDTO::setCryptoWaehrungReferenz);
        tfReferenzSymbol.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            String value = valueChangeEvent.getValue();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                value = "-";
            }

            //setzen in relevante Felder
            labelAktuellerWertEinheit.setValue(value);
            labelZielEinheit.setValue(value);
        });

        h.addComponent(tfSymbol);
        h.addComponent(labelSymbolAustausch);
        h.addComponent(tfReferenzSymbol);

        h.setExpandRatio(tfSymbol, 0.5f);
        h.setExpandRatio(labelSymbolAustausch, 0.2f);
        h.setExpandRatio(tfReferenzSymbol, 0.5f);

        return h;
    }

    private HorizontalLayout initZielLayout() {
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
        tfRelativesZiel.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                if (StringUtils.isEmpty(tfAktuellerWert.getValue())) {
                    return;
                }

                //wert auslesen und absoluten Zielwert berechnen
                BigDecimal absolutesZiel = TradeUtil.getBigDecimal(tfAktuellerWert.getValue()).multiply(TradeUtil.getBigDecimal(valueChangeEvent.getValue()));
                if (!absolutesZiel.toString().equals(tfAbsolutesZiel.getValue())) {
                    tfAbsolutesZiel.setValue(absolutesZiel.toString());
                }
            }
        });


        tfAbsolutesZiel.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tfAbsolutesZiel.setPlaceholder("z.B.: '0.0005'");
        tfAbsolutesZiel.setValueChangeMode(ValueChangeMode.BLUR);
        requestBinder.forField(tfAbsolutesZiel)
                .withValidator(StringUtils::isNotEmpty, "Es muss ein Zielwert angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<SimpelTradeJobDTO, String>) tradeJobDTO -> getBigDecimalAsString(tradeJobDTO.getZielwert()),
                        (Setter<SimpelTradeJobDTO, String>) (tradeJobDTO, value) -> tradeJobDTO.setZielwert(TradeUtil.getBigDecimal(value)));

        labelZielEinheit = new Label();
        labelZielEinheit.setWidth(100, Sizeable.Unit.PERCENTAGE);

        h.addComponent(tfRelativesZiel);
        h.addComponent(tfAbsolutesZiel);
        h.addComponent(labelZielEinheit);

        h.setComponentAlignment(labelZielEinheit, Alignment.MIDDLE_LEFT);

        h.setExpandRatio(tfRelativesZiel, 0.5f);
        h.setExpandRatio(tfAbsolutesZiel, 0.5f);
        h.setExpandRatio(labelZielEinheit, 0.2f);
        return h;
    }

    private List<TradeAktionEnum> ermittleRelevanteTradeJobStatusList() {
        List<TradeAktionEnum> tradeAktionEnumList = new ArrayList<>();
        if (bearbeitungsModus) {
            TradeTyp tradeTyp = tradeJobVO.getSimpelTradeJobDTO().getTradeAktionEnum().getTradeTyp();
            tradeAktionEnumList.addAll(TradeAktionEnum.getByTradeTyp(tradeTyp));
        }
        else {
            tradeAktionEnumList.add(TradeAktionEnum.KAUF_ZIEL);
            tradeAktionEnumList.add(TradeAktionEnum.VERKAUF_ZIEL);
        }
        return tradeAktionEnumList;
    }

    private List<TradingPlattform> ermittleRelevanteTradingPlattformList() {
        List<TradingPlattform> tradingPlattformList = new ArrayList<>();
        for (TradingPlattform tradingPlattform : TradingPlattform.values()) {
            if (tradingPlattform != TradingPlattform.ALLE) {
                tradingPlattformList.add(tradingPlattform);
            }
        }
        return tradingPlattformList;
    }

    private String getBigDecimalAsString(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public Window getWindow() {
        return window;
    }

    public void closeWindow() {
        if (window != null) {
            window.close();
        }
    }

    public void addAbschlussClickListener(Button.ClickListener buttonClickListener) {
        simpleWinLayout.addClickListenerToButton1(buttonClickListener);
    }

    public TradeJobVO holeVollstaendigeEingaben() {
        if (!istEingabeVollstaendig()) {
            return null;
        }
        requestBinder.writeBeanIfValid(tradeJobVO.getSimpelTradeJobDTO());
        return tradeJobVO;
    }

    public boolean istEingabeVollstaendig() {
        //Ueberpruefen ob restliche Daten richtig eingegeben wurden
        BinderValidationStatus requestValidationStatus = requestBinder.validate();
        return requestValidationStatus.isOk();
    }

}
