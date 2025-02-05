package com.example.api.trade.data;

import com.example.api.trade.model.Trade;
import com.example.api.trade.model.request.TradeRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.platform.commons.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TestData {

    public static final ObjectMapper JSON_MAPPER =
            new ObjectMapper()
                    .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .registerModule(new JavaTimeModule())
                    .setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

    private static <T> T getObjectFromJson(String jsonValue, Class<T> valueType) {

        try {
            return JSON_MAPPER.readValue(readValue(jsonValue), valueType);
        } catch (IOException e) {
            return null;
        }
    }

    private static String readValue(final String filePath) throws IOException {

        if (StringUtils.isBlank(filePath)) {
            return "{}";
        }
        return IOUtils.toString(Objects.requireNonNull(
                TestData.class.getResourceAsStream(filePath)));
    }

    public static TradeRequest getTradeRequest() {
        return getObjectFromJson("/json/trade-request.json", TradeRequest.class);
    }

    public static List<Trade> getTradeList() {
        return getObjectFromJson("/json/find-response.json", TradeRequest.class)
                .getData().getTrades();
    }

    public static TradeRequest getTradeRequestWithNoValidTrade() {
        return getObjectFromJson("/json/trade-request-no-valid-trade.json", TradeRequest.class);
    }
}
