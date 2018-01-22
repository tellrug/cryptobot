package at.vulperium.cryptobot.tradeaktionen;

import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.tradeaktionen.vo.TradeAktionVO;
import at.vulperium.cryptobot.tradejobs.vo.FilterVO;
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
        //Funktionsleiste
        Component funktionsLeiste = initFunktionsleiste();
        //Tabelle
        TradeAktionGrid tradeAktionGrid = new TradeAktionGrid();
        tradeAktionGrid.initTradeAktionGrid(tradeAktionVOList);
        grid = tradeAktionGrid.getTabelle();

        addComponent(funktionsLeiste);
        addComponent(grid);

        setExpandRatio(funktionsLeiste, 0.1f);
        setExpandRatio(grid, 0.9f);
    }


    private Component initFunktionsleiste() {

        FilterFunktionsComponent filterFunktionsComponent = new FilterFunktionsComponent();
        filterFunktionsComponent.getAddButton().setEnabled(false);

        //Symbol Filter-Funktion
        filterFunktionsComponent.addOnSymbolFilterValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent -> {

            ListDataProvider<TradeAktionVO> dataProvider = (ListDataProvider<TradeAktionVO>) grid.getDataProvider();
            if (StringUtils.isEmpty(valueChangeEvent.getValue())) {
                dataProvider.clearFilters();
            }
            else {
                dataProvider.setFilter((ValueProvider<TradeAktionVO, String>) tradeAktionVO ->
                        tradeAktionVO.getTradeAktionDTO().getVonWaehrung() + tradeAktionVO.getTradeAktionDTO().getZuWaehrung(),
                        s -> caseInsensitiveContains(s, valueChangeEvent.getValue()));
            }

        });

        //TradingPlattform Filter-Funktion
        filterFunktionsComponent.addTradingPlattformValueChangeListener((HasValue.ValueChangeListener<TradingPlattform>) valueChangeEvent -> {
            FilterVO filterVO = new FilterVO();
            filterVO.setTradingPlattform(valueChangeEvent.getValue());
            filterVO.setTradeTyp(filterFunktionsComponent.getSelectedTradeStatusTyp());
            filterTradeAktionen(filterVO);
        });

        //TradeTyp Filter-Funktion
        filterFunktionsComponent.addTradeStatusTypValueChangeListener((HasValue.ValueChangeListener<TradeTyp>) valueChangeEvent -> {
            FilterVO filterVO = new FilterVO();
            filterVO.setTradingPlattform(filterFunktionsComponent.getSelectedTradingPlattform());
            filterVO.setTradeTyp(valueChangeEvent.getValue());
            filterTradeAktionen(filterVO);
        });

        return filterFunktionsComponent;
    }

    private void filterTradeAktionen(FilterVO filterVO) {
        ListDataProvider<? extends Filterable> dataProvider = (ListDataProvider<? extends Filterable>) grid.getDataProvider();
        dataProvider.clearFilters();

        dataProvider.setFilter(e -> e.filteringTradingPlattform(filterVO.getTradingPlattform()));
        dataProvider.setFilter(e -> e.filteringTradeTyp(filterVO.getTradeTyp()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
            return where.toLowerCase().contains(what.toLowerCase());
        }
}
