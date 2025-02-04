package com.example.api.trade.error;

import com.example.api.trade.model.Trade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum ErrorCodes {

    GENERAL_ERROR("Something's gone wrong. Techncial error."),
    LOWER_VERSION_TRADE("Trade with higher version is already processed."),
    EXPIRED_TRADE("Trade is expired.");

    private final String message;

    public void buildException(Exception ex) {
        log.error("{} {}", this.name(), ex);
    }

    public void buildException(Trade trade) {
        log.error("{} {} {}", this.name(), trade.toString(), new Exception(this.message));
    }

    public void buildWarning(Trade trade) {
        log.warn("{} {} {}", this.name(), trade.toString(), this.message);
    }

}
