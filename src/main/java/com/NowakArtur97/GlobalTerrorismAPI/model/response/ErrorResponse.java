package com.NowakArtur97.GlobalTerrorismAPI.model.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ErrorResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private LocalDateTime timestamp;

	private int status;

	private final List<String> errors;

	public ErrorResponse(LocalDateTime timestamp, int status) {
		this.timestamp = timestamp;
		this.status = status;
		this.errors = new ArrayList<>();
	}

	public void addError(String error) {

		errors.add(error);
	}
}
