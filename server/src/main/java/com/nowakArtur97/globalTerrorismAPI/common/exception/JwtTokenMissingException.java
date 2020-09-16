package com.nowakArtur97.globalTerrorismAPI.common.exception;

public class JwtTokenMissingException extends RuntimeException {

    public JwtTokenMissingException(String message) {

        super(message);
    }
}
