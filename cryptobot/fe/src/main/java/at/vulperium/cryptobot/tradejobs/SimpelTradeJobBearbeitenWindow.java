package at.vulperium.cryptobot.tradejobs;

import at.vulperium.cryptobot.base.components.AbstractTradejobBearbeitenWindow;
import at.vulperium.cryptobot.base.components.WertEinheitComponent;
import at.vulperium.cryptobot.dtos.SimpelTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradejobs.vo.TradeJobVO;
import at.vulperium.cryptobot.utils.TradeUtil;
import at.vulperium.cryptobot.validators.BigDecimalValidator;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 02ub0400 on 19.02.2018.
 */
public class SimpelTradeJobBearbeitenWindow extends AbstractTradejobBearbeitenWindow {

    private TradeJobVO tradeJobVO;

    private Binder<SimpelTradeJobDTO> requestBinder = new Binder<>();
    private WertEinheitComponent mengeComp;


    public SimpelTradeJobBearbeitenWindow(TradeJobVO tradeJobVO) {
        super(tradeJobVO.getSimpelTradeJobDTO(), tradeJobVO.getSimpelTradeJobDTO() != null && tradeJobVO.getSimpelTradeJobDTO().getId() != null);
        this.tradeJobVO = tradeJobVO;
        initWindow();
    }

    @Override
    protected Layout initContentRequestLayout(TradeAktionEnum tradeAktionEnum) {
        if (!bearbeitungsModus) {
            tradeJobVO.getSimpelTradeJobDTO().setTradeStatus(TradeStatus.ERSTELLT);
        }

        if (TradeAktionEnum.KAUF_ZIEL == tradeAktionEnum || TradeAktionEnum.VERKAUF_ZIEL == tradeAktionEnum) {
            return initBeobachtungsJobLayout();
        }
        if (TradeAktionEnum.ORDER_KAUF == tradeAktionEnum || TradeAktionEnum.ORDER_VERKAUF == tradeAktionEnum) {
            return initTradeJobLayout();
        }
        requestBinder.setBean(tradeJobVO.getSimpelTradeJobDTO());
        return null;
    }


