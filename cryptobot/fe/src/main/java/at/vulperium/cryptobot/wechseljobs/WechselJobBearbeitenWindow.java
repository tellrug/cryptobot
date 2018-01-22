package at.vulperium.cryptobot.wechseljobs;

import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.base.components.SimpleWindowLayout;
import at.vulperium.cryptobot.base.components.WertEinheitComponent;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.messagebundles.UtilityMessages;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.utils.TradeUtil;
import at.vulperium.cryptobot.validators.BigDecimalValidator;
import at.vulperium.cryptobot.wechseljobs.vo.WechselTradeJobVO;
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
 * Created by Ace on 19.01.2018.
 */
public class WechselJobBearbeitenWindow implements Serializable {

    private UtilityMessages utilityMessages = BeanProvider.getContextualReference(UtilityMessages.class);

    private Window window = null;
    private SimpleWindowLayout simpleWinLayout;

    private boolean bearbeitungsModus = false;
    private WechselTradeJobVO wechselTradeJobVO;
    private Binder<WechselTradeJobDTO> requestBinder = new Binder<>();

    private WertEinheitComponent kaufgrenzwertComp;
    private WertEinheitComponent kaufmengeComp;
    private WertEinheitComponent aktuellerWertComp;
    private WertEinheitComponent zielsatzComp;

    public WechselJobBearbeitenWindow() {
        this.bearbeitungsModus = false;
        this.wechselTradeJobVO = new WechselTradeJobVO(new WechselTradeJobDTO());

        wechselTradeJobVO.getWechselTradeJobDTO().setTradeStatus(TradeStatus.ERSTELLT);
        initWindow();
    }

    public WechselJobBearbeitenWindow(WechselTradeJobVO wechselTradeJobVO) {
        this.wechselTradeJobVO = wechselTradeJobVO;

        if (wechselTradeJobVO == null || wechselTradeJobVO.getWechselTradeJobDTO() == null || wechselTradeJobVO.getWechselTradeJobDTO().getId() == null) {
            this.bearbeitungsModus = false;
        }
        else {
            this.bearbeitungsModus = true;
        }
        initWindow();
    }


