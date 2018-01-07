package at.vulperium.cryptobot.base.components;

import com.vaadin.ui.Layout;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 17.08.2017.
 */
public abstract class AbstractWindowLayout implements Serializable {

    private static final long serialVersionUID = 2216934206542774619L;

    public abstract Layout getWindowLayout();

    public abstract Layout getHeaderLayout();

    public abstract Layout getContentLayout();

    public abstract Layout getBottomLayout();
}
