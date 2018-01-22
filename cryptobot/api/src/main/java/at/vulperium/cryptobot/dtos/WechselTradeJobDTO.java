package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.TradeTyp;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 12.01.2018.
 */
public class WechselTradeJobDTO extends AbstractTradeJobDTO implements Serializable {

    //private BigDecimal menge;           //welche tatsaechliche Menge wurde gekauft / nach dem Verkauf auf 0 setzen
    //private BigDecimal kaufwert;        //um diesen Wert wurde tatsaechlich gekauft

    //Kaufen
    private BigDecimal kaufwertGrenze;       //ueber diese Grenze darf nicht gekauft werden
    private BigDecimal mengeReferenzwert;   //wieviel darf von der Referenzwaehrung ausgegeben werden

    //Verkauf
    //private boolean ganzzahligerVerkauf;    //gibt an ob nur ganzze Waehrungseinheiten verkauft werden koennen -> wird nachher in WaehrungDTO aufgenommen
    private BigDecimal minimalZielSatz;     //gibt den minimalen relativen Satz (z.B.: 1.05 -> 5%) an, dererreicht werden soll
    private BigDecimal vorgesehenerVerkaufwert;

    private TradeTyp tradeTyp;
    private LocalDateTime tradeVersuchAm;

    /*
    ABLAUF:

    KAUF_ANGEBOT:
    Sobald kaufwertGrenze unterschritten wurde, werden Einheiten im Wert von mengeReferenzwert gekauft.
    Es wird der kaufwert gesetzt. Es wird die menge gesetzt. Es wird tradeVersuchAm gesetzt. Der Status wird auf TradePruefungKauf gesetzt.

    TRADE_PRUEFUNG_KAUF:
    Es werden offene Orders betrachtet. Ueberpruefen ob relevante offene Order vorhanden ist:
    *Einheit und ReferenzEinheit
    *Menge
    *Kaufwert aus offenen Order vergleichen (KAUF)

    Keine relevante Order vorhanden: Kauf war erfolgreich -->
    Zielwert wird gesetzt,tradeVersuchAm auf null gesetzt, Status umstellen auf BEOBACHTEN, tradeTyp wird auf VERKAUF gesetzt --> TradeAktion speichern
    Relevanter Order vorhanden: Kauf hat nicht funktioniert --> ?????

    VERKAUF:
    Sobald Zielwert ueberschritten wurde, wird die Menge (ganzzahlig oder nicht) verkauft.
    Es wird der vorgesehenerVerkaufwert gesetzt, Es wird tradeVersuchAm gesetzt, der Status wird auf TradePruefungVerkauf gesetzt.

    TRADE_PRUEFUNG_VERKAUF:
    Es werden offene Orders betrachtet. Ueberpruefen ob relevante offene Order vorhanden ist:
    *Einheit und ReferenzEinheit
    *Menge
    *vorgesehenerVerkaufwert aus offenen Order vergleichen (VERKAUF)

    Keine relevante Order vorhanden: Verauf war erfolgreich --> Kaufwert wird auf 0 gesetzt, Zielwert wird auf 0 gesetzt, vorgesehenerVerkaufwert wird auf 0 gesetzt,
    menge wird auf 0 gesetzt, tradeVersuchAm auf null gesetzt, Status umstellen auf BEOBACHTEN, tradeTyp wird auf KAUF gesetzt --> TradeAktion speichern
    Relevanter Order vorhanden: Verkauf hat nicht funktioniert --> ?????
    */

    public BigDecimal getKaufwertGrenze() {
        return kaufwertGrenze;
    }

    public void setKaufwertGrenze(BigDecimal kaufwertGrenze) {
        this.kaufwertGrenze = kaufwertGrenze;
    }

    public BigDecimal getMengeReferenzwert() {
        return mengeReferenzwert;
    }

    public void setMengeReferenzwert(BigDecimal mengeReferenzwert) {
        this.mengeReferenzwert = mengeReferenzwert;
    }

    public BigDecimal getMinimalZielSatz() {
        return minimalZielSatz;
    }

    public void setMinimalZielSatz(BigDecimal minimalZielSatz) {
        this.minimalZielSatz = minimalZielSatz;
    }

    public TradeTyp getTradeTyp() {
        return tradeTyp;
    }

    public void setTradeTyp(TradeTyp tradeTyp) {
        this.tradeTyp = tradeTyp;
    }

    public LocalDateTime getTradeVersuchAm() {
        return tradeVersuchAm;
    }

    public void setTradeVersuchAm(LocalDateTime tradeVersuchAm) {
        this.tradeVersuchAm = tradeVersuchAm;
    }

    public BigDecimal getVorgesehenerVerkaufwert() {
        return vorgesehenerVerkaufwert;
    }

    public void setVorgesehenerVerkaufwert(BigDecimal vorgesehenerVerkaufwert) {
        this.vorgesehenerVerkaufwert = vorgesehenerVerkaufwert;
    }
}
