package at.vulperium.cryptobot.config;

import org.apache.deltaspike.core.api.config.ConfigResolver;

import java.util.concurrent.TimeUnit;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
public class ConfigValue {

    protected static final int DEFAULT_RELOAD_TIME = 300;
    private final ConfigResolver.TypedResolver<String> resolver;
    private final TimeUnit timeUnit;
    private final int reloadAfter;

    public ConfigValue(String configKey) {
        this(configKey, DEFAULT_RELOAD_TIME, TimeUnit.SECONDS);
    }

    private ConfigValue(String configKey, int reloadAfter, TimeUnit timeUnit) {
        this(ConfigResolver.resolve(configKey)
                        .as(String.class)
                        .cacheFor(timeUnit, reloadAfter)
                        .withDefault(null)
                        .evaluateVariables(true)
                        .withCurrentProjectStage(true)
                        .logChanges(true),
                reloadAfter,
                timeUnit
        );
    }

    private ConfigValue(ConfigResolver.TypedResolver<String> resolver, int reloadAfter, TimeUnit timeUnit) {
        this.resolver = resolver;
        this.reloadAfter = reloadAfter;
        this.timeUnit = timeUnit;
    }

    public String configKey() {
        return resolver.getKey();
    }

    public String get() {
        return resolver.getValue();
    }

    public void forceRefresh() {
        resolver.cacheFor(TimeUnit.MILLISECONDS, -1); // Caching deaktivieren
        resolver.getValue(); // refreshen
        resolver.cacheFor(timeUnit, reloadAfter);
    }
}
