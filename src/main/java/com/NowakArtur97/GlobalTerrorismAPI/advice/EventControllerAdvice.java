package com.NowakArtur97.GlobalTerrorismAPI.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.EventController;
import com.NowakArtur97.GlobalTerrorismAPI.exception.EventNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;

@RestControllerAdvice(basePackageClasses = EventController.class)
public class EventControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EventNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTargetNotFound(EventNotFoundException exception) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value());

		errorResponse.addError(exception.getMessage());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
}
