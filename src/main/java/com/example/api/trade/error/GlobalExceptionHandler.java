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

    /**
     * Handle MongoException and return a generic error message with INTERNAL_SERVER_ERROR status.
     * @param e MongoException to be handled.
     * @return ResponseEntity with generic error message and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(MongoException.class)
    public ResponseEntity<String> handleMongoException(MongoException e) {

        ErrorCodes.GENERAL_ERROR.buildException(e);
        return new ResponseEntity<>(ErrorCodes.GENERAL_ERROR.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle IllegalArgumentException and return the error message with BAD_REQUEST status.
     * @param e IllegalArgumentException to be handled.
     * @return ResponseEntity with error message and BAD_REQUEST status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {

        ErrorCodes.GENERAL_ERROR.buildException(e);
        return new ResponseEntity<>(e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }
}
