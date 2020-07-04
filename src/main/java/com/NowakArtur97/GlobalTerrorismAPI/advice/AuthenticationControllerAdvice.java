package com.NowakArtur97.GlobalTerrorismAPI.advice;

import com.NowakArtur97.GlobalTerrorismAPI.controller.security.AuthenticationController;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice(basePackageClasses = AuthenticationController.class)
public class AuthenticationControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());

        Set<String> validationErrors = new HashSet<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));

        Arrays.stream(String.join(",", validationErrors).split(",")).forEach(errorResponse::addError);

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
