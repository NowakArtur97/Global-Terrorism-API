package com.NowakArtur97.GlobalTerrorismAPI.common.exception;

public class JwtTokenMissingException extends RuntimeException {

    public JwtTokenMissingException(String message) {

        super(message);
    }
}
