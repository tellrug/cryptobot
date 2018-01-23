package at.vulperium.cryptobot.services.trades;

import at.vulperium.cryptobot.dtos.AbstractTradeJobDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobReaktion;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.Trend;
import at.vulperium.cryptobot.utils.TradeUtil;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;
import java.math.BigDecimal;

public abstract class AbstractTradeService<T extends AbstractTradeJobDTO> {

    protected  @Inject TradeAktionService tradeAktionService;

    protected void verarbeiteTradeJob(T tradeJobDTO, WSCryptoCoinDTO wsCryptoCoinDTO) {
        Validate.notNull(tradeJobDTO, "tradeJobDTO ist null");
        Validate.notNull(wsCryptoCoinDTO, "wsCryptoCoinDTO ist null");

        //Bestimmen des Trends
        Trend trend = ermittleTrend(tradeJobDTO.getSpitzenwert() != null ? tradeJobDTO.getSpitzenwert() : tradeJobDTO.getKaufwert(), wsCryptoCoinDTO.getPrice());

        //Aktualisieren von Letztwert
        tradeJobDTO.setLetztwert(wsCryptoCoinDTO.getPrice());
        //Setzen von Spitzenwert
        setzeSpitzenWert(tradeJobDTO, wsCryptoCoinDTO.getPrice());

        //Ueberpruefen ob Zielwertueberschritten wurde
        boolean zielErreicht = ermittleZielErreicht(tradeJobDTO, wsCryptoCoinDTO.getPrice());
        TradeJobReaktion tradeJobReaktion = ermittleTradeJobReaktion(tradeJobDTO, zielErreicht, trend);


        if (TradeJobReaktion.FOLGE_AKTION == tradeJobReaktion) {
            fuehreFolgeaktionDurch(tradeJobDTO);
        }

        //Wenn Abgeschlossen oder Fehler dann soll auch auf erledigt gesetzt werden
        if ((TradeStatus.ABGESCHLOSSEN == tradeJobDTO.getTradeStatus() || TradeStatus.FEHLER == tradeJobDTO.getTradeStatus())
                && tradeJobDTO.getErledigtAm() == null) {
            //Am Schluss wird erledigtAm belegt
            tradeJobDTO.setErledigtAm(LocalDateTime.now());
        }

        //Aktualisieren des TradeJobs (Letztwerte)
        aktualisiereTradeJob(tradeJobDTO);
    }


    protected Trend ermittleTrend(BigDecimal alterWert, BigDecimal neuerWert) {
        BigDecimal aenderung = neuerWert.divide(alterWert, BigDecimal.ROUND_HALF_EVEN);

        if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == 1 && (aenderung.subtract(TradeUtil.getBigDecimal(1.0))).compareTo(Trend.getAenderungssatz()) == 1) {
            //wenn aenderung > 1 && (aenderung-1) > Trend.AENDERUNGSSATZ --> Aufwaertstrend
            return Trend.AUFWAERTS;
        }

        if (aenderung.compareTo(TradeUtil.getBigDecimal(1.0)) == -1 && (TradeUtil.getBigDecimal(1.0).subtract(aenderung)).compareTo(Trend.getAenderungssatz()) == 1) {
            //wenn aenderung < 1 && (1-aenderung) > Trend.AENDERUNGSSATZ --> Abwaertstrend
            return Trend.ABWAERTS;
        }

        //sonst Gleichbleibend
        return Trend.KONSTANT;
    }

    protected BigDecimal ermittleOrderWert(T tradeJobDTO) {
        BigDecimal abschlag;
        if (tradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
            abschlag = TradeUtil.getBigDecimal("1.008"); // Abschlag damit schneller erfolgreich gekauft wird
        }
        else if (tradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
            abschlag = TradeUtil.getBigDecimal("0.99"); // Abschlag damit schneller erfolgreich verkauft wird
        }
        else {
            throw new IllegalStateException("Fehler bei der Ermittlung von TradeTyp bei Tradejob=" + tradeJobDTO.getId());
        }

        return tradeJobDTO.getLetztwert().multiply(abschlag);
    }

    protected BigDecimal ermittleRelevanteTradeMenge(BigDecimal menge, boolean ganzzahligeMenge) {
        if (ganzzahligeMenge) {
            return menge.setScale(0, BigDecimal.ROUND_FLOOR).setScale(TradeUtil.SCALE, BigDecimal.ROUND_FLOOR);
        }
        return menge;
    }

    protected TradeAktionDTO erstelleTradeAktionVerkauf(T tradeJobDTO) {
        //Es wird eine TradeAktion erstellt: Verkauf

        TradeAktionDTO tradeAktionDTO = new TradeAktionDTO();
        tradeAktionDTO.setTradeTyp(TradeTyp.VERKAUF);
        tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_VERKAUF);
        tradeAktionDTO.setErstelltAm(LocalDateTime.now());
        tradeAktionDTO.setTradingPlattform(tradeJobDTO.getTradingPlattform());

        tradeAktionDTO.setTradeJobId(tradeJobDTO.getId());
        tradeAktionDTO.setTradeJobTyp(tradeJobDTO.getTradeJobTyp());
        //tradeAktionDTO.setUserId(); //TODO technischen User setzen

        tradeAktionDTO.setMenge(ermittleRelevanteTradeMenge(tradeJobDTO.getMenge(), tradeJobDTO.isGanzZahlig()));
        tradeAktionDTO.setCryptoWaehrung(tradeJobDTO.getCryptoWaehrung());
        tradeAktionDTO.setCryptoWaehrungReferenz(tradeJobDTO.getCryptoWaehrungReferenz());

        //Ermitteln des Preises
        tradeAktionDTO.setPreisProEinheit(ermittleOrderWert(tradeJobDTO));

        //Speichern der TradeAktion
        Long tradeAktionId = tradeAktionService.speichereTradeAktion(tradeAktionDTO);
        return tradeAktionDTO;
    }

    private void setzeSpitzenWert(T tradeJobDTO, BigDecimal aktuellerWert) {

        if (tradeJobDTO.getSpitzenwert() == null) {
            tradeJobDTO.setSpitzenwert(aktuellerWert);
            return;
        }

        if (tradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.KAUF) {
            if (aktuellerWert.compareTo(tradeJobDTO.getSpitzenwert()) == -1) {
                tradeJobDTO.setSpitzenwert(aktuellerWert);
            }
        }
        else if (tradeJobDTO.getTradeAktionEnum().getTradeTyp() == TradeTyp.VERKAUF) {
            if (aktuellerWert.compareTo(tradeJobDTO.getSpitzenwert()) == 1) {
                tradeJobDTO.setSpitzenwert(aktuellerWert);
            }
        }
        else {
            throw new IllegalStateException("Fuer den TradeJob mit tradejobId=" + tradeJobDTO.getId() + "konnte kein TradeTyp ermittelt werden!");
        }
    }

    protected abstract boolean ermittleZielErreicht(T tradeJobDTO, BigDecimal aktuellerKurs);

    protected abstract TradeJobReaktion ermittleTradeJobReaktion(T tradeJobDTO, boolean zielErreicht, Trend trend);

    protected abstract void aktualisiereTradeJob(T tradeJobDTO);

    protected abstract TradeStatus fuehreFolgeaktionDurch(T tradeJobDTO);
}
