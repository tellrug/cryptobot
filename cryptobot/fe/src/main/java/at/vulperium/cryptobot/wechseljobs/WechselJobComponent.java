package at.vulperium.cryptobot.wechseljobs;

import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradeaktionen.TradeAktionWindow;
import at.vulperium.cryptobot.tradeaktionen.service.TradeAktionViewService;
import at.vulperium.cryptobot.tradeaktionen.vo.TradeAktionVO;
import at.vulperium.cryptobot.tradejobs.TradeJobBearbeitenWindow;
import at.vulperium.cryptobot.tradejobs.vo.FilterVO;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.util.FilterFunktionsComponent;
import at.vulperium.cryptobot.util.Filterable;
import at.vulperium.cryptobot.util.ViewUtils;
import at.vulperium.cryptobot.wechseljobs.service.WechselTradeJobViewService;
import at.vulperium.cryptobot.wechseljobs.vo.WechselTradeJobVO;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.StringUtils;

import java.util.List;

/**
 * Created by Ace on 19.01.2018.
 */
public class WechselJobComponent extends VerticalLayout {

    private WechselTradeJobViewService wechselTradeJobViewService = BeanProvider.getContextualReference(WechselTradeJobViewService.class);
    private TradeAktionViewService tradeAktionViewService = BeanProvider.getContextualReference(TradeAktionViewService.class);

    private List<WechselTradeJobVO> wechselTradeJobVOList;
    private Grid<WechselTradeJobVO> wechselJobGrid;


    public WechselJobComponent(List<WechselTradeJobVO> wechselTradeJobVOList) {
        this.wechselTradeJobVOList = wechselTradeJobVOList;
        initTradeJobLayout();
        initContent();
    }

    private void initTradeJobLayout() {
        this.setSpacing(true);
        this.setWidth(100, Sizeable.Unit.PERCENTAGE);
        this.setHeight(100, Sizeable.Unit.PERCENTAGE);
        this.setMargin(new MarginInfo(false, true, false, true));
    }

    private void initContent() {
        //Funktionsleiste
        Component funktionsLeiste = initFunktionsleiste();
        //Tabelle
        Component jobTabelle = initJobTabelle();


        addComponent(funktionsLeiste);
        addComponent(jobTabelle);

        setExpandRatio(funktionsLeiste, 0.1f);
        setExpandRatio(jobTabelle, 0.9f);
    }

