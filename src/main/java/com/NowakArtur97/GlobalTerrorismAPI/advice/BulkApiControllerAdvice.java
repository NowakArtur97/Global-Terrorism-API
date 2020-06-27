package com.NowakArtur97.GlobalTerrorismAPI.advice;

import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;
import com.github.wnameless.spring.bulkapi.BulkApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice(basePackageClasses = BulkApiController.class)
public class BulkApiControllerAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException exception) {

        String message = exception.getResponseBodyAsString().replace("\"", "");

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), exception.getStatusCode().value());

        Arrays.stream(getMessagesFromBody(message)).forEach(errorResponse::addError);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private String[] getMessagesFromBody(String message) {

        return message
                .substring(message.indexOf("[") + 1, message.indexOf("]"))
                .split(",");
    }
}
