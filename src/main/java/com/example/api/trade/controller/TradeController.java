package com.example.api.trade.controller;

import com.example.api.trade.constants.RequestConstant;
import com.example.api.trade.dbservice.TradeService;
import com.example.api.trade.model.request.TradeRequest;
import com.example.api.trade.model.response.TradeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RequestConstant.TRADES)
public class TradeController {

  final TradeService tradeService;

  public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

  @PostMapping
  public ResponseEntity<TradeResponse> create(@RequestBody TradeRequest tradeRequest) {

      return tradeService.create(tradeRequest);
  }
}
