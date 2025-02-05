package com.example.api.trade.dbservice.repo;

import com.example.api.trade.constants.MongoConstant;
import com.example.api.trade.model.Trade;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TradeRepository extends MongoRepository {

    private MongoCollection<Trade> tradeCollection;

    @PostConstruct void init() {

        tradeCollection = getMongoDatabase().getCollection(
                MongoConstant.TRADE, Trade.class);
    }

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

    public void create(List<Trade> trades) {

        if (CollectionUtils.isEmpty(trades)) {
            return;
        }

        tradeCollection.insertMany(trades);
    }

    public List<Trade> find(List<String> tradeIds) {

        return tradeCollection
                .find(Filters.and(Filters.in("tradeId", tradeIds)))
                .into(new ArrayList<>());
    }
}
