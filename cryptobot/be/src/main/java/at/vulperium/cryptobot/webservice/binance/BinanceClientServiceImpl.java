package at.vulperium.cryptobot.webservice.binance;


import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.HoldingOrderDTO;
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
import com.webcerebrium.binance.datatype.BinanceOrderStatus;
import com.webcerebrium.binance.datatype.BinanceSymbol;
import com.webcerebrium.binance.datatype.BinanceWalletAsset;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class BinanceClientServiceImpl implements BinanceClientService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceClientServiceImpl.class);

    private static final ConfigValue testModus = new ConfigValue("testModus");
    private static final ConfigValue requestURL = new ConfigValue("binanceURL");
    private static final ConfigValue pingReq = new ConfigValue("binancePing");
    private static final ConfigValue letztePreiseReq = new ConfigValue("binanceLetztePreise");

    private @Inject BinanceApiTransformer transformer;

    private Mapper mapper;
    private final BinanceApi binanceApi = new BinanceApi();

    @PostConstruct
    private void init() {
        MapperBuilder mapperBuilder = new MapperBuilder().setAccessModeName("both");
        mapper = mapperBuilder.build();
    }

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
            Response response = starteWebServiceCall(requestURL.get(), pingReq.get());
            Response.StatusType status = response.getStatusInfo().toEnum();
            responseText = response.readEntity(String.class);
            logger.info("WebService-Aufruf 'ping'. Antwort={}, Status={}", responseText, status.toEnum().name());
            return status.toEnum().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
    }

    @Override
    public List<WSCryptoCoinDTO> ermittleLetztePreise() {
        logger.info("Binance WebService - letztePreise ...");
        String responseText;
        if (ConfigUtil.toBoolean(testModus)) {
            responseText = "[{\"symbol\": \"LTCBTC\",\"price\": \"4.00000200\"},{\"symbol\": \"ETHBTC\",\"price\": \"0.07946600\"}," +
                    "{\"symbol\": \"VK1BTC\",\"price\": \"0.0081\"}," +
                    "{\"symbol\": \"VK2BTC\",\"price\": \"0.010\"}," +
                    "{\"symbol\": \"WJCKBTC\",\"price\": \"0.00001\"}," +
                    "{\"symbol\": \"WJCVBTC\",\"price\": \"0.00002\"}," +
                    "{\"symbol\": \"K1BTC\",\"price\": \"0.0029\"}]";
            logger.warn("Test-Modus ist aktiv: Kein WebService-Aufruf. Antwort: {}", responseText);
        }
        else {
            Response response = starteWebServiceCall(requestURL.get(), letztePreiseReq.get());
            Response.StatusType status = response.getStatusInfo().toEnum();
            responseText = response.readEntity(String.class);
            logger.info("WebService-Aufruf 'letztePreise'. Antwort={}, Status={}", responseText, status.toEnum().name());
        }

        //Umwandeln in ein JSONArray
        JSONArray jsonArray = new JSONArray(responseText);

        //Mappen in DTOS
        List<WSCryptoCoinDTO> wsCryptoCoinDTOList = new ArrayList<>();
        for (int n = 0; n < jsonArray.length(); n++) {
            JSONObject jsonObject = jsonArray.getJSONObject(n);
            WSCryptoCoinDTO wsCryptoCoinDTO = fromJSON(jsonObject.toString(), WSCryptoCoinDTO.class);
            wsCryptoCoinDTOList.add(wsCryptoCoinDTO);
        }

        logger.info("Anzahl der ausgelesenen Informationen - BINANCE(letztePreise): {}", wsCryptoCoinDTOList.size());
        return wsCryptoCoinDTOList;
    }

    public void ermittleInformationenZuSymbol(String symbolPair) {
        String requestUrl = "https://api.binance.com/api/v1";

        String responseText;
        String methode = "/ticker/24hr";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(requestUrl);
        Response response = webTarget.path(methode).queryParam("symbol", "symbolPair").request(MediaType.TEXT_PLAIN).get();
        Response.StatusType status = response.getStatusInfo().toEnum();
        responseText = response.readEntity(String.class);
        logger.info("WebService-Aufruf 'letztePreise'. Antwort={}, Status={}", responseText, status.toEnum().name());
    }

    @Override
    public HoldingOrderDTO ermittleHoldingOrderInformationen() {

        HoldingOrderDTO holdingOrderDTO = new HoldingOrderDTO();
        holdingOrderDTO.setTradingPlattform(TradingPlattform.BINANCE);

        if (ConfigUtil.toBoolean(testModus)) {
            holdingOrderDTO.getHoldingMap().put("LTC", TradeUtil.getBigDecimal(100));
            holdingOrderDTO.getHoldingMap().put("BTC", TradeUtil.getBigDecimal(0.03));
            holdingOrderDTO.getHoldingMap().put("WJCV", TradeUtil.getBigDecimal(2000));
        }
        else {
            //TODO
        }
        return holdingOrderDTO;
    }

    private Response starteWebServiceCall(String requestUrl, String methode) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(requestUrl);
        Response response = webTarget.path(methode).queryParam("symbol", "LTCBTC").request(MediaType.TEXT_PLAIN).get();
        return response;
    }

    private <T> T fromJSON(String s, Class<T> clazz) {
        return mapper.readObject(s, clazz);
    }


    /**
     * Holt Kurs-Informationen per WS
     * ES WIRD KEIN KEY BENOETIGT
     */
    private Map<String, WSCryptoCoinDTO> ermittleKursMap() {
        logger.info("BinanceWS - Holen der aktuellen Kursdaten...");

        //Es wird kein Key benoetigt
        Map<String, BigDecimal> kursMap;
        try {
            kursMap = getBinanceApi().pricesMap();
        }
        catch (BinanceApiException e) {
            logger.error("Fehler bei Abfrage von BinanceWS-pricesMap: ", e);
            throw new RuntimeException(e);
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

    /**
     * Holt alle offenen Trades
     * ES WIRD EIN API KEY BENOETIGT
     */
    public void holeOffeneOrderBySymbolPair(String symbolPair) {
        logger.info("BinanceWS - Holen der offenen Orders fuer SymbolPair={} ...", symbolPair);

        List<BinanceOrder> binanceOrderList;
        try {
            binanceOrderList = getBinanceApi().openOrders(BinanceSymbol.valueOf(symbolPair));
        }
        catch (BinanceApiException e) {
            logger.error("Fehler bei Abfrage von BinanceWS-holeOffeneOrderBySymbolPair: ", e);
            throw new RuntimeException(e);
        }

        if (CollectionUtils.isEmpty(binanceOrderList)) {
            return;
        }

        Set<String> customerOrderIdSet = new HashSet<>();
        for (BinanceOrder binanceOrder : binanceOrderList) {
            BinanceOrderStatus binanceOrderStatus = binanceOrder.getStatus();
            customerOrderIdSet.add(binanceOrder.getClientOrderId());
        }

        //Rueckliefern der CustomerId
        //Zuordnen zu TradeAktionen
    }

    private boolean storniereOrder(String symbolPair, String clientOrderId) {
        Validate.notNull(symbolPair, "symbolPair ist null.");
        Validate.notNull(clientOrderId, "clientOrderId ist null.");

        logger.info("BinanceWS - Storniere Order von SymbolPair={} mit clientOrderId={} ...", symbolPair, clientOrderId);
        try {
            JsonObject jsonObject = getBinanceApi().deleteOrderByOrigClientId(BinanceSymbol.valueOf(symbolPair), clientOrderId);
        }
        catch (BinanceApiException e) {
            logger.error("Fehler bei Abfrage von BinanceWS-storniereOrder: ", e);
            throw new RuntimeException(e);
        }

        //TODO Antwort checken?

        return true;
    }

    private boolean erstelleOrder(TradeAktionDTO tradeAktionDTO) {
        logger.info("BinanceWS - Erstellen von Order fuer tradeAktionId={} ...", tradeAktionDTO.getId());

        //Erstellen der Order
        BinanceOrderPlacement binanceOrderPlacement = transformer.transformTradeAktionDTO(tradeAktionDTO);
        tradeAktionDTO.setCustomerOrderId(binanceOrderPlacement.getNewClientOrderId());
        try {
            JsonObject jsonObject = getBinanceApi().createOrder(binanceOrderPlacement);
        }
        catch (BinanceApiException e) {
            logger.error("Fehler bei Aufruf von BinanceWS-erstelleOrder: ", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    public void ermittleAktuelleHoldings() {
        logger.info("BinanceWS - Ermitteln der aktuellen Hldings...");

        Map<String, BinanceWalletAsset> balancesMap;
        try {
            balancesMap = getBinanceApi().balancesMap();
        }
        catch (BinanceApiException e) {
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
