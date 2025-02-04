package com.example.api.trade.error;

import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MongoException.class)
    public ResponseEntity<String> handleMongoException(MongoException e) {

        ErrorCodes.GENERAL_ERROR.buildException(e);
        return new ResponseEntity<>(ErrorCodes.GENERAL_ERROR.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {

        ErrorCodes.GENERAL_ERROR.buildException(e);
        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
}
