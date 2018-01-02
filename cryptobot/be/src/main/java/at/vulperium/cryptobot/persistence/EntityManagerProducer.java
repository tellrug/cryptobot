package at.vulperium.cryptobot.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by Ace on 26.12.2017.
 */
@RequestScoped
public class EntityManagerProducer {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerProducer.class);

    @PersistenceUnit(unitName = "cryptodb")
    private EntityManagerFactory emfCryptoDB;

    @Produces
    @RequestScoped
    @Default
    public EntityManager createEntityManager() {
        return this.emfCryptoDB.createEntityManager();
    }

    public void dispose(@Disposes @Default EntityManager entityManager) {
        if (entityManager == null) {
            logger.warn("CryptoDB EntityManager dispose called with null EntityManager!");
            return;
        }
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
