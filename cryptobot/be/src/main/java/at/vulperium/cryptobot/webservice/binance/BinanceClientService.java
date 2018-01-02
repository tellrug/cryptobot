package at.vulperium.cryptobot.webservice.binance;


import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class BinanceClientService {

    public static final String REQUEST_URL = "https://api.binance.com/api/v1";
    public static final String PING = "/ping";
    public static final String ALLE_LETZTE_PREISE = "/ticker/allPrices";

    public void ping() {
        Response response = starteWebServiceCall(REQUEST_URL, PING);
        Response.StatusType status = response.getStatusInfo().toEnum();
        String responseText = response.readEntity(String.class);

        //String[] responseT = response.readEntity(String.class);
        System.out.println(responseText);
    }

    public void ermittleLetztePreise() {
        Response response = starteWebServiceCall(REQUEST_URL, ALLE_LETZTE_PREISE);
        Response.StatusType status = response.getStatusInfo().toEnum();
        String responseText = response.readEntity(String.class);

        //Umwandeln in ein JSONArray
        JSONArray jsonArray = new JSONArray(responseText);

        //Mappen in DTOS und diese returnen
        JSONObject jsonObject = jsonArray.getJSONObject(0);
    }


    private Response starteWebServiceCall(String requestUrl, String methode) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(requestUrl);
        Response response = webTarget.path(methode).request(MediaType.TEXT_PLAIN).get();
        return response;
    }
}
