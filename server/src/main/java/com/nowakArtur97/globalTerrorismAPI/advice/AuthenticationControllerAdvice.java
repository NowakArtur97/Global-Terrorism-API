package com.nowakArtur97.globalTerrorismAPI.advice;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.nowakArtur97.globalTerrorismAPI.feature.user")
public class AuthenticationControllerAdvice {

    @ExceptionHandler({BadCredentialsException.class})
    ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value());

        errorResponse.addError("Invalid login credentials.");

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value());

        errorResponse.addError(exception.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
