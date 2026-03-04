package com.example.api.trade.dbservice.repo;

import com.example.api.trade.constants.MongoConstant;
import com.example.api.trade.model.Trade;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "db.instance.name", havingValue = "mongodb")
public class TradeRepository extends MongoRepository {

    private MongoCollection<Trade> tradeCollection;

    @PostConstruct void setUp() {

        tradeCollection = getMongoDatabase().getCollection(
                MongoConstant.TRADE, Trade.class);
    }

    /**
     * Update the list of trades in DB.
     * @param trades List of trades to be updated in DB.
     */
    public void update(List<Trade> trades) {

        if (CollectionUtils.isEmpty(trades)) {
            return;
        }

        for (Trade trade : trades) {
            BasicDBObject query = new BasicDBObject("tradeId", trade.getTradeId());
            tradeCollection.updateOne(query,
                    new BasicDBObject("$set", getBasicDBObject(trade)));
        }
    }

    /**
     * Create the list of trades in DB.
     * @param trades List of trades to be created in DB.
     */
    public void create(List<Trade> trades) {

        if (CollectionUtils.isEmpty(trades)) {
            return;
        }

        tradeCollection.insertMany(trades);
    }

    /**
     * Find the list of trades in DB based on the list of tradeIds.
     * @param tradeIds List of tradeIds to find the trades in DB.
     * @return List of trades in DB based on the list of tradeIds.
     */
    public List<Trade> find(List<String> tradeIds) {

        return tradeCollection
                .find(Filters.and(Filters.in("tradeId", tradeIds)))
                .into(new ArrayList<>());
    }
}
