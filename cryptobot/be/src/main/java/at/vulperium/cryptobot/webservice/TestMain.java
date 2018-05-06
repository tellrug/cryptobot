package at.vulperium.cryptobot.webservice;

import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.OrderDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradeJobTyp;
import at.vulperium.cryptobot.enums.TradeStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import at.vulperium.cryptobot.utils.TradeUtil;
import at.vulperium.cryptobot.webservice.binance.BinanceApiService;
import at.vulperium.cryptobot.webservice.binance.BinanceClientServiceImpl;

import java.util.Map;

public class TestMain {

    public static void main( String[] args ) {

        BinanceClientServiceImpl binanceClientService = new BinanceClientServiceImpl();

        //FUNKTIONIERT
        Map<String, WSCryptoCoinDTO> map =binanceClientService.ermittleLetztePreiseMap();

        /*
        HoldingDTO holdingDTO = binanceClientService.ermittleHoldingInformationen();
        System.out.println("Anzahl der Coins: " + holdingDTO.getHoldingMap().size());
        */

        /*
        TradeAktionDTO tradeAktionDTO = new TradeAktionDTO();
        tradeAktionDTO.setTradingPlattform(TradingPlattform.BINANCE);
        tradeAktionDTO.setCryptoWaehrungReferenz("BTC");
        tradeAktionDTO.setCryptoWaehrung("XVG");
        tradeAktionDTO.setMenge(TradeUtil.getBigDecimal("250"));
        tradeAktionDTO.setPreisProEinheit(TradeUtil.getBigDecimal(0.00001718));
        tradeAktionDTO.setTradeTyp(TradeTyp.VERKAUF);
        tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_VERKAUF);
        tradeAktionDTO.setTradeJobTyp(TradeJobTyp.SIMPEL);
        binanceClientService.erstelleOrder(tradeAktionDTO);

        //84f6c457-6888-45bd-aba9-0c42a7cd2acf
        */

        /*
        OrderDTO orderDTO = binanceClientService.holeOrderInfosByClientOrderId("XVGBTC", "84f6c457-6888-45bd-aba9-0c42a7cd2acf");
        System.out.println("Orderstatus: " + orderDTO.getOrderStatus());
        */

        /*
        TradeAktionDTO tradeAktionDTO = new TradeAktionDTO();
        tradeAktionDTO.setTradingPlattform(TradingPlattform.BINANCE);
        tradeAktionDTO.setCryptoWaehrungReferenz("BTC");
        tradeAktionDTO.setCryptoWaehrung("XVG");
        tradeAktionDTO.setMenge(TradeUtil.getBigDecimal("250"));
        tradeAktionDTO.setPreisProEinheit(TradeUtil.getBigDecimal(0.00001718));
        tradeAktionDTO.setTradeTyp(TradeTyp.VERKAUF);
        tradeAktionDTO.setTradeStatus(TradeStatus.TRADE_VERKAUF);
        tradeAktionDTO.setTradeJobTyp(TradeJobTyp.SIMPEL);
        tradeAktionDTO.setCustomerOrderId("84f6c457-6888-45bd-aba9-0c42a7cd2acf");
        binanceClientService.storniereOrder(tradeAktionDTO);
        */


        BinanceApiService binanceApiService = new BinanceApiService();
        binanceApiService.pingTest();
    }
}
