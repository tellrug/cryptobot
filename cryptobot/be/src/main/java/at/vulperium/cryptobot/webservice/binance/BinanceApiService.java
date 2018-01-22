package at.vulperium.cryptobot.webservice.binance;

import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Ace on 22.01.2018.
 */
public class BinanceApiService {


    public void pingTest() {

        BinanceApi binanceApi = new BinanceApi();
        try {
            Map<String, BigDecimal> map = binanceApi.pricesMap();
            JsonObject jsonObject = binanceApi.ping();
            System.out.print("Anzahl der aktuellen Kursdaten: " + map.size());
        }
        catch (BinanceApiException e) {
            e.printStackTrace();
        }
    }
}
