package at.vulperium.cryptobot.tradeaktionen;

import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.base.components.SimpleWindowLayout;
import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.messagebundles.UtilityMessages;
import at.vulperium.cryptobot.tradeaktionen.vo.TradeAktionVO;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ace on 19.01.2018.
 */
public class TradeAktionWindow implements Serializable {

    private UtilityMessages utilityMessages = BeanProvider.getContextualReference(UtilityMessages.class);

    private Window window = null;
    private SimpleWindowLayout simpleWinLayout;
    private AbstractTradeJobDTO tradeJob;

    private List<TradeAktionVO> tradeAktionVOList;

    public TradeAktionWindow(List<TradeAktionVO> tradeAktionVOList, AbstractTradeJobDTO abstractTradeJobDTO) {
        this.tradeAktionVOList = tradeAktionVOList;
        this.tradeJob =abstractTradeJobDTO;
        initWindow();
    }

    private void initWindow() {
        window = ComponentProducer.erstelleSimplesWindow(70f, 45f);
        window.setContent(initLayout());
    }

    private Component initLayout() {
        simpleWinLayout = new SimpleWindowLayout(erstelleWindowCaption(), VaadinIcons.EXCHANGE, utilityMessages.ok());

        Layout neuerRequestLayout = initNeuerGridLayout();

        //Hinzufuegen des Inhalts
        simpleWinLayout.getContentLayout().setSizeFull();
        simpleWinLayout.getContentLayout().addComponent(neuerRequestLayout);

        simpleWinLayout.addClickListenerToButton1((Button.ClickListener) event -> closeWindow());
        return simpleWinLayout.getWindowLayout();
    }

    private Layout initNeuerGridLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true, false, false, false));

        TradeAktionGrid tradeAktionGrid = new TradeAktionGrid(true);
        tradeAktionGrid.initTradeAktionGrid(tradeAktionVOList);

        verticalLayout.addComponent(tradeAktionGrid.getTabelle());

        return verticalLayout;
    }

    public Window getWindow() {
        return window;
    }

    public void closeWindow() {
        if (window != null) {
            window.close();
        }
    }

    private String erstelleWindowCaption() {
        TradeJobTyp tradeJobTyp = tradeJob.getTradeJobTyp();

        String caption = "Trades f\u00fcr ";
        if (TradeJobTyp.SIMPEL == tradeJobTyp) {
            caption += "einfachen Job: ";
        }
        else if (TradeJobTyp.WECHSEL == tradeJobTyp) {
            caption += "Wechsel-Job: ";
        }

        caption += tradeJob.getCryptoWaehrung() + " - " + tradeJob.getCryptoWaehrungReferenz();
        return caption;
    }
}
