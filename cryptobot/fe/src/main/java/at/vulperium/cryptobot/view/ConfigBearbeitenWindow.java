package at.vulperium.cryptobot.view;

import at.vulperium.cryptobot.dtos.ConfigDTO;
import at.vulperium.cryptobot.messagebundles.UtilityMessages;
import at.vulperium.cryptobot.services.ConfigService;
import at.vulperium.cryptobot.base.components.ComponentProducer;
import at.vulperium.cryptobot.base.components.SimpleWindowLayout;
import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public class ConfigBearbeitenWindow implements Serializable {

    private ConfigService configService = BeanProvider.getContextualReference(ConfigService.class);
    private UtilityMessages utilityMessages = BeanProvider.getContextualReference(UtilityMessages.class);

    private Window window = null;
    private SimpleWindowLayout simpleWinLayout;

    private List<ConfigDTO> configDTOList;
    private Map<ConfigDTO, Binder> binderMap;


    public ConfigBearbeitenWindow() {
        binderMap = new HashMap<>();

        //Laden der Properties
        configDTOList = configService.holeAlleAnonymeConfigDTOs();
        initWindow();
    }


    private void initWindow() {
        window = ComponentProducer.erstelleSimplesWindow(30f, 25f);
        window.setContent(initLayout());
    }

    private Component initLayout() {
        simpleWinLayout = new SimpleWindowLayout("Einstellungen", VaadinIcons.COG, utilityMessages.ok());

        Layout neuerRequestLayout = initPropertiesLayout();

        //Hinzufuegen des Inhalts
        simpleWinLayout.getContentLayout().setSizeFull();
        simpleWinLayout.getContentLayout().addComponent(neuerRequestLayout);

        simpleWinLayout.addClickListenerToButton1((Button.ClickListener) event -> closeWindow());
        return simpleWinLayout.getWindowLayout();
    }

    private Layout initPropertiesLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(new MarginInfo(true, false, false, false));

        GridLayout propertiesLayout = new GridLayout(2, configDTOList.size() + 1);
        propertiesLayout.setSpacing(true);
        propertiesLayout.setSizeFull();
        propertiesLayout.setMargin(false);

        for (int i = 0; i < configDTOList.size(); i++) {
            ConfigDTO configDTO =  configDTOList.get(i);

            Binder<ConfigDTO> requestBinder = new Binder<>();
            requestBinder.setBean(configDTO);

            binderMap.put(configDTO, requestBinder);

            Label label = new Label("<strong>" + configDTO.getConfigEnum().getBezeichnung() +":</strong>");
            label.setContentMode(ContentMode.HTML);

            TextField tf = new TextField();
            tf.setWidth(100f, Sizeable.Unit.PERCENTAGE);
            tf.setReadOnly(true);
            requestBinder.forField(tf)
                    .withValidator(StringUtils::isNotEmpty, "Es muss eine Wert angegeben werden.")
                    .bind((ValueProvider<ConfigDTO, String>) ConfigDTO::getConfigValue, (Setter<ConfigDTO, String>) ConfigDTO::setConfigValue);

            propertiesLayout.addComponent(label, 0, i);
            propertiesLayout.addComponent(tf, 1, i);

            propertiesLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        }

        propertiesLayout.setColumnExpandRatio(0, 0.2f);
        propertiesLayout.setColumnExpandRatio(1, 0.8f);

        propertiesLayout.setRowExpandRatio(configDTOList.size() + 1, 1f);

        verticalLayout.addComponent(propertiesLayout);
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
}
