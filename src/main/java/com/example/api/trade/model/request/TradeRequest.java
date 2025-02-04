package com.example.api.trade.model.request;

import com.example.api.trade.model.Trade;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class TradeRequest {

    private Data data;

    @NoArgsConstructor
    @Getter
    public static class Data {

        private String type;
        private List<Trade> trades;
    }
}
