package com.example.api.trade.dbservice.repo;

import com.example.api.trade.model.Trade;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.ArrayList;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "db.instance.name", havingValue = "dynamodb")
public class TradeDynamoRepository {

    private final DynamoDbTable<Trade> tradeDynamoDbTable;

    public TradeDynamoRepository(DynamoDbEnhancedClient enhancedClient) {
        this.tradeDynamoDbTable = enhancedClient
                .table("Trade", TableSchema.fromBean(Trade.class));
    }

    /**
     * Save a trade to DynamoDB.
     * @param trade The trade to be saved.
     */
    public void create(Trade trade) {
        tradeDynamoDbTable.putItem(trade);
    }

    public void create(List<Trade> trades) {

        if (CollectionUtils.isEmpty(trades)) {
            return;
        }

        for (Trade trade : trades) {
            create(trade);
        }
    }

    public void update(List<Trade> trades) {

        if (CollectionUtils.isEmpty(trades)) {
            return;
        }

        for (Trade trade : trades) {
            tradeDynamoDbTable.updateItem(UpdateItemEnhancedRequest.builder(Trade.class)
                    .item(trade)
                    .build());
        }
    }

    /**
     * Find a trade in DynamoDB based on the tradeId.
     * @param tradeId The tradeId to find the trade in DynamoDB.
     * @return The trade found in DynamoDB based on the tradeId.
     */
    public Trade find(String tradeId) {
        return tradeDynamoDbTable.getItem(r -> r.key(k -> k.partitionValue(tradeId)));
    }

    public List<Trade> find(List<String> tradesIds) {

        List<Trade> trades = new ArrayList<>();
        tradesIds.forEach(id -> trades.add(this.find(id)));
        return trades;
    }
}
