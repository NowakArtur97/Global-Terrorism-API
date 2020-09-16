package com.nowakArtur97.globalTerrorismAPI.advice;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.nowakArtur97.globalTerrorismAPI.feature")
public class GenericRestControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value());

        errorResponse.addError(exception.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
