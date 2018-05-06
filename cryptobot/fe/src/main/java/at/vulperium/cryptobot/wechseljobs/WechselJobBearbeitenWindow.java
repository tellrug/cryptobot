package at.vulperium.cryptobot.wechseljobs;

import at.vulperium.cryptobot.base.components.AbstractTradejobBearbeitenWindow;
import at.vulperium.cryptobot.base.components.WertEinheitComponent;
import at.vulperium.cryptobot.dtos.WechselTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeAktionEnum;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.utils.TradeUtil;
import at.vulperium.cryptobot.validators.BigDecimalValidator;
import at.vulperium.cryptobot.wechseljobs.vo.WechselTradeJobVO;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 02ub0400 on 21.02.2018.
 */
public class WechselJobBearbeitenWindow extends AbstractTradejobBearbeitenWindow {

    private WechselTradeJobVO wechselTradeJobVO;

    private Binder<WechselTradeJobDTO> requestBinder = new Binder<>();

    private WertEinheitComponent kaufgrenzwertComp;
    private WertEinheitComponent kaufmengeComp;
    private WertEinheitComponent aktuellerWertComp;
    private WertEinheitComponent zielsatzComp;

    public WechselJobBearbeitenWindow(WechselTradeJobVO wechselTradeJobVO) {
        super(wechselTradeJobVO.getWechselTradeJobDTO(), wechselTradeJobVO.getWechselTradeJobDTO() != null && wechselTradeJobVO.getWechselTradeJobDTO().getId() != null);
        this.wechselTradeJobVO = wechselTradeJobVO;
        initWindow();
    }

