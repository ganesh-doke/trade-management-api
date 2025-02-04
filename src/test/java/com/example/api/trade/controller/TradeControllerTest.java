package com.example.api.trade.controller;

import com.example.api.trade.data.TestData;
import com.example.api.trade.dbservice.impl.TradeServiceImpl;
import com.example.api.trade.dbservice.repo.TradeRepository;
import com.example.api.trade.dbservice.util.ManageTrade;
import com.example.api.trade.model.Trade;
import com.example.api.trade.model.request.TradeRequest;
import com.example.api.trade.model.response.TradeResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TradeControllerTest {

    TradeController tradeController;
    @InjectMocks TradeServiceImpl tradeService;
    @InjectMocks ManageTrade manageTrade;
    @InjectMocks TradeRepository tradeRepository;
    @Mock MongoCollection<Trade> tradeCollection;
    @Mock MappingMongoConverter mappingMongoConverter;

    @BeforeEach
    void setUp() {

        tradeController = new TradeController(tradeService);
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tradeController, "tradeService", tradeService);
        ReflectionTestUtils.setField(tradeService, "manageTrade", manageTrade);
        ReflectionTestUtils.setField(manageTrade, "tradeRepository", tradeRepository);
        ReflectionTestUtils.setField(tradeRepository, "tradeCollection", tradeCollection);
        ReflectionTestUtils.setField(tradeRepository, "mappingMongoConverter", mappingMongoConverter);
    }

    @Test
    void testCreate() {

        Mockito.doNothing().when(tradeCollection).insertMany(Mockito.any());
        Mockito.when(tradeCollection.updateOne(
                Mockito.any(BasicDBObject.class),
                Mockito.any(BasicDBObject.class)))
                .thenReturn(UpdateResult.unacknowledged())
                .thenReturn(UpdateResult.unacknowledged())
                .thenReturn(UpdateResult.unacknowledged());

        FindIterable iterable = Mockito.mock(FindIterable.class);
        Mockito.when(tradeCollection.find(Mockito.any(Bson.class)))
                .thenReturn(iterable);
        Mockito.when(iterable.into(Mockito.any(List.class)))
                .thenReturn(TestData.getTradeList());
        Mockito.doNothing().when(mappingMongoConverter)
                .write(Mockito.any(),
                        Mockito.any(BasicDBObject.class));

        TradeRequest tradeRequest = TestData.getTradeRequest();
        ResponseEntity<TradeResponse> responseEntity = tradeController.create(tradeRequest);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        TradeResponse tradeResponse = responseEntity.getBody();

        Assertions.assertNotNull(tradeResponse);
        Assertions.assertEquals(tradeRequest.getData().getType(), tradeResponse.getData().getType());
        Assertions.assertEquals("Success", tradeResponse.getData().getMessage());
        Assertions.assertEquals(4, tradeResponse.getData().getTrades().size());
    }

    @Test
    void testCreateWithNoValidTrade() {

        FindIterable iterable = Mockito.mock(FindIterable.class);
        Mockito.when(tradeCollection.find(Mockito.any(Bson.class)))
                .thenReturn(iterable);
        Mockito.when(iterable.into(Mockito.any(List.class)))
                .thenReturn(new ArrayList<>());

        TradeRequest tradeRequest = TestData.getTradeRequestWithNoValidTrade();
        ResponseEntity<TradeResponse> responseEntity = tradeController.create(tradeRequest);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
