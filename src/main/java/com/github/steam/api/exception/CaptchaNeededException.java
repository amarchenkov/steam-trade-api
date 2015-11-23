package com.github.steam.api.exception;

public class CaptchaNeededException extends IEconServiceException {

    public CaptchaNeededException(String message) {
        super(message);
    }

    public CaptchaNeededException(String message, Throwable cause) {
        super(message, cause);
    }

}
