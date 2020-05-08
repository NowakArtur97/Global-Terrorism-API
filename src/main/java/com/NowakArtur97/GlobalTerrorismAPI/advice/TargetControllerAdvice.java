package com.NowakArtur97.GlobalTerrorismAPI.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;

@RestControllerAdvice(basePackageClasses = TargetController.class)
public class TargetControllerAdvice {

	@ExceptionHandler(TargetNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTargetNotFound(TargetNotFoundException exception) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value());

		errorResponse.addError(exception.getMessage());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
}
