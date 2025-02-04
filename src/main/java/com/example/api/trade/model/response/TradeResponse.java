package com.example.api.trade.model.response;

import com.example.api.trade.model.Trade;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TradeResponse {

    private Data data;

    @Builder
    @Getter
    public static class Data {

        private String type;
        private String message;
        private List<Trade> trades;
    }
}
