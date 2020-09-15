package com.NowakArtur97.GlobalTerrorismAPI.advice;

import com.NowakArtur97.GlobalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.common.baseModel.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.NowakArtur97.GlobalTerrorismAPI.feature")
public class GenericRestControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value());

        errorResponse.addError(exception.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
