package com.example.api.trade.dbservice.impl;

import com.example.api.trade.dbservice.util.ManageTrade;
import com.example.api.trade.dbservice.TradeService;
import com.example.api.trade.model.Trade;
import com.example.api.trade.model.request.TradeRequest;
import com.example.api.trade.model.response.TradeResponse;
import com.mongodb.MongoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    final ManageTrade manageTrade;

    public TradeServiceImpl(ManageTrade manageTrade) {
        this.manageTrade = manageTrade;
    }

    @Override
    public ResponseEntity<TradeResponse> create(TradeRequest tradeRequest)
            throws MongoException {

        List<Trade> trades = manageTrade.processTrade(tradeRequest);

        return new ResponseEntity<>(
                TradeResponse.builder()
                        .data(TradeResponse.Data.builder()
                                .type(tradeRequest.getData().getType())
                                .message("Success")
                                .trades(trades)
                                .build())
                        .build(),
                CollectionUtils.isEmpty(trades) ?
                        HttpStatus.OK :
                        HttpStatus.CREATED
        );
    }
}
