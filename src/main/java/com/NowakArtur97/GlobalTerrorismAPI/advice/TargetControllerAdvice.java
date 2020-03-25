package com.NowakArtur97.GlobalTerrorismAPI.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.NowakArtur97.GlobalTerrorismAPI.controller.TargetController;
import com.NowakArtur97.GlobalTerrorismAPI.exception.TargetNotFoundException;

@RestControllerAdvice(basePackageClasses = TargetController.class)
public class TargetControllerAdvice {

	@ExceptionHandler(TargetNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public String handleTargetNotFound(TargetNotFoundException exception) {

		return exception.getMessage();
	}
}
