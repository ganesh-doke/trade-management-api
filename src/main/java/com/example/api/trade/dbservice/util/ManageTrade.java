package com.example.api.trade.dbservice.util;

import com.example.api.trade.dbservice.repo.TradeDynamoRepository;
import com.example.api.trade.dbservice.repo.TradeRepository;
import com.example.api.trade.error.ErrorCodes;
import com.example.api.trade.model.Trade;
import com.example.api.trade.model.request.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ManageTrade {

    @Autowired(required = false) TradeRepository tradeRepository;
    @Autowired(required = false) TradeDynamoRepository tradeDynamoRepository;

    @Value("${db.instance.name}")
    private String dbInstanceName;

    private Boolean isMongoDB() {
        return "mongodb".equalsIgnoreCase(dbInstanceName);
    }

    /**
     * Process the trade request and return the list of trades to be created/updated in DB.
     * @param tradeRequest Trade request containing the list of trades to be processed.
     * @return List of trades to be created/updated in DB.
     */
    public List<Trade> processTrade(TradeRequest tradeRequest) {

        Map<String, Trade> tradeMap = processTradeInTransmission(
                tradeRequest.getData().getTrades());

        List<String> tradeIds = new ArrayList<>(tradeMap.keySet());
        List<Trade> tradesInDB = new ArrayList<>();

        if (isMongoDB()) {
            tradesInDB = tradeRepository.find(tradeIds);
            validateTradeWithDB(tradesInDB, tradeMap);

            tradeRepository.create(new ArrayList<>(tradeMap.values()));
            tradeRepository.update(tradesInDB);
        }
        else {
            tradesInDB = tradeDynamoRepository.find(tradeIds);
            validateTradeWithDB(tradesInDB, tradeMap);

            tradeDynamoRepository.create(new ArrayList<>(tradeMap.values()));
            tradeDynamoRepository.update(tradesInDB);
        }
        List<Trade> trades = new ArrayList<>(tradeMap.values());
        trades.addAll(tradesInDB);

        return trades;
    }

    /**
     * Process the trade request and return the map of trades to be created/updated in DB.
     * @param trades List of trades to be processed.
     * @return Map of trades to be created/updated in DB.
     */
    private Map<String, Trade> processTradeInTransmission(List<Trade> trades) {

        Map<String, Trade> tradeMap = new HashMap<>();
        trades.forEach(trade -> validateTrade(trade, tradeMap));

        return tradeMap;
    }

    /**
     * Validate the trades in DB with the trades in the request and update the trades in DB accordingly.
     * @param tradesInDB List of trades in DB.
     * @param tradeMap Map of trades in the request.
     */
    private void validateTradeWithDB(List<Trade> tradesInDB, Map<String, Trade> tradeMap) {

        if (CollectionUtils.isEmpty(tradesInDB) || CollectionUtils.isEmpty(tradeMap)) {
            return;
        }

        List<Trade> newVersionOfTrades = new ArrayList<>();
        tradesInDB.forEach(tradeInDB -> {

            Trade newTrade = tradeMap.get(tradeInDB.getTradeId());

            if (newTrade != null) {
                if (isLowerVersion(newTrade, tradeInDB)) {
                    ErrorCodes.LOWER_VERSION_TRADE.buildException(newTrade);
                    validateMatureTrade(tradeInDB);
                    newVersionOfTrades.add(tradeInDB);
                } else {
                    newVersionOfTrades.add(newTrade);
                }
                tradeMap.remove(newTrade.getTradeId());
            }
        });

        tradesInDB.clear();
        tradesInDB.addAll(newVersionOfTrades);
    }

    /**
     * Validate the trade with the existing trade in the request and update the trade map accordingly.
     * @param trade Trade to be validated.
     * @param tradeMap Map of trades in the request.
     */
    private void validateTrade(Trade trade, Map<String, Trade> tradeMap) {

        Trade existingTrade = tradeMap.get(trade.getTradeId());
        if (isTradeExpired(trade)) {
            trade.setExpired(Boolean.TRUE);
            ErrorCodes.EXPIRED_TRADE.buildWarning(trade);
        } else if (isLowerVersion(trade, existingTrade)) {
            ErrorCodes.LOWER_VERSION_TRADE.buildException(trade);
        } else {
            tradeMap.put(trade.getTradeId(), trade);
        }
    }

    /**
     * Validate the trade with the existing trade in DB and update the trade in DB accordingly.
     * @param trade Trade to be validated.
     */
    private void validateMatureTrade(Trade trade) {
        if (!isTradeExpired(trade)) {
            trade.setExpired(Boolean.TRUE);
        }
    }

    /**
     * Check if the new trade is of lower version than the existing trade.
     * @param newTrade New trade to be validated.
     * @param existingTrade Existing trade to be compared with.
     * @return true if the new trade is of lower version than the existing trade, false otherwise.
     */
    private boolean isLowerVersion(Trade newTrade, Trade existingTrade) {
        return existingTrade != null && newTrade != null && newTrade.getVersion() < existingTrade.getVersion();
    }

    /**
     * Check if the trade is expired based on the maturity date.
     * @param newTrade New trade to be validated.
     * @return true if the trade is expired, false otherwise.
     */
    private boolean isTradeExpired(Trade newTrade) {
        return newTrade.getMaturityDate().isBefore(LocalDate.now());
    }
}
