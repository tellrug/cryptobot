package at.vulperium.cryptobot.webservice.binance;


import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.OrderDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.services.BinanceClientService;
import at.vulperium.cryptobot.utils.ConfigUtil;
import at.vulperium.cryptobot.utils.TradeUtil;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceOrder;
import com.webcerebrium.binance.datatype.BinanceOrderPlacement;
import com.webcerebrium.binance.datatype.BinanceSymbol;
import com.webcerebrium.binance.datatype.BinanceWalletAsset;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class BinanceClientServiceImpl implements BinanceClientService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceClientServiceImpl.class);

    private static final ConfigValue testModus = new ConfigValue("testModus");

    private @Inject BinanceApiTransformer transformer;
    private final BinanceApi binanceApi = new BinanceApi();

    @Override
    public boolean ping() {
        String responseText;
        logger.info("Binance WebService - ping ...");
        if (ConfigUtil.toBoolean(testModus)) {
            responseText = "TEST-PING";
            logger.warn("Test-Modus ist aktiv: Kein WebService-Aufruf. Antwort: {}", responseText);
            return true;
        }
        else {
            try {
                getBinanceApi().ping();
            } catch (BinanceApiException e) {
                logger.error("Fehler bei Abfrage von BinanceWS-ping: ", e);
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public Map<String, WSCryptoCoinDTO> ermittleLetztePreiseMap() {
        logger.info("BinanceWS - Holen der aktuellen Kursdaten...");

        //Es wird kein Key benoetigt
        Map<String, BigDecimal> kursMap;
        if (ConfigUtil.toBoolean(testModus)) {
            kursMap = new HashMap<>();
            kursMap.put("LTCBTC", TradeUtil.getBigDecimal("4.00000200"));
            kursMap.put("ETHBTC", TradeUtil.getBigDecimal("0.07946600"));
            kursMap.put("VK1BTC", TradeUtil.getBigDecimal("0.0081"));
            kursMap.put("VK2BTC", TradeUtil.getBigDecimal("0.010"));
            kursMap.put("WJCKBTC", TradeUtil.getBigDecimal("0.00001"));
            kursMap.put("WJCVBTC", TradeUtil.getBigDecimal("0.00002"));
            kursMap.put("K1BTC", TradeUtil.getBigDecimal("0.0029"));
        }
        else {
            try {
                kursMap = getBinanceApi().pricesMap();
            } catch (BinanceApiException e) {
                logger.error("Fehler bei Abfrage von BinanceWS-pricesMap: ", e);
                throw new RuntimeException(e);
            }
        }

        if (kursMap == null) {
            return null;
        }

        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = new HashMap<>();
        for (String symbol : kursMap.keySet()) {
            WSCryptoCoinDTO wsCryptoCoinDTO = new WSCryptoCoinDTO();
            wsCryptoCoinDTO.setSymbol(symbol);
            wsCryptoCoinDTO.setPrice(kursMap.get(symbol));

            wsCryptoCoinDTOMap.put(symbol, wsCryptoCoinDTO);
        }

        return wsCryptoCoinDTOMap;
    }


    @Override
    public HoldingDTO ermittleHoldingInformationen() {
        HoldingDTO holdingDTO;
        if (ConfigUtil.toBoolean(testModus)) {
            holdingDTO= new HoldingDTO();
            holdingDTO.setTradingPlattform(TradingPlattform.BINANCE);
            holdingDTO.getHoldingMap().put("LTC", TradeUtil.getBigDecimal(100));
            holdingDTO.getHoldingMap().put("BTC", TradeUtil.getBigDecimal(0.03));
            holdingDTO.getHoldingMap().put("WJCV", TradeUtil.getBigDecimal(2000));
        } else {
            holdingDTO = ermittleAktuelleHoldings();
        }
        return holdingDTO;
    }


    @Override
    public OrderDTO holeOrderInfosByClientOrderId(String symbolPair, String clientOrderId) {
        logger.info("BinanceWS - Holen der offenen Orders fuer SymbolPair={} und customerOrderId={}...", symbolPair, clientOrderId);

        List<BinanceOrder> binanceOrderList;
        BinanceOrder binanceOrder;
        try {
            binanceOrder = getBinanceApi().getOrderByOrigClientId(BinanceSymbol.valueOf(symbolPair), clientOrderId);
        } catch (BinanceApiException e) {
            logger.error("Fehler bei Abfrage von BinanceWS-holeOrderInfosByClientOrderId: ", e);
            throw new RuntimeException(e);
        }

        if (binanceOrder == null) {
            //Entsprechende Order wurde nicht gefunden
            logger.warn("Order zu SymbolPair={} und clientOrderId={} konnte nicht gefunden werden!", symbolPair, clientOrderId);
        }

        OrderDTO orderDTO = transformer.transformBinanceOrder(binanceOrder);
        return orderDTO;
    }

    @Override
    public boolean storniereOrder(TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");

        String symbolPair = tradeAktionDTO.getCryptoWaehrung() + tradeAktionDTO.getCryptoWaehrungReferenz();
        logger.info("BinanceWS - Storniere Order von SymbolPair={} mit clientOrderId={} ...", symbolPair, tradeAktionDTO.getCustomerOrderId());
        try {
            JsonObject jsonObject =
                    getBinanceApi().deleteOrderByOrigClientId(BinanceSymbol.valueOf(symbolPair), tradeAktionDTO.getCustomerOrderId());
        } catch (BinanceApiException e) {
            logger.error("Fehler bei Abfrage von BinanceWS-storniereOrder: ", e);
            throw new RuntimeException(e);
        }

        //TODO Antwort checken?

        return true;
    }

    @Override
    public boolean erstelleOrder(TradeAktionDTO tradeAktionDTO) {
        Validate.notNull(tradeAktionDTO, "tradeAktionDTO ist null.");
        logger.info("BinanceWS - Erstellen von Order fuer tradeAktionId={} ...", tradeAktionDTO.getId());

        //Erstellen der Order
        BinanceOrderPlacement binanceOrderPlacement = transformer.transformTradeAktionDTO(tradeAktionDTO);
        tradeAktionDTO.setCustomerOrderId(binanceOrderPlacement.getNewClientOrderId());
        try {
            JsonObject jsonObject = getBinanceApi().createOrder(binanceOrderPlacement);
        } catch (BinanceApiException e) {
            logger.error("Fehler bei Aufruf von BinanceWS-erstelleOrder: ", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    private HoldingDTO ermittleAktuelleHoldings() {
        logger.info("BinanceWS - Ermitteln der aktuellen Hldings...");

        Map<String, BinanceWalletAsset> balancesMap;
        try {
            balancesMap = getBinanceApi().balancesMap();
        } catch (BinanceApiException e) {
            logger.error("Fehler bei Abfrage von BinanceWS-ermittleAktuelleHoldings: ", e);
            throw new RuntimeException(e);
        }

        BigDecimal nullValue = TradeUtil.getBigDecimal(0).setScale(8, RoundingMode.HALF_DOWN);
        Set<String> deletSymbolSet = new HashSet<>();
        for (String symbol : balancesMap.keySet()) {
            if (balancesMap.get(symbol).getFree().equals(nullValue)) {
                deletSymbolSet.add(symbol);
            }
        }

        for (String symbol : deletSymbolSet) {
            balancesMap.remove(symbol);
        }

        logger.info("Anzahl der aktuellen Holdings: {}", balancesMap.size());

        //Transformieren
        return transformer.transformBalanceMap(balancesMap);
    }

    private synchronized BinanceApi getBinanceApi() {
        //TODO als config speichern
        String apiKey = "";
        String secretKey = "";
        binanceApi.setApiKey(apiKey);
        binanceApi.setSecretKey(secretKey);
        return binanceApi;
    }
}
