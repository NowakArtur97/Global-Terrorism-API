package com.NowakArtur97.GlobalTerrorismAPI.exception;

public class EventNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1404139443580548779L;

	private static final String EXCEPTION_MESSAGE = "Could not find event with id: ";

	public EventNotFoundException(Long id) {

		super(EXCEPTION_MESSAGE + id);
	}
}
