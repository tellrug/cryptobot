package at.vulperium.cryptobot.base.navigator;

import com.vaadin.navigator.ViewChangeListener;

/**
 * Created by 02ub0400 on 05.01.2018.
 */
public interface BaseViewChangeListener extends ViewChangeListener {

    @Override
    default void afterViewChange(ViewChangeEvent event) {

    }

    @Override
    default boolean beforeViewChange(ViewChangeEvent viewChangeEvent) {
        return true;
    }
}
