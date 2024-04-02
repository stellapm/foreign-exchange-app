package com.fea.foreign.exchange.app.controller;

import com.fea.foreign.exchange.app.exceptions.IllegalParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

import static com.fea.foreign.exchange.app.constants.Exceptions.EXTERNAL_SERVICE_FAIL;
import static com.fea.foreign.exchange.app.constants.Exceptions.INVALID_ARGUMENT;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex){
        logger.error("An IOException occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EXTERNAL_SERVICE_FAIL);
    }

    @ExceptionHandler(IllegalParamException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalParamException ex){
        logger.error("An IllegalParamException occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleArgumentNotValid(MethodArgumentNotValidException ex){
        logger.error("An MethodArgumentNotValidException occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INVALID_ARGUMENT);
    }
}
