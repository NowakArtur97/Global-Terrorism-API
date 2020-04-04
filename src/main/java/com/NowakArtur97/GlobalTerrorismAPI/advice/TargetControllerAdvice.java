package com.NowakArtur97.GlobalTerrorismAPI.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;

@RestControllerAdvice(basePackageClasses = TargetController.class)
public class TargetControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(TargetNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTargetNotFound(TargetNotFoundException exception) {

		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				exception.getMessage());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
}
