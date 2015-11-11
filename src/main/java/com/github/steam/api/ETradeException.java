package com.github.steam.api;

public class ETradeException extends Exception {

    public ETradeException(String message) {
        super("STEAM-API: " + message);
    }

    public ETradeException(String message, Throwable cause) {
        super("STEAM-API: " + message, cause);
    }

}