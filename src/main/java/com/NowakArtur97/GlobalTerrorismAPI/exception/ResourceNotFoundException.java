package com.NowakArtur97.GlobalTerrorismAPI.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String nodeType, Long id) {

        super("Could not find " + nodeType + " with id: " + id);
    }
}
