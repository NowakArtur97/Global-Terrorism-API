package com.NowakArtur97.GlobalTerrorismAPI.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.NowakArtur97.GlobalTerrorismAPI.model.ErrorResponse;

@ControllerAdvice
public class RestResponseGlobalEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value());

		ex.getBindingResult().getFieldErrors().stream()
				.forEach(error -> errorResponse.addError(error.getDefaultMessage()));

		return new ResponseEntity<>(errorResponse, headers, status);
	}
}
