package com.github.steam.api.exception;

public class IEconServiceException extends Exception {

    public IEconServiceException(String message) {
        super("STEAM-API: " + message);
    }

    public IEconServiceException(String message, Throwable cause) {
        super("STEAM-API: " + message, cause);
    }

}