    private void initWindow() {
        window = ComponentProducer.erstelleSimplesWindow(60f, 30f);
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

    private String ermittleTitel() {
        if (bearbeitungsModus) {
            return "Bearbeite Wechsel-Job";
        }
        else {
            return "Erstelle Wechsel-Job";
        }
    }

    private Layout initNeuerRequestLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true, false, false, false));

        GridLayout neuerRequestLayout = new GridLayout(2, 8);
        neuerRequestLayout.setSpacing(true);
        neuerRequestLayout.setSizeFull();
        neuerRequestLayout.setMargin(false);

        requestBinder.setBean(wechselTradeJobVO.getWechselTradeJobDTO());

        //SYMBOL
        Label labelSymbol = new Label(VaadinIcons.COINS.getHtml() + " <strong>Symbol</strong>");
        labelSymbol.setContentMode(ContentMode.HTML);

        HorizontalLayout symbolLayout = initSymbolLayout();
        neuerRequestLayout.addComponent(labelSymbol, 0, 0);
        neuerRequestLayout.addComponent(symbolLayout, 1, 0);

        neuerRequestLayout.setComponentAlignment(labelSymbol, Alignment.MIDDLE_LEFT);


        //KAUFGRENZWERT
        Label labelKaufgrenzwert = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Kauf-Grenzwert</strong>");
        labelKaufgrenzwert.setContentMode(ContentMode.HTML);

        kaufgrenzwertComp = new WertEinheitComponent("-", "z.B.: '0.00005'");
        neuerRequestLayout.addComponent(labelKaufgrenzwert, 0, 1);
        neuerRequestLayout.addComponent(kaufgrenzwertComp, 1, 1);

        requestBinder.forField(kaufgrenzwertComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getKaufwertGrenze()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setKaufwertGrenze(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelKaufgrenzwert, Alignment.MIDDLE_LEFT);

        //KAUFMENGE
        Label labelKaufmenge = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Kauf-Menge</strong>");
        labelKaufmenge.setContentMode(ContentMode.HTML);

        kaufmengeComp = new WertEinheitComponent("-", "z.B.: '0.02'");
        neuerRequestLayout.addComponent(labelKaufmenge, 0, 2);
        neuerRequestLayout.addComponent(kaufmengeComp, 1, 2);

        requestBinder.forField(kaufmengeComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getMengeReferenzwert()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setMengeReferenzwert(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelKaufmenge, Alignment.MIDDLE_LEFT);

        //AKTUELLER WERT
        Label labelAktuellerWert = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Aktueller Wert</strong>");
        labelAktuellerWert.setContentMode(ContentMode.HTML);

        aktuellerWertComp = new WertEinheitComponent("-", "z.B.: '0.0002'");
        neuerRequestLayout.addComponent(labelAktuellerWert, 0, 3);
        neuerRequestLayout.addComponent(aktuellerWertComp, 1, 3);

        requestBinder.forField(aktuellerWertComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getKaufwert()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelAktuellerWert, Alignment.MIDDLE_LEFT);

        //ZIELSATZ
        Label labelZielsatz = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Ziel-Satz</strong>");
        labelZielsatz.setContentMode(ContentMode.HTML);

        zielsatzComp = new WertEinheitComponent("-", "z.B.: '0.02'");
        neuerRequestLayout.addComponent(labelZielsatz, 0, 4);
        neuerRequestLayout.addComponent(zielsatzComp, 1, 4);

        requestBinder.forField(zielsatzComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getMinimalZielSatz()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setMinimalZielSatz(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelZielsatz, Alignment.MIDDLE_LEFT);


        //JOBSTATUS
        Label labelJobStatus = new Label(VaadinIcons.CLIPBOARD_PULSE.getHtml() + " <strong>Job-Status</strong>");
        labelJobStatus.setContentMode(ContentMode.HTML);

        ComboBox<TradeAktionEnum> tradeJobStatusCombobox = new ComboBox<>();
        tradeJobStatusCombobox.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        tradeJobStatusCombobox.setEmptySelectionAllowed(false);

        //setzen der richtigen Items
        tradeJobStatusCombobox.setItems(ermittleRelevanteTradeJobStatusList());
        requestBinder.forField(tradeJobStatusCombobox).bind(WechselTradeJobDTO::getTradeAktionEnum, WechselTradeJobDTO::setTradeAktionEnum);


        neuerRequestLayout.addComponent(labelJobStatus, 0, 5);
        neuerRequestLayout.addComponent(tradeJobStatusCombobox, 1, 5);

        neuerRequestLayout.setComponentAlignment(labelJobStatus, Alignment.MIDDLE_LEFT);

        //PLATTFORM
        Label labelPlattform = new Label(VaadinIcons.GLOBE_WIRE.getHtml() + " <strong>Plattform</strong>");
        labelPlattform.setContentMode(ContentMode.HTML);

        ComboBox<TradingPlattform> plattformCombobox = new ComboBox<>();
        plattformCombobox.setWidth(100f, Sizeable.Unit.PERCENTAGE);
        plattformCombobox.setEmptySelectionAllowed(false);
        plattformCombobox.setEnabled(!bearbeitungsModus);

        //setzen der richtigen Items
        plattformCombobox.setItems(ermittleRelevanteTradingPlattformList());
        requestBinder.forField(plattformCombobox).bind(WechselTradeJobDTO::getTradingPlattform, WechselTradeJobDTO::setTradingPlattform);


        neuerRequestLayout.addComponent(labelPlattform, 0, 6);
        neuerRequestLayout.addComponent(plattformCombobox, 1, 6);

        neuerRequestLayout.setComponentAlignment(labelPlattform, Alignment.MIDDLE_LEFT);

        neuerRequestLayout.setColumnExpandRatio(0, 0.2f);
        neuerRequestLayout.setColumnExpandRatio(1, 0.8f);

        neuerRequestLayout.setRowExpandRatio(7, 1f);

        verticalLayout.addComponent(neuerRequestLayout);
        return verticalLayout;
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
                .bind((ValueProvider<WechselTradeJobDTO, String>) WechselTradeJobDTO::getCryptoWaehrung, (Setter<WechselTradeJobDTO, String>) WechselTradeJobDTO::setCryptoWaehrung);
        tfSymbol.setEnabled(!bearbeitungsModus);

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
                .bind((ValueProvider<WechselTradeJobDTO, String>) WechselTradeJobDTO::getCryptoWaehrungReferenz, (Setter<WechselTradeJobDTO, String>) WechselTradeJobDTO::setCryptoWaehrungReferenz);
        tfReferenzSymbol.setEnabled(!bearbeitungsModus);
        tfReferenzSymbol.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            String value = valueChangeEvent.getValue();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                value = "-";
            }

            //setzen in relevante Felder
            kaufgrenzwertComp.getEinheitLabel().setValue(value);
            kaufmengeComp.getEinheitLabel().setValue(value);
            zielsatzComp.getEinheitLabel().setValue(value);
            aktuellerWertComp.getEinheitLabel().setValue(value);
        });

        h.addComponent(tfSymbol);
        h.addComponent(labelSymbolAustausch);
        h.addComponent(tfReferenzSymbol);

        h.setExpandRatio(tfSymbol, 0.5f);
        h.setExpandRatio(labelSymbolAustausch, 0.2f);
        h.setExpandRatio(tfReferenzSymbol, 0.5f);

        return h;
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

    public WechselTradeJobVO holeVollstaendigeEingaben() {
        if (!istEingabeVollstaendig()) {
            return null;
        }
        requestBinder.writeBeanIfValid(wechselTradeJobVO.getWechselTradeJobDTO());
        return wechselTradeJobVO;
    }

    public boolean istEingabeVollstaendig() {
        //Ueberpruefen ob restliche Daten richtig eingegeben wurden
        BinderValidationStatus requestValidationStatus = requestBinder.validate();
        return requestValidationStatus.isOk();
    }

    private List<TradeAktionEnum> ermittleRelevanteTradeJobStatusList() {
        List<TradeAktionEnum> tradeAktionEnumList = new ArrayList<>();
        tradeAktionEnumList.add(TradeAktionEnum.ORDER_KAUF);
        tradeAktionEnumList.add(TradeAktionEnum.ORDER_VERKAUF);
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
}
