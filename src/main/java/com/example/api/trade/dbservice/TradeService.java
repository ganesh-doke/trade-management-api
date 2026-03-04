package com.example.api.trade.dbservice;

import com.example.api.trade.model.request.TradeRequest;
import com.example.api.trade.model.response.TradeResponse;
import com.mongodb.MongoException;
import org.springframework.http.ResponseEntity;

public interface TradeService {

    /**
     * Create the list of trades in DB based on the trade request.
     * @param tradeRequest Trade request containing the list of trades to be created in DB.
     * @return ResponseEntity with the list of trades created in DB and appropriate HTTP status.
     * @throws MongoException if there is an error while creating the trades in DB.
     */
    ResponseEntity<TradeResponse> create(TradeRequest tradeRequest)
            throws MongoException;
}
