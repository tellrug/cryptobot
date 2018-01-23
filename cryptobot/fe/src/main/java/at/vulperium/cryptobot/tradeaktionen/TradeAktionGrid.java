package at.vulperium.cryptobot.tradeaktionen;

import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.tradeaktionen.vo.TradeAktionVO;
import at.vulperium.cryptobot.util.CryptoStyles;
import at.vulperium.cryptobot.util.ViewUtils;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.HeaderRow;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public class TradeAktionGrid {

    private Grid<TradeAktionVO> grid;
    private boolean winModus = false;

    public TradeAktionGrid() {
        this.winModus = false;
    }

    public TradeAktionGrid(boolean winModus) {
        this.winModus = winModus;
    }

    public void initTradeAktionGrid(List<TradeAktionVO> tradeAktionVOList) {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        setzeDaten(tradeAktionVOList);

        //Datum
        grid.addComponentColumn((ValueProvider<TradeAktionVO, Component>) tradeAktionVO -> {
            String text = ViewUtils.dateTimeToStringOhneSkunden(tradeAktionVO.getTradeAktionDTO().getErstelltAm());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Datum").setResizable(false).setStyleGenerator(item -> "v-align-center").setWidth(150);

        //Symbol
        grid.addComponentColumn((ValueProvider<TradeAktionVO, Component>) tradeAktionVO -> {

            String vonSymbol;
            String zuSymbol;
            if (tradeAktionVO.getTradeAktionDTO().getTradeTyp() == TradeTyp.KAUF) {
                vonSymbol = tradeAktionVO.getTradeAktionDTO().getCryptoWaehrungReferenz();
                zuSymbol = tradeAktionVO.getTradeAktionDTO().getCryptoWaehrung();
            }
            else {
                vonSymbol = tradeAktionVO.getTradeAktionDTO().getCryptoWaehrung();
                zuSymbol = tradeAktionVO.getTradeAktionDTO().getCryptoWaehrungReferenz();
            }

            String text = ViewUtils.formatTradeInfo(vonSymbol, zuSymbol, VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), tradeAktionVO.getTradeAktionDTO().getTradeTyp());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            label.addStyleName("bold");
            return label;
        }).setCaption("Trade-Info").setResizable(false).setStyleGenerator(item -> "v-align-center").setWidth(150);

        //Menge
        grid.addComponentColumn((ValueProvider<TradeAktionVO, Component>) tradeAktionVO -> {
            String symbolText = ViewUtils.formatWertEinheit(tradeAktionVO.getTradeAktionDTO().getMenge(), tradeAktionVO.getTradeAktionDTO().getCryptoWaehrung());
            String color = tradeAktionVO.getTradeAktionDTO().getTradeTyp() == TradeTyp.KAUF ? ViewUtils.COLOR_GREEN : ViewUtils.COLOR_RED;
            symbolText = ViewUtils.formatColor(symbolText, color);

            BigDecimal referenzWert = tradeAktionVO.getTradeAktionDTO().getMenge().multiply(tradeAktionVO.getTradeAktionDTO().getPreisProEinheit());
            String symbolReferenzText = ViewUtils.formatWertEinheit(referenzWert, tradeAktionVO.getTradeAktionDTO().getCryptoWaehrungReferenz());
            color = tradeAktionVO.getTradeAktionDTO().getTradeTyp() == TradeTyp.KAUF ? ViewUtils.COLOR_RED : ViewUtils.COLOR_GREEN;
            symbolReferenzText = ViewUtils.formatColor(symbolReferenzText, color);

            String text = symbolText + ViewUtils.NEW_LINE + symbolReferenzText;
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            label.addStyleName("bold");
            return label;
        }).setCaption("Menge").setResizable(false);

        //PreisProEinheit
        grid.addComponentColumn((ValueProvider<TradeAktionVO, Component>) tradeAktionVO -> {
            String text = ViewUtils.formatWertEinheit(tradeAktionVO.getTradeAktionDTO().getPreisProEinheit(), tradeAktionVO.getTradeAktionDTO().getCryptoWaehrungReferenz());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName("tiny");
            return label;
        }).setCaption("Preis / Einheit").setResizable(false).setStyleGenerator(item -> "v-align-center");

        //TradeStatus
        grid.addComponentColumn((ValueProvider<TradeAktionVO, Component>) tradeAktionVO -> {
            String text = ViewUtils.transformTradeStatusToIcon(tradeAktionVO.getTradeAktionDTO().getTradeStatus());
            Label label = ComponentProducer.erstelleLabelHtml(text);
            label.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);
            return label;
        }).setCaption("Status").setResizable(false).setStyleGenerator(item -> "v-align-center").setWidth(120);

        //TradingPlattform (nur vollansicht)
        if (!winModus) {
            grid.addColumn((ValueProvider<TradeAktionVO, String>) tradeAktionVO -> tradeAktionVO.getTradeAktionDTO().getTradingPlattform().name())
                    .setCaption("Plattform")
                    .setResizable(false)
                    .setStyleGenerator(item -> "v-align-center").setWidth(120);
        }


        //Header anpassen
        HeaderRow headerRow = grid.getDefaultHeaderRow();
        headerRow.setStyleName("text-align-center");
    }

    private void setzeDaten(List<TradeAktionVO> tradeAktionVOList) {
        //Sortieren der Liste
        Collections.sort(tradeAktionVOList, (v1, v2) -> {
            if (v1.getTradeAktionDTO().getErstelltAm().isAfter(v2.getTradeAktionDTO().getErstelltAm())) {
                return -1;
            }
            if (v2.getTradeAktionDTO().getErstelltAm().isAfter(v1.getTradeAktionDTO().getErstelltAm())) {
                return 1;
            }
            if (v1.getTradeAktionDTO().getId() > v2.getTradeAktionDTO().getId()) {
                return -1;
            }
            if (v2.getTradeAktionDTO().getId() > v1.getTradeAktionDTO().getId()) {
                return 1;
            }
            return 0;
        });

        grid.setItems(tradeAktionVOList);
    }

    public Grid<TradeAktionVO> getTabelle() {
        return grid;
    }
}
