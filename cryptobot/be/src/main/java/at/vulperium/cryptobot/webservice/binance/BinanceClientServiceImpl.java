package at.vulperium.cryptobot.webservice.binance;


import at.vulperium.cryptobot.config.ConfigValue;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.services.BinanceClientService;
import at.vulperium.cryptobot.utils.ConfigUtil;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BinanceClientServiceImpl implements BinanceClientService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceClientServiceImpl.class);

    private static final ConfigValue testModus = new ConfigValue("testModus");
    private static final ConfigValue requestURL = new ConfigValue("binanceURL");
    private static final ConfigValue pingReq = new ConfigValue("binancePing");
    private static final ConfigValue letztePreiseReq = new ConfigValue("binanceLetztePreise");

    private Mapper mapper;

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
                    "{\"symbol\": \"K1BTC\",\"price\": \"0.0039\"}]";
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


    private Response starteWebServiceCall(String requestUrl, String methode) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(requestUrl);
        Response response = webTarget.path(methode).request(MediaType.TEXT_PLAIN).get();
        return response;
    }

    private <T> T fromJSON(String s, Class<T> clazz) {
        return mapper.readObject(s, clazz);
    }
}
