package com.example.api.trade.dbservice.util;

import com.example.api.trade.dbservice.repo.TradeRepository;
import com.example.api.trade.error.ErrorCodes;
import com.example.api.trade.model.Trade;
import com.example.api.trade.model.request.TradeRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ManageTrade {

    final TradeRepository tradeRepository;

    public ManageTrade(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public List<Trade> processTrade(TradeRequest tradeRequest) {

        Map<String, Trade> tradeMap = processTradeInTransmission(
                tradeRequest.getData().getTrades());

        List<String> tradeIds = new ArrayList<>(tradeMap.keySet());
        List<Trade> tradesInDB = tradeRepository.find(tradeIds);
        validateTradeWithDB(tradesInDB, tradeMap);

        tradeRepository.create(new ArrayList<>(tradeMap.values()));
        tradeRepository.update(tradesInDB);

        List<Trade> trades = new ArrayList<>(tradeMap.values());
        trades.addAll(tradesInDB);

        return trades;
    }

    private Map<String, Trade> processTradeInTransmission(List<Trade> trades) {

        Map<String, Trade> tradeMap = new HashMap<>();
        trades.forEach(trade -> validateTrade(trade, tradeMap));

        return tradeMap;
    }

    private void validateTradeWithDB(List<Trade> tradesInDB, Map<String, Trade> tradeMap) {

        if (CollectionUtils.isEmpty(tradesInDB) || CollectionUtils.isEmpty(tradeMap)) {
            return;
        }

        List<Trade> newVersionOfTrades = new ArrayList<>();
        tradesInDB.forEach(tradeInDB -> {

            Trade newTrade = tradeMap.get(tradeInDB.getTradeId());
            if (isLowerVersion(newTrade, tradeInDB)) {
                ErrorCodes.LOWER_VERSION_TRADE.buildException(newTrade);
                validateMatureTrade(tradeInDB);
                newVersionOfTrades.add(tradeInDB);
            } else {
                newVersionOfTrades.add(newTrade);
            }
            tradeMap.remove(newTrade.getTradeId());
        });

        tradesInDB.clear();
        tradesInDB.addAll(newVersionOfTrades);
    }

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

    private void validateMatureTrade(Trade trade) {
        if (!isTradeExpired(trade)) {
            trade.setExpired(Boolean.TRUE);
        }
    }

    private boolean isLowerVersion(Trade newTrade, Trade existingTrade) {
        return existingTrade != null && newTrade.getVersion() < existingTrade.getVersion();
    }

    private boolean isTradeExpired(Trade newTrade) {
        return newTrade.getMaturityDate().isBefore(LocalDate.now());
    }
}
