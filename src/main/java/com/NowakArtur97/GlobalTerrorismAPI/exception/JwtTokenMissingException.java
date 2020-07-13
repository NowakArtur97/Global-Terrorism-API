package com.NowakArtur97.GlobalTerrorismAPI.exception;

public class JwtTokenMissingException extends RuntimeException {

    public JwtTokenMissingException(String message) {

        super(message);
    }
}
