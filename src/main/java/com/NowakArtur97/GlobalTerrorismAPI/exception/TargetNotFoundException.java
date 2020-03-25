package com.NowakArtur97.GlobalTerrorismAPI.exception;

public class TargetNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7907917803732822384L;

	 private static final String EXCEPTION_MESSAGE = "Could not find target with id: ";
	
	public TargetNotFoundException(Long id) {

		super(EXCEPTION_MESSAGE + id);
	}
}
