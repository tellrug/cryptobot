package at.vulperium.cryptobot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by 02ub0400 on 15.01.2018.
 */
@Entity
@Table(name = "SIMPEL_TRADEJOB")
@NamedQueries({
        @NamedQuery(name = SimpelTradeJob.QRY_FIND_ALL,
                query = "SELECT t FROM SimpelTradeJob t ")
})
public class SimpelTradeJob extends AbstractTradeJob {

    public static final String QRY_FIND_ALL = "simpeltradeJob.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIMPEL_TRADEJOB_SQ")
    @SequenceGenerator(name = "SIMPEL_TRADEJOB_SQ", sequenceName = "SIMPEL_TRADEJOB_SQ", allocationSize = 1, initialValue = 200000)
    @Column(name = "SIMPEL_TRADEJOB_PK", nullable = false, updatable = false)
    private Long id;


    @Override
    public Long getId() {
        return id;
    }
}