    private Component initFunktionsleiste() {
        FilterFunktionsComponent filterFunktionsComponent = new FilterFunktionsComponent();
        //Funktion Add-Button
        filterFunktionsComponent.addButtonListener((Button.ClickListener) clickEvent -> {
            WechselJobBearbeitenWindow wechselJobBearbeitenWindow = new WechselJobBearbeitenWindow();
            wechselJobBearbeitenWindow.addAbschlussClickListener((Button.ClickListener) clickEvent1 -> {
                WechselTradeJobVO neuerWechselTradeJobVO = wechselJobBearbeitenWindow.holeVollstaendigeEingaben();
                if (neuerWechselTradeJobVO != null) {
                    if (wechselTradeJobViewService.erstelleNeuenTradeJob(neuerWechselTradeJobVO)) {

                        //Schliessen des Fensters
                        wechselJobBearbeitenWindow.closeWindow();

                        //Neuer Job wurde erfolgreich erstellt
                        wechselTradeJobVOList.add(neuerWechselTradeJobVO);
                        setzeDaten();
                    }
                    else {
                        //Fehler beim Erstellen des neuen Jobs
                        Notification.show("Es ist ein Fehler beim Erstellen des neuen Jobs aufgetreten", Notification.Type.ERROR_MESSAGE);
                    }
                }
            });
            UI.getCurrent().addWindow(wechselJobBearbeitenWindow.getWindow());
        });

        //Symbol Filter-Funktion
        filterFunktionsComponent.addOnSymbolFilterValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            ListDataProvider<WechselTradeJobVO> dataProvider = (ListDataProvider<WechselTradeJobVO>) wechselJobGrid.getDataProvider();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                dataProvider.clearFilters();
            }
            else {
                dataProvider.setFilter((ValueProvider<WechselTradeJobVO, String>) wechselTradeJobVO -> wechselTradeJobVO.getWechselTradeJobDTO().getCryptoWaehrung(),
                        s -> caseInsensitiveContains(s, valueChangeEvent.getValue()));
            }
        });

        //TradingPlattform Filter-Funktion
        filterFunktionsComponent.addTradingPlattformValueChangeListener((HasValue.ValueChangeListener<TradingPlattform>) valueChangeEvent -> {
            FilterVO filterVO = new FilterVO();
            filterVO.setTradingPlattform(valueChangeEvent.getValue());
            filterVO.setTradeTyp(filterFunktionsComponent.getSelectedTradeStatusTyp());
            filterTradeJobs(filterVO);
        });

        //TradeTyp Filter-Funktion
        filterFunktionsComponent.addTradeStatusTypValueChangeListener((HasValue.ValueChangeListener<TradeTyp>) valueChangeEvent -> {
            FilterVO filterVO = new FilterVO();
            filterVO.setTradingPlattform(filterFunktionsComponent.getSelectedTradingPlattform());
            filterVO.setTradeTyp(valueChangeEvent.getValue());
            filterTradeJobs(filterVO);
        });

        return filterFunktionsComponent;
    }


    private Component initJobTabelle() {
        wechselJobGrid = new Grid<>();
        wechselJobGrid.setSizeFull();
        wechselJobGrid.setSelectionMode(Grid.SelectionMode.NONE);

        setzeDaten();

        //Datum
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJobVO -> {
            String text = ViewUtils.dateTimeToStringOhneSkunden(wechselTradeJobVO.getWechselTradeJobDTO().getErstelltAm());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Datum").setResizable(false).setStyleGenerator(item -> "v-align-center")
                .setWidth(150);

        //SYMBOL
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJobVO -> {
            String vonSymbol = wechselTradeJobVO.getWechselTradeJobDTO().getCryptoWaehrung();
            String zuSymbol = wechselTradeJobVO.getWechselTradeJobDTO().getCryptoWaehrungReferenz();
            String text = vonSymbol + ViewUtils.ABSTAND + VaadinIcons.EXCHANGE.getHtml() + ViewUtils.ABSTAND + zuSymbol;
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            label.addStyleName("bold");
            return label;
        }).setCaption("Trade-Info").setResizable(false).setStyleGenerator(item -> "v-align-center").setWidth(150);

        //KAUFWERTGRENZE
        //MENGEREFERENZWERT
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJob -> {
            String text = ViewUtils.formatWertInfo("Kaufwertgrenze", wechselTradeJob.getWechselTradeJobDTO().getKaufwertGrenze(), wechselTradeJob.getWechselTradeJobDTO().getCryptoWaehrungReferenz(),
                    "Kaufmenge", wechselTradeJob.getWechselTradeJobDTO().getMengeReferenzwert(), wechselTradeJob.getWechselTradeJobDTO().getCryptoWaehrungReferenz());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Kauf").setResizable(false);

        //ZIEL
        //AKTUELLERWERT
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJob -> {
            String text = ViewUtils.formatWertInfo("Zielsatz", wechselTradeJob.getWechselTradeJobDTO().getMinimalZielSatz(), "",
                    "Aktueller Wert", wechselTradeJob.getWechselTradeJobDTO().getLetztwert(), wechselTradeJob.getWechselTradeJobDTO().getCryptoWaehrungReferenz());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Ziel").setResizable(false);

        //TRADESTATUS
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJobVO -> {
            String text = ViewUtils.transformTradeAktionUndStatusToIcon(wechselTradeJobVO.getWechselTradeJobDTO().getTradeAktionEnum(), wechselTradeJobVO.getWechselTradeJobDTO().getTradeStatus());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);
            label.setDescription(wechselTradeJobVO.getWechselTradeJobDTO().getTradeAktionEnum().getAnzeigetext());
            return label;
        }).setCaption("Status").setResizable(false).setStyleGenerator(item -> "v-align-center").setWidth(150);

        //PLATTFORM
        wechselJobGrid.addColumn((ValueProvider<WechselTradeJobVO, String>) wechselTradeJobVO -> wechselTradeJobVO.getWechselTradeJobDTO().getTradingPlattform().name())
                .setCaption("Plattform")
                .setResizable(false)
                .setStyleGenerator(item -> "v-align-center").setWidth(120);

        //TRADEAKTIONEN
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJobVO -> {
            NativeButton tradeButton = new NativeButton();
            tradeButton.setIcon(VaadinIcons.EXCHANGE);
            tradeButton.setDescription("Trades");
            tradeButton.setStyleName("tiny");
            tradeButton.addStyleName(CryptoStyles.STANDARD_STATUS_ICON);
            tradeButton.addStyleName(CryptoStyles.ONLY_ICON_BUTTON);

            tradeButton.addClickListener((Button.ClickListener) clickEvent -> {
                List<TradeAktionVO> tradeAktionVOList = tradeAktionViewService.holeTradesFuerJobId(wechselTradeJobVO.getWechselTradeJobDTO().getId());
                if (tradeAktionVOList == null || tradeAktionVOList.isEmpty()) {
                    Notification.show("Keine Trades f\u00fcr diesen Job vorhanden", Notification.Type.TRAY_NOTIFICATION);
                }
                else {
                    TradeAktionWindow tradeAktionWindow = new TradeAktionWindow(tradeAktionVOList, wechselTradeJobVO.getWechselTradeJobDTO());
                    UI.getCurrent().addWindow(tradeAktionWindow.getWindow());
                }
            });

            return tradeButton;
        }).setCaption("Trades")
                .setWidth(90)
                .setStyleGenerator(item -> "v-align-center");

        //AKTION
        wechselJobGrid.addComponentColumn((ValueProvider<WechselTradeJobVO, Component>) wechselTradeJobVO -> {
            NativeButton aktionButton = new NativeButton();
            aktionButton.setIcon(VaadinIcons.PENCIL);
            aktionButton.setDescription("Job bearbeiten");
            aktionButton.setStyleName("tiny");
            aktionButton.addStyleName(CryptoStyles.STANDARD_STATUS_ICON);
            aktionButton.addStyleName(CryptoStyles.ONLY_ICON_BUTTON);

            aktionButton.addClickListener((Button.ClickListener) clickEvent -> {
                WechselJobBearbeitenWindow wechselJobBearbeitenWindow = new WechselJobBearbeitenWindow(wechselTradeJobVO);
                wechselJobBearbeitenWindow.addAbschlussClickListener((Button.ClickListener) clickEvent1 -> {
                    WechselTradeJobVO neuerWechselTradeJobVO = wechselJobBearbeitenWindow.holeVollstaendigeEingaben();
                    if (neuerWechselTradeJobVO != null) {
                        if (wechselTradeJobViewService.bearbeiteTradeJob(neuerWechselTradeJobVO)) {
                            //Schliessen des Fensters
                            wechselJobBearbeitenWindow.closeWindow();
                        }
                        else {
                            //Fehler beim Erstellen des neuen Jobs
                            Notification.show("Es ist ein Fehler bei der Bearbeitung des Jobs aufgetreten", Notification.Type.ERROR_MESSAGE);
                        }
                    }
                });
                UI.getCurrent().addWindow(wechselJobBearbeitenWindow.getWindow());
            });

            return aktionButton;
        }).setCaption("Aktion")
                .setWidth(90)
                .setStyleGenerator(item -> "v-align-center");

        //Header anpassen
        HeaderRow headerRow = wechselJobGrid.getDefaultHeaderRow();
        headerRow.setStyleName("text-align-center");

        return wechselJobGrid;

    }


    private void setzeDaten() {
        //Sortieren der Liste
        wechselTradeJobVOList.sort((v1, v2) -> {
            if (v1.getWechselTradeJobDTO().getErstelltAm().isAfter(v2.getWechselTradeJobDTO().getErstelltAm())) {
                return -1;
            }
            if (v2.getWechselTradeJobDTO().getErstelltAm().isAfter(v1.getWechselTradeJobDTO().getErstelltAm())) {
                return 1;
            }
            if (v1.getWechselTradeJobDTO().getId() > v2.getWechselTradeJobDTO().getId()) {
                return -1;
            }
            if (v2.getWechselTradeJobDTO().getId() > v1.getWechselTradeJobDTO().getId()) {
                return 1;
            }
            return 0;
        });

        wechselJobGrid.setItems(wechselTradeJobVOList);
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }


    private void filterTradeJobs(FilterVO filterVO) {
        ListDataProvider<? extends Filterable> dataProvider = (ListDataProvider<? extends Filterable>) wechselJobGrid.getDataProvider();
        dataProvider.clearFilters();

        //Zuerst nach Plattform filtern
        if (filterVO.getTradingPlattform() != TradingPlattform.ALLE) {
            dataProvider.setFilter(w -> w.filteringTradingPlattform(filterVO.getTradingPlattform()));
        }
        //Nach TradeTyp filtern
        if (filterVO.getTradeTyp() != null) {
            dataProvider.setFilter(w -> w.filteringTradeTyp(filterVO.getTradeTyp()));
        }
    }
}
