package at.vulperium.cryptobot.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Created by 02ub0400 on 02.01.2018.
 */
public class DataSourceHelper {

    private final static String[] JNDI_PREFIXES =
            {"",                         // WebSphere
                    "java:openejb/Resource/",   // OpenEJB-embedded
                    "openejb:Resource/",        // TomEE in web modules
                    "java:",                    // TomEE in EE modules
                    "jdbc/"};                   // JBoss

    private DataSourceHelper() {
        // private utility class ct
    }

    public static DataSource getDataSource(String dataSourceName) {
        String[] jndiPrefixes = getKnownContainerJndiPrefixes();
        for (String jndiPrefix : jndiPrefixes) {
            String dataSourceJndiLocation = jndiPrefix + dataSourceName;

            try {
                InitialContext ctx = new InitialContext();
                return (DataSource) ctx.lookup(dataSourceJndiLocation);
            }
            catch (NamingException ne) {
                // continue with next try
            }
        }

        throw new IllegalStateException("Could not find DataSource with name=" + dataSourceName +
                " in the following JNDI locations: " + Arrays.toString(jndiPrefixes));
    }

    public static String[] getKnownContainerJndiPrefixes() {
        //X TODO das ist nur fuer TomEE
        return JNDI_PREFIXES;
    }
}
