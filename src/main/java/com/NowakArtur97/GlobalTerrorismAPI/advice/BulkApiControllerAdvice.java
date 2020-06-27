package com.NowakArtur97.GlobalTerrorismAPI.advice;

import com.github.wnameless.spring.bulkapi.BulkApiController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice(basePackageClasses = BulkApiController.class)
public class BulkApiControllerAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException exception) {

        return new ResponseEntity<>(exception.getResponseBodyAsString(), exception.getResponseHeaders(), exception.getStatusCode());
    }
}
