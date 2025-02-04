package com.example.api.trade.error;

import com.mongodb.MongoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler =
            new GlobalExceptionHandler();

    @Test
    void handleMongoException_shouldReturnInternalServerError() {

        MongoException mongoException = new MongoException("Database connection failed");
        ResponseEntity<String> responseEntity = globalExceptionHandler.handleMongoException(mongoException);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assertions.assertEquals(ErrorCodes.GENERAL_ERROR.getMessage(), responseEntity.getBody());
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() {

        String exceptionMessage = "Bad Request";
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException(exceptionMessage);
        ResponseEntity<String> responseEntity = globalExceptionHandler.handleIllegalArgumentException(illegalArgumentException);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(exceptionMessage, responseEntity.getBody());
    }
}
