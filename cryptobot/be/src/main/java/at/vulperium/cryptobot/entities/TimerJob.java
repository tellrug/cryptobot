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
import java.util.Date;

/**
 * Created by 02ub0400 on 15.01.2018.
 */
@Entity
@Table(name = "TIMERJOB")
@NamedQueries({
        @NamedQuery(name = TimerJob.QRY_FIND_ALL,
                query = "SELECT t FROM TimerJob t ")
})
public class TimerJob extends BaseEntity<Long> {

    public static final String QRY_FIND_ALL = "timerjob.findAll";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIMERJOB_SQ")
    @SequenceGenerator(name = "TIMERJOB_SQ", sequenceName = "TIMERJOB_SQ", allocationSize = 1, initialValue = 200000)
    @Column(name = "TIMERJOB_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ERSTELLTAM", nullable = false, updatable = false)
    private Date erstelltAm;

    @Column(name = "NAECHSTE_DURCHFUEHRUNG", nullable = true, updatable = true)
    private Date naechsteDurchfuehrungAm;

    @Column(name = "LETZTE_DURCHFUEHRUNG", nullable = true, updatable = true)
    private Date letzteDurchfuehrungAm;

    @Column(name = "BEZEICHNUNG", nullable = false, updatable = false)
    private String bezeichnung;


    @Override
    public Long getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Date getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(Date erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Date getLetzteDurchfuehrungAm() {
        return letzteDurchfuehrungAm;
    }

    public void setLetzteDurchfuehrungAm(Date letzteDurchfuehrungAm) {
        this.letzteDurchfuehrungAm = letzteDurchfuehrungAm;
    }

    public Date getNaechsteDurchfuehrungAm() {
        return naechsteDurchfuehrungAm;
    }

    public void setNaechsteDurchfuehrungAm(Date naechsteDurchfuehrungAm) {
        this.naechsteDurchfuehrungAm = naechsteDurchfuehrungAm;
    }
}
