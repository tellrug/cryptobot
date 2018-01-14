package at.vulperium.cryptobot.tradejobs;

import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradejobs.service.TradeJobViewService;
import at.vulperium.cryptobot.tradejobs.vo.FilterVO;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.util.FilterFunktionsComponent;
import at.vulperium.cryptobot.util.ViewUtils;
import at.vulperium.cryptobot.utils.TradeUtil;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class TradeJobComponent extends VerticalLayout {

    private TradeJobViewService tradeJobViewService = BeanProvider.getContextualReference(TradeJobViewService.class);

    private List<TradeJobVO> tradeJobVOList;
    private Grid<TradeJobVO> tradeJobGrid;

    public TradeJobComponent() {
        this.tradeJobVOList = tradeJobViewService.holeAlleTradejobVOs();

        initTradeJobLayout();
        initContent();
    }


    private void initTradeJobLayout() {
        this.setSpacing(true);
        this.setWidth(100, Unit.PERCENTAGE);
        this.setHeight(100, Unit.PERCENTAGE);
        this.setMargin(new MarginInfo(false, true, false, true));
    }

    private void initContent() {
        //Funktionsleiste
        Component funktionsLeiste = initFunktionsleiste();
        //Tabelle
        Component jobTabelle = initTradeJobTabelle();


        addComponent(funktionsLeiste);
        addComponent(jobTabelle);

        setExpandRatio(funktionsLeiste, 0.1f);
        setExpandRatio(jobTabelle, 0.9f);
    }

    private Component initFunktionsleiste() {

        FilterFunktionsComponent filterFunktionsComponent = new FilterFunktionsComponent();

        //Funktion Add-Button
        filterFunktionsComponent.addButtonListener((Button.ClickListener) clickEvent -> {
            TradeJobBearbeitenWindow tradeJobBearbeitenWindow = new TradeJobBearbeitenWindow();
            tradeJobBearbeitenWindow.addAbschlussClickListener((Button.ClickListener) clickEvent1 -> {
                //Ueberpruefen ob die Eingaben vollstaendig sind
                TradeJobVO neuerTradeJobVO = tradeJobBearbeitenWindow.holeVollstaendigeEingaben();
                if (neuerTradeJobVO != null) {
                    //Speichern des neuen Jobs
                    if (tradeJobViewService.erstelleNeuenTradeJob(neuerTradeJobVO)) {

                        //Schliessen des Fensters
                        tradeJobBearbeitenWindow.closeWindow();

                        //Neuer Job wurde erfolgreich erstellt
                        tradeJobVOList.add(neuerTradeJobVO);
                        setzeDaten();
                    }
                    else {
                        //Fehler beim Erstellen des neuen Jobs
                        Notification.show("Es ist ein Fehler beim Erstellen des neuen Jobs aufgetreten", Notification.Type.ERROR_MESSAGE);
                    }
                }
            });
            UI.getCurrent().addWindow(tradeJobBearbeitenWindow.getWindow());
        });

        //Symbol Filter-Funktion
        filterFunktionsComponent.addOnSymbolFilterValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {
            ListDataProvider<TradeJobVO> dataProvider = (ListDataProvider<TradeJobVO>) tradeJobGrid.getDataProvider();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                dataProvider.clearFilters();
            }
            else {
                dataProvider.setFilter((ValueProvider<TradeJobVO, String>) tradeJobVO -> tradeJobVO.getTradeJobDTO().getCryptoWaehrung(),
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

    private Component initTradeJobTabelle() {
        tradeJobGrid = new Grid<>();
        tradeJobGrid.setSizeFull();
        tradeJobGrid.setSelectionMode(Grid.SelectionMode.NONE);

        setzeDaten();

        //Datum
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            String text = ViewUtils.dateTimeToStringOhneSkunden(tradeJobVO.getTradeJobDTO().getErstelltAm());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Datum").setResizable(false).setStyleGenerator(item -> "v-align-center")
                .setWidth(150);

        //SYMBOL
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            Label label = ComponentProducer.erstelleLabelHtml(tradeJobVO.getTradeJobDTO().getCryptoWaehrung());
            label.addStyleName("tiny");
            return label;
        }).setCaption("Symbol").setResizable(false).setStyleGenerator(item -> "v-align-center")
                .setWidth(90);

        //MENGE
        tradeJobGrid.addColumn((ValueProvider<TradeJobVO, String>) tradeJobVO -> tradeJobVO.getTradeJobDTO().getMenge().toString())
                .setCaption("Menge")
                .setResizable(false)
                .setStyleGenerator(item -> "v-align-center");

        //KAUFWERT & ZIELWERT
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            String bezAktuellerWert = tradeJobVO.getTradeJobDTO().getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF ? "Ausgang" : "Kauf";
            String referenz = tradeJobVO.getTradeJobDTO().getCryptoWaehrungReferenz();
            String text =
                    ViewUtils.formatWertInfo(bezAktuellerWert, tradeJobVO.getTradeJobDTO().getKaufwert(), referenz, "Ziel", tradeJobVO.getTradeJobDTO().getZielwert(), referenz);
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Wert").setResizable(false);

        //AKTUELLERWERT
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            String referenz = tradeJobVO.getTradeJobDTO().getCryptoWaehrungReferenz();
            String text = ViewUtils.formatWertEinheit(tradeJobVO.getTradeJobDTO().getLetztwert(), referenz);
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Aktuell").setResizable(false);

        //DIFFERENZ
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            String referenz = tradeJobVO.getTradeJobDTO().getCryptoWaehrungReferenz();
            BigDecimal diffAbsolut = TradeUtil.diffAbsolut(tradeJobVO.getTradeJobDTO().getLetztwert(), tradeJobVO.getTradeJobDTO().getKaufwert());
            BigDecimal diffProzent = TradeUtil.diffProzent(tradeJobVO.getTradeJobDTO().getLetztwert(), tradeJobVO.getTradeJobDTO().getKaufwert());
            String text = ViewUtils.formatWertInfo("Absolut", diffAbsolut, referenz, "Prozent", diffProzent, "%", true);
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Differenz").setResizable(false);

        //STATUS
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            String text = ViewUtils.transformTradeAktionUndStatusToIcon(tradeJobVO.getTradeJobDTO().getTradeAktionEnum(), tradeJobVO.getTradeJobDTO().getTradeStatus());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);
            label.setDescription(tradeJobVO.getTradeJobDTO().getTradeAktionEnum().getAnzeigetext());
            return label;
        }).setCaption("Status").setResizable(false).setStyleGenerator(item -> "v-align-center").setWidth(150);

        //PLATTFORM
        tradeJobGrid.addColumn((ValueProvider<TradeJobVO, String>) tradeJobVO -> tradeJobVO.getTradeJobDTO().getTradingPlattform().name())
                .setCaption("Plattform")
                .setResizable(false)
                .setStyleGenerator(item -> "v-align-center").setWidth(120);

        //AKTIONEN
        tradeJobGrid.addComponentColumn((ValueProvider<TradeJobVO, Component>) tradeJobVO -> {
            NativeButton aktionButton = new NativeButton();
            aktionButton.setIcon(VaadinIcons.PENCIL);
            aktionButton.setDescription("Job bearbeiten");
            aktionButton.setStyleName("tiny");
            aktionButton.addStyleName(CryptoStyles.STANDARD_STATUS_ICON);
            aktionButton.addStyleName(CryptoStyles.ONLY_ICON_BUTTON);

            aktionButton.addClickListener((Button.ClickListener) clickEvent -> {
                TradeJobBearbeitenWindow tradeJobBearbeitenWindow = new TradeJobBearbeitenWindow(tradeJobVO);
                tradeJobBearbeitenWindow.addAbschlussClickListener((Button.ClickListener) clickEvent1 -> {
                    //Ueberpruefen ob die Eingaben vollstaendig sind
                    TradeJobVO bearbeiteterTradeJobVO = tradeJobBearbeitenWindow.holeVollstaendigeEingaben();
                    if (bearbeiteterTradeJobVO != null) {
                        //Aktualisieren des Jobs
                        if (tradeJobViewService.bearbeiteTradeJob(bearbeiteterTradeJobVO)) {
                            //Neuer Job wurde erfolgreich erstellt
                            //Schliessen des Fensters
                            tradeJobBearbeitenWindow.closeWindow();
                        }
                        else {
                            //Fehler beim Erstellen des neuen Jobs
                            Notification.show("Es ist ein Fehler bei der Bearbeitung des Jobs aufgetreten", Notification.Type.ERROR_MESSAGE);
                        }

                        tradeJobGrid.getDataProvider().refreshAll();
                    }
                });
                UI.getCurrent().addWindow(tradeJobBearbeitenWindow.getWindow());
            });

            return aktionButton;
        }).setCaption("Aktion")
                .setWidth(90)
                .setStyleGenerator(item -> "v-align-center");


        //Header anpassen
        HeaderRow headerRow = tradeJobGrid.getDefaultHeaderRow();
        headerRow.setStyleName("text-align-center");

        return tradeJobGrid;
    }

    private void setzeDaten() {
        //Sortieren der Liste
        Collections.sort(tradeJobVOList, (v1, v2) -> {
            if (v1.getTradeJobDTO().getErstelltAm().isAfter(v2.getTradeJobDTO().getErstelltAm())) {
                return -1;
            }
            if (v2.getTradeJobDTO().getErstelltAm().isAfter(v1.getTradeJobDTO().getErstelltAm())) {
                return 1;
            }
            if (v1.getTradeJobDTO().getId() > v2.getTradeJobDTO().getId()) {
                return -1;
            }
            if (v2.getTradeJobDTO().getId() > v1.getTradeJobDTO().getId()) {
                return 1;
            }
            return 0;
        });

        tradeJobGrid.setItems(tradeJobVOList);
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }


    private void filterTradeJobs(FilterVO filterVO) {
        ListDataProvider<TradeJobVO> dataProvider = (ListDataProvider<TradeJobVO>) tradeJobGrid.getDataProvider();

        dataProvider.clearFilters();

        //Zuerst nach Plattform filtern
        if (filterVO.getTradingPlattform() != TradingPlattform.ALLE) {
            dataProvider.setFilter((ValueProvider<TradeJobVO, TradingPlattform>) tradeJobVO -> tradeJobVO.getTradeJobDTO().getTradingPlattform(),
                    tj -> tj == filterVO.getTradingPlattform());
        }

        //Nach TradeTyp filtern
        if (filterVO.getTradeTyp() != null) {
            dataProvider.setFilter((ValueProvider<TradeJobVO, TradeTyp>) tradeJobVO -> tradeJobVO.getTradeJobDTO().getTradeAktionEnum().getTradeTyp(),
                    tj -> tj == filterVO.getTradeTyp());
        }
    }
}