    private Layout initBeobachtungsJobLayout() {
        GridLayout neuerRequestLayout = new GridLayout(2, 3);
        neuerRequestLayout.setSpacing(true);
        neuerRequestLayout.setSizeFull();
        neuerRequestLayout.setMargin(false);

        //AKTUELLER WERT
        Label labelAktuellerWert = new Label(VaadinIcons.DOLLAR.getHtml() + "  <strong>Kaufwert</strong>");
        labelAktuellerWert.setContentMode(ContentMode.HTML);

        initAktuellerWertLayout();
        neuerRequestLayout.addComponent(labelAktuellerWert, 0, 0);
        neuerRequestLayout.addComponent(aktuellerWertComp, 1, 0);

        neuerRequestLayout.setComponentAlignment(labelAktuellerWert, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(aktuellerWertComp, Alignment.MIDDLE_LEFT);

        //ZIELWERT
        Label labelZiel = new Label(VaadinIcons.FLAG_CHECKERED.getHtml() + " <strong>Ziel</strong>");
        labelZiel.setContentMode(ContentMode.HTML);

        HorizontalLayout zielLayout = initZielLayout();
        neuerRequestLayout.addComponent(labelZiel, 0, 1);
        neuerRequestLayout.addComponent(zielLayout, 1, 1);

        neuerRequestLayout.setComponentAlignment(labelZiel, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(zielLayout, Alignment.MIDDLE_LEFT);

        neuerRequestLayout.setColumnExpandRatio(0, 0.2f);
        neuerRequestLayout.setColumnExpandRatio(1, 0.8f);

        neuerRequestLayout.setRowExpandRatio(3, 1f);

        return neuerRequestLayout;
    }

    private Layout initTradeJobLayout() {
        GridLayout neuerRequestLayout = new GridLayout(2, 4);
        neuerRequestLayout.setSpacing(true);
        neuerRequestLayout.setSizeFull();
        neuerRequestLayout.setMargin(false);

        //MENGE
        Label labelMenge = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Menge</strong>");
        labelMenge.setContentMode(ContentMode.HTML);

        mengeComp = new WertEinheitComponent(getSymbolValue(), "z.B.: '20.5'");
        neuerRequestLayout.addComponent(labelMenge, 0, 0);
        neuerRequestLayout.addComponent(mengeComp, 1, 0);

        requestBinder.forField(mengeComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<SimpelTradeJobDTO, String>) tradeJobDTO -> getBigDecimalAsString(tradeJobDTO.getMenge()),
                        (Setter<SimpelTradeJobDTO, String>) (tradeJobDTO, value) -> tradeJobDTO.setMenge(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelMenge, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(mengeComp, Alignment.MIDDLE_LEFT);

        //AKTUELLER WERT
        Label labelAktuellerWert = new Label(VaadinIcons.DOLLAR.getHtml() + "  <strong>Aktueller Wert</strong>");
        labelAktuellerWert.setContentMode(ContentMode.HTML);

        initAktuellerWertLayout();
        neuerRequestLayout.addComponent(labelAktuellerWert, 0, 1);
        neuerRequestLayout.addComponent(aktuellerWertComp, 1, 1);

        neuerRequestLayout.setComponentAlignment(labelAktuellerWert, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(aktuellerWertComp, Alignment.MIDDLE_LEFT);

        //ZIEL
        Label labelZiel = new Label(VaadinIcons.FLAG_CHECKERED.getHtml() + " <strong>Ziel</strong>");
        labelZiel.setContentMode(ContentMode.HTML);

        HorizontalLayout zielLayout = initZielLayout();
        neuerRequestLayout.addComponent(labelZiel, 0, 2);
        neuerRequestLayout.addComponent(zielLayout, 1, 2);

        neuerRequestLayout.setComponentAlignment(labelZiel, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(zielLayout, Alignment.MIDDLE_LEFT);


        neuerRequestLayout.setColumnExpandRatio(0, 0.2f);
        neuerRequestLayout.setColumnExpandRatio(1, 0.8f);

        neuerRequestLayout.setRowExpandRatio(3, 1f);

        return neuerRequestLayout;
    }

    private boolean istGesamteEingabeVollstaendig() {
        boolean eingabeVollstaendig = super.istEingabeVollstaendig();
        //Ueberpruefen ob restliche Daten richtig eingegeben wurden
        BinderValidationStatus requestValidationStatus = requestBinder.validate();
        return requestValidationStatus.isOk() && eingabeVollstaendig;
    }

    @Override
    protected String ermittleTitel(boolean bearbeitungsModus) {
        if (bearbeitungsModus) {
            return "Bearbeite Trade-Job";
        }
        else {
            return "Erstelle Trade-Job";
        }
    }

    @Override
    protected void symbolChanged(String value) {
        if (mengeComp != null) {
            mengeComp.getEinheitLabel().setValue(value);
        }
    }

    @Override
    protected void referenzSymbolChanged(String value) {
        if (aktuellerWertComp != null) {
            aktuellerWertComp.getEinheitLabel().setValue(value);
        }
        if (labelZielEinheit != null) {
            labelZielEinheit.setValue(value);
        }
    }

    @Override
    protected List<TradeAktionEnum> ermittleRelevanteTradeAktionList() {
        List<TradeAktionEnum> tradeAktionEnumList = new ArrayList<>();
        if (bearbeitungsModus) {
            tradeAktionEnumList.add(tradeJobVO.getSimpelTradeJobDTO().getTradeAktionEnum());
        }
        else {
            tradeAktionEnumList.add(TradeAktionEnum.KAUF_ZIEL);
            tradeAktionEnumList.add(TradeAktionEnum.VERKAUF_ZIEL);
            tradeAktionEnumList.add(TradeAktionEnum.ORDER_KAUF);
            tradeAktionEnumList.add(TradeAktionEnum.ORDER_VERKAUF);
        }
        return tradeAktionEnumList;
    }

    @Override
    protected List<TradingPlattform> ermittleRelevanteTradingPlattformList() {
        List<TradingPlattform> tradingPlattformList = new ArrayList<>();

        if (bearbeitungsModus) {
            tradingPlattformList.add(tradeJobVO.getSimpelTradeJobDTO().getTradingPlattform());
        }
        else {
            for (TradingPlattform tradingPlattform : TradingPlattform.values()) {
                if (tradingPlattform != TradingPlattform.ALLE) {
                    tradingPlattformList.add(tradingPlattform);
                }
            }
        }
        return tradingPlattformList;
    }

    public TradeJobVO holeVollstaendigeEingaben() {
        if (!istGesamteEingabeVollstaendig()) {
            return null;
        }

        //Schreiben der uebrigen Datenfelder
        requestBinder.writeBeanIfValid(tradeJobVO.getSimpelTradeJobDTO());

        if (tradeJobVO.getSimpelTradeJobDTO().getTradeAktionEnum() == TradeAktionEnum.KAUF_ZIEL ||
                tradeJobVO.getSimpelTradeJobDTO().getTradeAktionEnum() == TradeAktionEnum.VERKAUF_ZIEL) {
            //Werte setzen
            tradeJobVO.getSimpelTradeJobDTO().setMenge(TradeUtil.getBigDecimal(1.0));
        }
        return tradeJobVO;
    }
}
