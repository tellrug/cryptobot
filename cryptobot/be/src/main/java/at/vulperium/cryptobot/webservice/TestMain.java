package at.vulperium.cryptobot.webservice;

import at.vulperium.cryptobot.webservice.binance.BinanceApiService;
import at.vulperium.cryptobot.webservice.binance.BinanceClientServiceImpl;

public class TestMain {

    public static void main( String[] args ) {

        BinanceClientServiceImpl binanceClientService = new BinanceClientServiceImpl();

        //Funktioniert
        //binanceClientService.holeOffeneOrderBySymbolPair("XVGBTC");

        binanceClientService.ermittleAktuelleHoldings();

        /*
        BinanceApiService binanceApiService = new BinanceApiService();
        binanceApiService.pingTest();
        */
    }
}
