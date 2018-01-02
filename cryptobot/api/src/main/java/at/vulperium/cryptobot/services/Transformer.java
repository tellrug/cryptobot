package at.vulperium.cryptobot.services;

import java.io.Serializable;

/**
 * Created by Ace on 26.12.2017.
 */
public interface Transformer<S, T> extends Serializable {

    T transform(S source);

    T transform(S source, T target);
}
