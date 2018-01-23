package at.vulperium.cryptobot.tradeaktionen;

import at.vulperium.cryptobot.tradeaktionen.vo.TradeAktionVO;
import at.vulperium.cryptobot.util.FilterFunktionsComponent;
import at.vulperium.cryptobot.util.Filterable;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by 02ub0400 on 18.01.2018.
 */
public class TradeAktionComponent extends VerticalLayout {

    private Grid<TradeAktionVO> grid;

    public TradeAktionComponent(List<TradeAktionVO> tradeAktionVOList) {
        initTradeAktionLayout();
        initContent(tradeAktionVOList);
    }

    private void initTradeAktionLayout() {
        this.setSpacing(true);
        this.setWidth(70, Unit.PERCENTAGE);
        this.setHeight(100, Unit.PERCENTAGE);
        this.setMargin(new MarginInfo(false, true, false, true));
    }


    private void initContent(List<TradeAktionVO> tradeAktionVOList) {
        //Tabelle
        TradeAktionGrid tradeAktionGrid = new TradeAktionGrid();
        tradeAktionGrid.initTradeAktionGrid(tradeAktionVOList);
        grid = tradeAktionGrid.getTabelle();

        //Funktionsleiste
        Component funktionsLeiste = initFunktionsleiste();

        addComponent(funktionsLeiste);
        addComponent(grid);

        setExpandRatio(funktionsLeiste, 0.1f);
        setExpandRatio(grid, 0.9f);
    }


    private Component initFunktionsleiste() {

        FilterFunktionsComponent filterFunktionsComponent = new FilterFunktionsComponent((ListDataProvider<? extends Filterable>) grid.getDataProvider());
        filterFunktionsComponent.getAddButton().setEnabled(false);

        //Symbol Filter-Funktion
        filterFunktionsComponent.addOnSymbolFilterValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {

            ListDataProvider<TradeAktionVO> dataProvider = (ListDataProvider<TradeAktionVO>) grid.getDataProvider();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                dataProvider.clearFilters();
            }
            else {
                dataProvider.setFilter((ValueProvider<TradeAktionVO, String>) tradeAktionVO ->
                                tradeAktionVO.getTradeAktionDTO().getCryptoWaehrung() + tradeAktionVO.getTradeAktionDTO().getCryptoWaehrungReferenz(),
                        s -> caseInsensitiveContains(s, valueChangeEvent.getValue()));
            }

        });

        return filterFunktionsComponent;
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }
}
