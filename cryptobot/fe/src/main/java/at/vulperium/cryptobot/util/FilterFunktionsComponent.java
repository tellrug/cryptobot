package at.vulperium.cryptobot.util;

import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;

import java.util.Arrays;

/**
 * Created by Ace on 06.01.2018.
 */
public class FilterFunktionsComponent extends HorizontalLayout {

    private NativeButton addButton;
    private TextField filterTextField;
    private ComboBox<TradeTyp> tradeStatusTypComboBox;
    private ComboBox<TradingPlattform> tradingPlattformComboBox;

    public FilterFunktionsComponent() {

        initFilterFunktionLayout();
    }

    private void initFilterFunktionLayout() {
        setWidth(100, Unit.PERCENTAGE);
        setSpacing(true);
        setMargin(new MarginInfo(false, false, true, false));

        //Filter Symbol -Textfield
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth(100, Unit.PERCENTAGE);

        filterTextField = new TextField("Symbol:");
        filterTextField.setPlaceholder("z.B.: XVG");
        filterTextField.setValueChangeMode(ValueChangeMode.LAZY);

        formLayout.addComponent(filterTextField);


        //MiddleLayout - Filtern nach Plattform und TradeTyp
        Layout middleLayout = initMiddleLayout();

        //Add-Button
        addButton = new NativeButton();
        addButton.setIcon(VaadinIcons.PLUS);
        addButton.addStyleName(CryptoStyles.STANDARD_STATUS_ICON);
        addButton.addStyleName(CryptoStyles.ONLY_ICON_BUTTON);
        addButton.addStyleName(CryptoStyles.XLARGE_STATUS_ICON);


        addComponent(formLayout);
        addComponent(middleLayout);
        addComponent(addButton);

        setComponentAlignment(formLayout, Alignment.MIDDLE_LEFT);
        setComponentAlignment(middleLayout, Alignment.MIDDLE_CENTER);
        setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
    }

    private Layout initMiddleLayout() {
        HorizontalLayout middleLayout = new HorizontalLayout();
        middleLayout.setSpacing(true);
        middleLayout.setWidth(100, Unit.PERCENTAGE);

        //Filter Plattform
        tradingPlattformComboBox = new ComboBox<>();
        tradingPlattformComboBox.setPlaceholder("Plattform");
        tradingPlattformComboBox.setEmptySelectionAllowed(false);
        tradingPlattformComboBox.setItems(Arrays.asList(TradingPlattform.values()));

        //Filter TradeTyp
        tradeStatusTypComboBox = new ComboBox<>();
        tradeStatusTypComboBox.setPlaceholder("Kategorie");
        tradeStatusTypComboBox.setEmptySelectionAllowed(true);
        tradeStatusTypComboBox.setItems(Arrays.asList(TradeTyp.values()));

        middleLayout.addComponent(tradingPlattformComboBox);
        middleLayout.addComponent(tradeStatusTypComboBox);

        middleLayout.setComponentAlignment(tradingPlattformComboBox, Alignment.MIDDLE_RIGHT);
        middleLayout.setComponentAlignment(tradeStatusTypComboBox, Alignment.MIDDLE_LEFT);

        return middleLayout;
    }

    public Button getAddButton() {
        return addButton;
    }

    public void addButtonListener(Button.ClickListener buttonClickListener) {
        addButton.addClickListener(buttonClickListener);
    }

    public void addOnSymbolFilterValueChangeListener(HasValue.ValueChangeListener<String> valueChangeListener) {
        filterTextField.addValueChangeListener(valueChangeListener);
    }

    public void addTradingPlattformValueChangeListener(HasValue.ValueChangeListener<TradingPlattform> valueChangeListener) {
        tradingPlattformComboBox.addValueChangeListener(valueChangeListener);
    }

    public void addTradeStatusTypValueChangeListener(HasValue.ValueChangeListener<TradeTyp> valueChangeListener) {
        tradeStatusTypComboBox.addValueChangeListener(valueChangeListener);
    }

    public TradingPlattform getSelectedTradingPlattform() {
        return tradingPlattformComboBox.getValue();
    }

    public TradeTyp getSelectedTradeStatusTyp() {
        return tradeStatusTypComboBox.getValue();
    }
}
