package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.apache.commons.lang.Validate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
@ApplicationScoped
public class TradingPlattformServiceImpl implements TradingPlattformService {

    private @Inject BinanceClientService binanceClientService;

    @Override
    public boolean ueberpruefeErreichbarkeit(TradingPlattform tradingPlattform) {
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        return tradingPlattformClient.ping();
    }

    @Override
    public Map<String, WSCryptoCoinDTO> holeWSCryptoCoinMap(TradingPlattform tradingPlattform) {
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        List<WSCryptoCoinDTO> wsCryptoCoinDTOList = tradingPlattformClient.ermittleLetztePreise();
        return ermittleWSCryptoCoinMap(wsCryptoCoinDTOList);
    }

    private Map<String, WSCryptoCoinDTO> ermittleWSCryptoCoinMap(List<WSCryptoCoinDTO> wsCryptoCoinDTOList) {
        Validate.notNull(wsCryptoCoinDTOList, "wsCryptoCoinDTOList ist null");

        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = new HashMap<>();
        for (WSCryptoCoinDTO wsCryptoCoinDTO : wsCryptoCoinDTOList) {
            wsCryptoCoinDTOMap.put(wsCryptoCoinDTO.getSymbol(), wsCryptoCoinDTO);
        }

        return wsCryptoCoinDTOMap;
    }

    private TradingPlattformClient ermittleClient(TradingPlattform tradingPlattform) {
        if (TradingPlattform.BINANCE == tradingPlattform) {
            return binanceClientService;
        }
        else {
            throw new IllegalArgumentException("Abfragen von TradingPlattform=" + tradingPlattform + " werden derzeit nicht unterstuetzt.");
        }
    }
}
