package com.NowakArtur97.GlobalTerrorismAPI.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String nodeType) {
        super("Could not find " + nodeType);
    }

    public ResourceNotFoundException(String nodeType, Long id) {

        super("Could not find " + nodeType + " with id: " + id + ".");
    }
}
