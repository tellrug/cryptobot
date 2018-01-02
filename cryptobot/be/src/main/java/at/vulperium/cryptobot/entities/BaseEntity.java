package at.vulperium.cryptobot.entities;

import javax.naming.OperationNotSupportedException;
import java.io.Serializable;

/**
 * Created by Ace on 26.12.2017.
 */
public abstract class BaseEntity<T> implements Serializable {

    public abstract T getId();

    public void setId(T id) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Id darf nicht gesetzt werden");
    }
}
