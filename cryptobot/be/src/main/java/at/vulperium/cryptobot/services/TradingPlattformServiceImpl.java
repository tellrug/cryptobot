package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.OrderDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.apache.commons.lang.Validate;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
@ApplicationScoped
public class TradingPlattformServiceImpl implements TradingPlattformService {

    private @Inject
    BinanceClientService binanceClientService;

    private Map<TradingPlattform, TradingInfos> tradingInfosMap = new HashMap<>();

    @Override
    public boolean ueberpruefeErreichbarkeit(TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        return tradingPlattformClient.ping();
    }

    @Override
    public Map<String, WSCryptoCoinDTO> holeWSCryptoCoinMap(TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        TradingInfos tradingInfos = holeTradingInfos(tradingPlattform);
        return tradingInfos.getWsCryptoCoinDTOMap();
    }

    @Override
    public HoldingDTO ermittleHoldingInformationen(TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        TradingInfos tradingInfos = holeTradingInfos(tradingPlattform);
        return tradingInfos.getHoldingDTO();
    }

    @Override
    public OrderDTO holeOrderDTOzuTradeAktion(TradeAktionDTO tradeAktionDTO) {

        if (tradeAktionDTO.getTradingPlattform() != TradingPlattform.BINANCE) {
            throw new IllegalStateException("Fehler bei Abfrage von Order zu TradeAktion - " +
                    "Momentan wird nur BINANCE unterstuetzt. TradeAktionId: " + tradeAktionDTO.getId());
        }
        String symbolPair = tradeAktionDTO.getCryptoWaehrung() + tradeAktionDTO.getCryptoWaehrungReferenz();
        OrderDTO orderDTO = binanceClientService.holeOrderInfosByClientOrderId(symbolPair, tradeAktionDTO.getCustomerOrderId());
        orderDTO.setTradeAktionId(tradeAktionDTO.getId());
        return orderDTO;
    }

    @Override
    public boolean storniereOrder(TradeAktionDTO tradeAktionDTO) {
        if (tradeAktionDTO.getTradingPlattform() != TradingPlattform.BINANCE) {
            throw new IllegalStateException("Fehler bei Abfrage von  storniere Order zu TradeAktion - " +
                    "Momentan wird nur BINANCE unterstuetzt. TradeAktionId: " + tradeAktionDTO.getId());
        }
        return binanceClientService.storniereOrder(tradeAktionDTO);
    }

    @Override
    public boolean erstelleOrder(TradeAktionDTO tradeAktionDTO) {
        if (tradeAktionDTO.getTradingPlattform() != TradingPlattform.BINANCE) {
            throw new IllegalStateException("Fehler bei Abfrage von erstelle Order zu TradeAktion - " +
                    "Momentan wird nur BINANCE unterstuetzt. TradeAktionId: " + tradeAktionDTO.getId());
        }

        return binanceClientService.erstelleOrder(tradeAktionDTO);
    }

    private Map<String, WSCryptoCoinDTO> ladeWSCryptoCoinMapPerWS(TradingPlattform tradingPlattform) {
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        return tradingPlattformClient.ermittleLetztePreiseMap();
    }

    private HoldingDTO ladeHoldingOrderInformationenPerWS(TradingPlattform tradingPlattform) {
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        return tradingPlattformClient.ermittleHoldingInformationen();
    }

    private TradingPlattformClient ermittleClient(TradingPlattform tradingPlattform) {
        if (TradingPlattform.BINANCE == tradingPlattform) {
            return binanceClientService;
        } else {
            throw new IllegalArgumentException("Abfragen von TradingPlattform=" + tradingPlattform + " werden derzeit nicht unterstuetzt.");
        }
    }

    private TradingInfos holeTradingInfos(TradingPlattform tradingPlattform) {
        synchronized (this) {
            if (tradingInfosMap.get(tradingPlattform) == null ||
                    tradingInfosMap.get(tradingPlattform).getLetzteAktualisierung() == null ||
                    tradingInfosMap.get(tradingPlattform).getLetzteAktualisierung().plus(Duration.standardMinutes(1L)).isBefore(LocalDateTime.now())) {

                //Aktualisierung der Daten per WS
                Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = ladeWSCryptoCoinMapPerWS(tradingPlattform);
                HoldingDTO holdingDTO = ladeHoldingOrderInformationenPerWS(tradingPlattform);

                TradingInfos tradingInfos = new TradingInfos(LocalDateTime.now(), tradingPlattform, wsCryptoCoinDTOMap, holdingDTO);
                tradingInfosMap.put(tradingPlattform, tradingInfos);
            }
        }

        return tradingInfosMap.get(tradingPlattform);
    }

    private final static class TradingInfos implements Serializable {

        private TradingPlattform tradingPlattform;
        private LocalDateTime letzteAktualisierung;
        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap;
        private HoldingDTO holdingDTO;

        public TradingInfos(LocalDateTime letzteAktualisierung, TradingPlattform tradingPlattform,
                            Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, HoldingDTO holdingDTO) {
            this.holdingDTO = holdingDTO;
            this.letzteAktualisierung = letzteAktualisierung;
            this.tradingPlattform = tradingPlattform;
            this.wsCryptoCoinDTOMap = wsCryptoCoinDTOMap;
        }

        public HoldingDTO getHoldingDTO() {
            return holdingDTO;
        }

        public TradingPlattform getTradingPlattform() {
            return tradingPlattform;
        }

        public Map<String, WSCryptoCoinDTO> getWsCryptoCoinDTOMap() {
            return wsCryptoCoinDTOMap;
        }

        public LocalDateTime getLetzteAktualisierung() {
            return letzteAktualisierung;
        }
    }
}
