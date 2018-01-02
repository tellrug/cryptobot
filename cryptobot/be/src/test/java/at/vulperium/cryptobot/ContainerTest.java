package at.vulperium.cryptobot;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.config.PropertyLoader;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.ExceptionUtils;
import org.apache.deltaspike.core.util.ProjectStageProducer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ContainerTest {

    protected static CdiContainer cdiContainer;
    protected static int containerRefCount = 0;

    private static final String SCAN_EXCLUDE_FILE = "db/scanexcludes.list";
    private static final String OPENEJB_CLASSPATH_EXCLUDE_OPTION = "openejb.deployments.classpath.exclude";

    @BeforeMethod
    public final void setUp() throws Exception {
        containerRefCount++;

        if (cdiContainer == null) {
            ProjectStageProducer.setProjectStage(ProjectStage.UnitTest);

            cdiContainer = CdiContainerLoader.getCdiContainer();

            //Laden der dbProperties
            Properties dbProperties = PropertyLoader.getProperties("db/db.properties");
            if (dbProperties != null) {
                //fuer Windows muss die Verwendung des relativen Pfades aktiviert werden, damit wir /tmp/ verwenden koennen
                if (SystemUtils.IS_OS_WINDOWS) {
                    dbProperties.setProperty("h2.implicitRelativePath", "true");
                }

                String[] scanExclusionList = getScanExclusions();
                String scanExclusions = ".*\\/(" + StringUtils.join(scanExclusionList, "|") + ").*";
                //dbProperties.put(OPENEJB_CLASSPATH_EXCLUDE_OPTION, scanExclusions);
            }
            cdiContainer.boot(dbProperties);

            //cdiContainer.boot();
            cdiContainer.getContextControl().startContexts();
        }
        else {
            // clean the Instances by restarting the contexts
            cdiContainer.getContextControl().stopContexts();
            cdiContainer.getContextControl().startContexts();
        }
    }


    @AfterMethod
    public final void tearDown() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.getContextControl().stopContext(RequestScoped.class);
            cdiContainer.getContextControl().startContext(RequestScoped.class);
            containerRefCount--;
        }
    }


    @BeforeClass
    public final void beforeClass() throws Exception {
        setUp();
        cdiContainer.getContextControl().stopContext(RequestScoped.class);
        cdiContainer.getContextControl().startContext(RequestScoped.class);

        // perform injection into the very own test class
        BeanManager beanManager = cdiContainer.getBeanManager();

        CreationalContext creationalContext = beanManager.createCreationalContext(null);

        AnnotatedType annotatedType = beanManager.createAnnotatedType(this.getClass());
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(this, creationalContext);
    }

    @AfterSuite
    public synchronized void shutdownContainer() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.shutdown();
            cdiContainer = null;
        }
    }

    public final void cleanInstances() {
        cdiContainer.getContextControl().stopContext(RequestScoped.class);
        cdiContainer.getContextControl().startContext(RequestScoped.class);
        cdiContainer.getContextControl().stopContext(SessionScoped.class);
        cdiContainer.getContextControl().startContext(SessionScoped.class);
    }

    private String[] getScanExclusions() {
        try (BufferedReader reader = new BufferedReader(reader(SCAN_EXCLUDE_FILE))) {
            List<String> excludes = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                excludes.add(line);
            }

            return excludes.toArray(new String[excludes.size()]);
        }
        catch (IOException e) {
            throw ExceptionUtils.throwAsRuntimeException(e);
        }
    }

    private InputStreamReader reader(String name) {
        return new InputStreamReader(ClassUtils.getClassLoader(null).getResourceAsStream(name));
    }

}
