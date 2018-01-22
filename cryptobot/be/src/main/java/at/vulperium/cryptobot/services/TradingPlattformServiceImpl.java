package at.vulperium.cryptobot.services;

import at.vulperium.cryptobot.dtos.HoldingOrderDTO;
import at.vulperium.cryptobot.dtos.webservice.WSCryptoCoinDTO;
import at.vulperium.cryptobot.enums.TradingPlattform;
import org.apache.commons.lang.Validate;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 02ub0400 on 03.01.2018.
 */
@ApplicationScoped
public class TradingPlattformServiceImpl implements TradingPlattformService {

    private @Inject BinanceClientService binanceClientService;

    private Map<TradingPlattform, TradingInfos> tradingInfosMap = new HashMap<>();

    @Override
    public boolean ueberpruefeErreichbarkeit(TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        return tradingPlattformClient.ping();
    }

    @Override
    public Map<String, WSCryptoCoinDTO> holeWSCryptoCoinMap(TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        TradingInfos tradingInfos = holeTradingInfos(tradingPlattform);
        return tradingInfos.getWsCryptoCoinDTOMap();
    }

    @Override
    public HoldingOrderDTO ermittleHoldingOrderInformationen(TradingPlattform tradingPlattform) {
        Validate.notNull(tradingPlattform, "tradingPlattform ist null.");
        TradingInfos tradingInfos = holeTradingInfos(tradingPlattform);
        return tradingInfos.getHoldingOrderDTO();
    }


    private Map<String, WSCryptoCoinDTO> ladeWSCryptoCoinMapPerWS(TradingPlattform tradingPlattform) {
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        List<WSCryptoCoinDTO> wsCryptoCoinDTOList = tradingPlattformClient.ermittleLetztePreise();
        return ermittleWSCryptoCoinMap(wsCryptoCoinDTOList);
    }

    private HoldingOrderDTO ladeHoldingOrderInformationenPerWS(TradingPlattform tradingPlattform) {
        TradingPlattformClient tradingPlattformClient = ermittleClient(tradingPlattform);
        return tradingPlattformClient.ermittleHoldingOrderInformationen();
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

    private TradingInfos holeTradingInfos(TradingPlattform tradingPlattform) {
        synchronized (this) {
            if (tradingInfosMap.get(tradingPlattform) == null ||
                    tradingInfosMap.get(tradingPlattform).getLetzteAktualisierung() == null ||
                    tradingInfosMap.get(tradingPlattform).getLetzteAktualisierung().plus(Duration.standardMinutes(1L)).isBefore(LocalDateTime.now())) {

                //Aktualisierung der Daten per WS
                Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap = ladeWSCryptoCoinMapPerWS(tradingPlattform);
                HoldingOrderDTO holdingOrderDTO = ladeHoldingOrderInformationenPerWS(tradingPlattform);

                TradingInfos tradingInfos = new TradingInfos(LocalDateTime.now(), tradingPlattform, wsCryptoCoinDTOMap, holdingOrderDTO);
                tradingInfosMap.put(tradingPlattform, tradingInfos);
            }
        }

        return tradingInfosMap.get(tradingPlattform);
    }

    private final static class TradingInfos implements Serializable {

        private TradingPlattform tradingPlattform;
        private LocalDateTime letzteAktualisierung;
        Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap;
        private HoldingOrderDTO holdingOrderDTO;

        public TradingInfos(LocalDateTime letzteAktualisierung, TradingPlattform tradingPlattform,
                            Map<String, WSCryptoCoinDTO> wsCryptoCoinDTOMap, HoldingOrderDTO holdingOrderDTO) {
            this.holdingOrderDTO = holdingOrderDTO;
            this.letzteAktualisierung = letzteAktualisierung;
            this.tradingPlattform = tradingPlattform;
            this.wsCryptoCoinDTOMap = wsCryptoCoinDTOMap;
        }

        public HoldingOrderDTO getHoldingOrderDTO() {
            return holdingOrderDTO;
        }

        public TradingPlattform getTradingPlattform() {
            return tradingPlattform;
        }

        public Map<String, WSCryptoCoinDTO> getWsCryptoCoinDTOMap() {
            return wsCryptoCoinDTOMap;
        }

        public LocalDateTime getLetzteAktualisierung() {
            return letzteAktualisierung;
        }
    }
}
