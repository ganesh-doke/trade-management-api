package com.example.api.trade.dbservice;

import com.example.api.trade.model.request.TradeRequest;
import com.example.api.trade.model.response.TradeResponse;
import com.mongodb.MongoException;
import org.springframework.http.ResponseEntity;

public interface TradeService {

    ResponseEntity<TradeResponse> create(TradeRequest tradeRequest)
            throws MongoException;
}
