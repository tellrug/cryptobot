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

    @Column(name = "ERLEDIGTAM", nullable = true, updatable = true)
    private Date erledigtAm;

    @Column(name = "CRYPTOWAEHRUNG", nullable = false, updatable = false)
    private String cryptoWaehrung;

    @Column(name = "CRYPTOWAEHRUNG_REFERENZ", nullable = false, updatable = false)
    private String referenzCryptoWaehrung;

    @Column(name = "MENGE", nullable = false, updatable = false)
    private BigDecimal menge;

    @Column(name = "PREIS_EINHEIT", nullable = false, updatable = true)
    private BigDecimal preisProEinheit;

    @Column(name = "TRADE_STATUS", nullable = false, updatable = true)
    private String status;

    @Column(name = "PLATTFORM", nullable = false, updatable = true)
    private String tradingplattform;

    @Column(name = "USERID", nullable = true, updatable = true)
    private Long userId;

    @Column(name = "TRADE_TYP", nullable = true, updatable = true)
    private String tradeTyp;

    @Column(name = "TRADEAKTION_FK", nullable = true, updatable = true)
    private Long referenzTradeAktionId;

    @Column(name = "TRADEJOB_FK", nullable = true, updatable = true)
    private Long tradeJobId;

    @Column(name = "TRADEJOBTYP", nullable = true, updatable = true)
    private String tradeJobTyp;

    @Column(name = "CUSTOMERORDERID", nullable = true, updatable = true)
    private String customerOrderId;


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

    public Date getErledigtAm() {
        return erledigtAm;
    }

    public void setErledigtAm(Date erledigtAm) {
        this.erledigtAm = erledigtAm;
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

    public String getTradeTyp() {
        return tradeTyp;
    }

    public void setTradeTyp(String tradeTyp) {
        this.tradeTyp = tradeTyp;
    }

    public Long getReferenzTradeAktionId() {
        return referenzTradeAktionId;
    }

    public void setReferenzTradeAktionId(Long referenzTradeAktionId) {
        this.referenzTradeAktionId = referenzTradeAktionId;
    }

    public Long getTradeJobId() {
        return tradeJobId;
    }

    public void setTradeJobId(Long tradeJobId) {
        this.tradeJobId = tradeJobId;
    }

    public BigDecimal getPreisProEinheit() {
        return preisProEinheit;
    }

    public void setPreisProEinheit(BigDecimal preisProEinheit) {
        this.preisProEinheit = preisProEinheit;
    }

    public String getTradeJobTyp() {
        return tradeJobTyp;
    }

    public void setTradeJobTyp(String tradeJobTyp) {
        this.tradeJobTyp = tradeJobTyp;
    }

    public String getCryptoWaehrung() {
        return cryptoWaehrung;
    }

    public void setCryptoWaehrung(String cryptoWaehrung) {
        this.cryptoWaehrung = cryptoWaehrung;
    }

    public String getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(String customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public String getReferenzCryptoWaehrung() {
        return referenzCryptoWaehrung;
    }

    public void setReferenzCryptoWaehrung(String referenzCryptoWaehrung) {
        this.referenzCryptoWaehrung = referenzCryptoWaehrung;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
