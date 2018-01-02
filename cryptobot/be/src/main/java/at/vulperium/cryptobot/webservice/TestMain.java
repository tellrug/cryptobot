package at.vulperium.cryptobot.webservice;

import at.vulperium.cryptobot.webservice.binance.BinanceClientService;

public class TestMain {

    public static void main( String[] args ) {
        BinanceClientService binanceClientService = new BinanceClientService();
        binanceClientService.ermittleLetztePreise();
    }
}
