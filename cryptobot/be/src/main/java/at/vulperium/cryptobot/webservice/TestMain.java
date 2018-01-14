package at.vulperium.cryptobot.webservice;

import at.vulperium.cryptobot.webservice.binance.BinanceClientServiceImpl;

public class TestMain {

    public static void main( String[] args ) {
        BinanceClientServiceImpl binanceClientService = new BinanceClientServiceImpl();
        binanceClientService.ermittleInformationenZuSymbol("LTCBTC");
    }
}
