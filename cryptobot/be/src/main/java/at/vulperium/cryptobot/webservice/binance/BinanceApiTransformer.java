package at.vulperium.cryptobot.webservice.binance;

import at.vulperium.cryptobot.dtos.HoldingDTO;
import at.vulperium.cryptobot.dtos.OrderDTO;
import at.vulperium.cryptobot.dtos.TradeAktionDTO;
import at.vulperium.cryptobot.enums.OrderStatus;
import at.vulperium.cryptobot.enums.TradeTyp;
import at.vulperium.cryptobot.enums.TradingPlattform;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 02ub0400 on 23.01.2018.
 */
@ApplicationScoped
public class BinanceApiTransformer {

    private static final Logger logger = LoggerFactory.getLogger(BinanceApiTransformer.class);


    public HoldingDTO transformBalanceMap(Map<String, BinanceWalletAsset> balancesMap) {

        HoldingDTO holdingDTO = new HoldingDTO();
        holdingDTO.setTradingPlattform(TradingPlattform.BINANCE);

        if (balancesMap == null || balancesMap.isEmpty()) {
            return holdingDTO;
        }

        for (String symbol : balancesMap.keySet()) {
            BinanceWalletAsset binanceWalletAsset = balancesMap.get(symbol);
            holdingDTO.getHoldingMap().put(symbol, binanceWalletAsset.getFree());
        }

        return holdingDTO;
    }

    public OrderDTO transformBinanceOrder (BinanceOrder binanceOrder) {
        Validate.notNull(binanceOrder, "binanceOrder ist null.");

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setSymbolPair(binanceOrder.getSymbol());
        orderDTO.setClientOrderId(binanceOrder.getClientOrderId());

        BinanceOrderStatus binanceOrderStatus = binanceOrder.getStatus();
        orderDTO.setOrderStatus(transformiereBinanceOrderStatus(binanceOrderStatus));

        return orderDTO;
    }

    public BinanceOrderPlacement transformTradeAktionDTO(TradeAktionDTO tradeAktionDTO) {
        String clientOrderId = UUID.randomUUID().toString();

        BinanceOrderPlacement binanceOrderPlacement = new BinanceOrderPlacement();


        String symbolPair = tradeAktionDTO.getCryptoWaehrung() + tradeAktionDTO.getCryptoWaehrungReferenz();
        BinanceOrderSide binanceOrderSide = ermittleBinanceOrderSide(tradeAktionDTO.getTradeTyp());

        try {
            binanceOrderPlacement.setSymbol(BinanceSymbol.valueOf(symbolPair));                 //TradingSymbol zB XVGBTC
            binanceOrderPlacement.setSide(binanceOrderSide);                                    //Verkauf oder Kauf: Kauf = BTC --> XVG, Verkauf = XVG --> BTC
            binanceOrderPlacement.setPrice(tradeAktionDTO.getPreisProEinheit());                //Preis in BTC
            binanceOrderPlacement.setQuantity(tradeAktionDTO.getMenge());                       //Menge von NeuWaehrung
            binanceOrderPlacement.setType(BinanceOrderType.LIMIT);                              //Art des Trades LIMIT, MARKET, ...
            binanceOrderPlacement.setNewClientOrderId(clientOrderId);                           //was kann damit gemacht werden?
            binanceOrderPlacement.setTimeInForce(BinanceTimeInForce.GOOD_TILL_CANCELLED);
        }
        catch (BinanceApiException e) {
            e.printStackTrace();
        }

        return binanceOrderPlacement;
    }


    private BinanceOrderSide ermittleBinanceOrderSide(TradeTyp tradeTyp) {
        if (tradeTyp == TradeTyp.KAUF) {            //BTC --> XVG
            return BinanceOrderSide.BUY;
        }
        else if (tradeTyp == TradeTyp.VERKAUF) {    //XVG --> BTC
            return BinanceOrderSide.SELL;
        }
        else {
            //Fehler
            logger.info("Es wird ein konkreter TradeTyp erwartet. TradeTyp={}", tradeTyp);
            throw new IllegalStateException("Fehlerhafter TradeTyp vorhanden!");
        }
    }

    private OrderStatus transformiereBinanceOrderStatus(BinanceOrderStatus binanceOrderStatus) {

        switch (binanceOrderStatus) {
            case NEW:
                return OrderStatus.OFFEN;
            case PARTIALLY_FILLED:
                return OrderStatus.TEILWEISE_OFFEN;
            case CANCELED:
            case PENDING_CANCEL:
            case EXPIRED:
            case REJECTED:
                return OrderStatus.STORNIERT;
            case FILLED:
                return OrderStatus.ABGESCHLOSSEN;
        }

        return null;
    }
}