    @Override
    protected Layout initContentRequestLayout(TradeAktionEnum tradeAktionEnum) {
        if (!bearbeitungsModus) {
            wechselTradeJobVO.getWechselTradeJobDTO().setTradeStatus(TradeStatus.ERSTELLT);
        }
        wechselTradeJobVO.getWechselTradeJobDTO().setTradeTyp(tradeAktionEnum.getTradeTyp());


        GridLayout neuerRequestLayout = new GridLayout(2, 5);
        neuerRequestLayout.setSpacing(true);
        neuerRequestLayout.setSizeFull();
        neuerRequestLayout.setMargin(false);


        //KAUFGRENZWERT
        Label labelKaufgrenzwert = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Kauf-Grenzwert</strong>");
        labelKaufgrenzwert.setContentMode(ContentMode.HTML);

        kaufgrenzwertComp = new WertEinheitComponent("-", "z.B.: '0.00005'");
        kaufgrenzwertComp.getEinheitLabel().setValue(getRefSymbolValue());
        neuerRequestLayout.addComponent(labelKaufgrenzwert, 0, 1);
        neuerRequestLayout.addComponent(kaufgrenzwertComp, 1, 1);

        requestBinder.forField(kaufgrenzwertComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getKaufwertGrenze()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setKaufwertGrenze(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelKaufgrenzwert, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(kaufgrenzwertComp, Alignment.MIDDLE_LEFT);

        //KAUFMENGE
        Label labelKaufmenge = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Kauf-Menge</strong>");
        labelKaufmenge.setContentMode(ContentMode.HTML);

        kaufmengeComp = new WertEinheitComponent(getRefSymbolValue(), "z.B.: '0.02'");

        neuerRequestLayout.addComponent(labelKaufmenge, 0, 2);
        neuerRequestLayout.addComponent(kaufmengeComp, 1, 2);

        requestBinder.forField(kaufmengeComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getMengeReferenzwert()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setMengeReferenzwert(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelKaufmenge, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(kaufmengeComp, Alignment.MIDDLE_LEFT);

        //AKTUELLER WERT
        Label labelAktuellerWert = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Kauf/Aktueller Wert</strong>");
        labelAktuellerWert.setContentMode(ContentMode.HTML);

        aktuellerWertComp = new WertEinheitComponent("-", "z.B.: '0.0002'");
        aktuellerWertComp.getEinheitLabel().setValue(getRefSymbolValue());
        neuerRequestLayout.addComponent(labelAktuellerWert, 0, 3);
        neuerRequestLayout.addComponent(aktuellerWertComp, 1, 3);

        requestBinder.forField(aktuellerWertComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss eine Menge angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getKaufwert()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setKaufwert(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelAktuellerWert, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(aktuellerWertComp, Alignment.MIDDLE_LEFT);

        //ZIELSATZ
        Label labelZielsatz = new Label(VaadinIcons.COIN_PILES.getHtml() + " <strong>Ziel-Satz</strong>");
        labelZielsatz.setContentMode(ContentMode.HTML);

        zielsatzComp = new WertEinheitComponent("-", "z.B.: '1.3'");
        neuerRequestLayout.addComponent(labelZielsatz, 0, 4);
        neuerRequestLayout.addComponent(zielsatzComp, 1, 4);

        requestBinder.forField(zielsatzComp.getWertTextfield())
                .withValidator(StringUtils::isNotEmpty, "Es muss ein Zielsatz angegeben werden.")
                .withValidator(new BigDecimalValidator())
                .bind((ValueProvider<WechselTradeJobDTO, String>) wechselTradeJobDTO -> getBigDecimalAsString(wechselTradeJobDTO.getMinimalZielSatz()),
                        (Setter<WechselTradeJobDTO, String>) (wechselTradeJobDTO, value) -> wechselTradeJobDTO.setMinimalZielSatz(TradeUtil.getBigDecimal(value)));

        neuerRequestLayout.setComponentAlignment(labelZielsatz, Alignment.MIDDLE_LEFT);
        neuerRequestLayout.setComponentAlignment(zielsatzComp, Alignment.MIDDLE_LEFT);

        neuerRequestLayout.setColumnExpandRatio(0, 0.2f);
        neuerRequestLayout.setColumnExpandRatio(1, 0.8f);

        neuerRequestLayout.setRowExpandRatio(5, 1f);

        return neuerRequestLayout;
    }

    @Override
    protected void symbolChanged(String value) {
    }

    @Override
    protected void referenzSymbolChanged(String value) {
        if (kaufmengeComp != null) {
            kaufmengeComp.getEinheitLabel().setValue(value);
        }
        if (kaufgrenzwertComp != null) {
            kaufgrenzwertComp.getEinheitLabel().setValue(value);
        }
        if (aktuellerWertComp != null) {
            aktuellerWertComp.getEinheitLabel().setValue(value);
        }
    }

    @Override
    protected String ermittleTitel(boolean bearbeitungsModus) {
        if (bearbeitungsModus) {
            return "Bearbeite Wechsel-Tradejob";
        }
        else {
            return "Erstelle Wechsel-Tradejob";
        }
    }

    @Override
    protected List<TradingPlattform> ermittleRelevanteTradingPlattformList() {
        List<TradingPlattform> tradingPlattformList = new ArrayList<>();

        if (bearbeitungsModus) {
            tradingPlattformList.add(wechselTradeJobVO.getWechselTradeJobDTO().getTradingPlattform());
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

    @Override
    protected List<TradeAktionEnum> ermittleRelevanteTradeAktionList() {
        List<TradeAktionEnum> tradeAktionEnumList = new ArrayList<>();
        if (bearbeitungsModus) {
            tradeAktionEnumList.add(wechselTradeJobVO.getWechselTradeJobDTO().getTradeAktionEnum());
        }
        else {
            tradeAktionEnumList.add(TradeAktionEnum.ORDER_KAUF);
            tradeAktionEnumList.add(TradeAktionEnum.ORDER_VERKAUF);
        }
        return tradeAktionEnumList;
    }

    public WechselTradeJobVO holeVollstaendigeEingaben() {
        if (!istGesamteEingabeVollstaendig()) {
            return null;
        }

        //Schreiben der uebrigen Datenfelder
        requestBinder.writeBeanIfValid(wechselTradeJobVO.getWechselTradeJobDTO());
        return wechselTradeJobVO;
    }

    private boolean istGesamteEingabeVollstaendig() {
        boolean eingabeVollstaendig = super.istEingabeVollstaendig();
        //Ueberpruefen ob restliche Daten richtig eingegeben wurden
        BinderValidationStatus requestValidationStatus = requestBinder.validate();
        return requestValidationStatus.isOk() && eingabeVollstaendig;
    }
}
