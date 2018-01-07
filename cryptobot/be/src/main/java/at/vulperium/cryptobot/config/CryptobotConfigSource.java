package at.vulperium.cryptobot.config;

import org.apache.deltaspike.core.api.config.Source;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.deltaspike.core.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
@Source
public class CryptobotConfigSource implements ConfigSource {

    private static final Logger logger = LoggerFactory.getLogger(CryptobotConfigSource.class);
    private static final int ORDINAL = 150;
    private static final String SELECT_PROPERTY_STMT = "SELECT KEY, VALUE FROM CRYPTOBOT_CONFIG ";

    private Map<String, String> properties = Collections.emptyMap();
    private volatile long reloadAfter = -1;

    @Override
    public String getConfigName() {
        return "DB-properties";
    }

    @Override
    public int getOrdinal() {
        return ORDINAL;
    }

    @Override
    public Map<String, String> getProperties() {
        return getAllProperties();
    }

    @Override
    public String getPropertyValue(String key) {
        return getAllProperties().get(key);
    }

    @Override
    public boolean isScannable() {
        return true;
    }

    private Map<String, String> getAllProperties() {
        long now = System.currentTimeMillis();
        if (now > reloadAfter) {
            synchronized (this) {
                if (now > reloadAfter) {
                    Map<String, String> configProperties = getConfigProperties();
                    properties = Collections.unmodifiableMap(configProperties);
                    logger.info("Reloading config from the Properties table. item count={}", properties.size());
                    System.out.println("Reloading config from the Properties table. item count=" + properties.size());
                    reloadAfter = now + TimeUnit.MINUTES.toMillis(30); // alle 30 sekunden neu von der DB laden
                }
            }
        }

        return properties;
    }


    private Map<String, String> getConfigProperties() {
            Connection connection = null;
            Map<String, String> properties = new HashMap<>();
            try {
                connection = DataSourceHelper.getDataSource("cryptodb").getConnection();
                PreparedStatement prepStatement = connection.prepareStatement(SELECT_PROPERTY_STMT);

                ResultSet resultSet = prepStatement.executeQuery();
                while (resultSet.next()) {
                    String key = resultSet.getString(1);
                    String value = resultSet.getString(2);
                    properties.put(key, value);
                }
            }
            catch (Exception e) {
                ExceptionUtils.throwAsRuntimeException(e);
            }
            finally {
                if (connection != null) {
                    try {
                        connection.close();
                    }
                    catch (SQLException e) {
                        // ignore
                    }
                }
            }

        return properties;
    }
}
