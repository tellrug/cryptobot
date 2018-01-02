package at.vulperium.cryptobot.services;

/**
 * Created by Ace on 26.12.2017.
 */
public interface TransformBothDirections<S, T> extends Transformer<S, T> {

    S transformInverse(T source);

    S transformInverse(T source, S target);
}
