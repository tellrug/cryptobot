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
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 02ub0400 on 10.01.2018.
 */
@Entity
@Table(name = "TRADEAKTION")
@NamedQueries({
        @NamedQuery(name = TradeAktion.QRY_FIND_ALL,
                query = "SELECT t FROM TradeAktion t ")
})
public class TradeAktion extends BaseEntity<Long> {

    public static final String QRY_FIND_ALL = "tradeAktion.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRADEAKTION_SQ")
    @SequenceGenerator(name = "TRADEAKTION_SQ", sequenceName = "TRADEAKTION_SQ", allocationSize = 1, initialValue = 200000)
    @Column(name = "TRADEAKTION_PK", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ERSTELLTAM", nullable = false, updatable = false)
    private Date erstelltAm;

    @Column(name = "VON_WAEHRUNG_FK", nullable = false, updatable = false)
    private String vonWaehrung;

    @Column(name = "VON_MENGE", nullable = false, updatable = true)
    private BigDecimal vonMenge;

    @Column(name = "ZU_WAEHRUNG_FK", nullable = false, updatable = false)
    private String zuWaehrung;

    @Column(name = "ZU_MENGE", nullable = false, updatable = true)
    private BigDecimal zuMenge;

    @Column(name = "TRADE_STATUS", nullable = false, updatable = true)
    private String status;

    @Column(name = "PLATTFORM", nullable = false, updatable = true)
    private String tradingplattform;

    @Column(name = "USERID", nullable = true, updatable = true)
    private Long userId;


    @Override
    public Long getId() {
        return id;
    }

    public Date getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(Date erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradingplattform() {
        return tradingplattform;
    }

    public void setTradingplattform(String tradingplattform) {
        this.tradingplattform = tradingplattform;
    }

    public String getVonWaehrung() {
        return vonWaehrung;
    }

    public void setVonWaehrung(String vonWaehrung) {
        this.vonWaehrung = vonWaehrung;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getVonMenge() {
        return vonMenge;
    }

    public void setVonMenge(BigDecimal vonMenge) {
        this.vonMenge = vonMenge;
    }

    public String getZuWaehrung() {
        return zuWaehrung;
    }

    public void setZuWaehrung(String zuWaehrung) {
        this.zuWaehrung = zuWaehrung;
    }

    public BigDecimal getZuMenge() {
        return zuMenge;
    }

    public void setZuMenge(BigDecimal zuMenge) {
        this.zuMenge = zuMenge;
    }
}
