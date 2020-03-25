package com.NowakArtur97.GlobalTerrorismAPI.exception;

public class TargetNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7907917803732822384L;

	public TargetNotFoundException(Long id) {

		super("Could not find target with id: " + id);
	}
}
