package com.nowakArtur97.globalTerrorismAPI.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.nowakArtur97.globalTerrorismAPI.common.baseModel.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.json.JsonException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.nowakArtur97.globalTerrorismAPI.feature")
public class RestResponseGlobalEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());

        exception.getBindingResult().getAllErrors()
                .forEach(error -> errorResponse.addError(error.getDefaultMessage()));

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        String error = "Malformed JSON request ";

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());

        errorResponse.addError(error + exception.getLocalizedMessage());

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());

        exception.getConstraintViolations().forEach(error -> errorResponse.addError(error.getMessage()));

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidFormatException.class, MismatchedInputException.class})
    ResponseEntity<Object> handlerIllegalArgumentException(JsonProcessingException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        errorResponse.addError(exception.getOriginalMessage());

        if (exception instanceof InvalidFormatException) {
            status = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), status);
    }

    @ExceptionHandler({JsonException.class})
    ResponseEntity<Object> handleJsonException(JsonException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());

        errorResponse.addError(exception.getMessage());

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value());

        errorResponse.addError(exception.getMessage());

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
