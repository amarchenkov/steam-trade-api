package com.github.steam.api.exception;

public class SteamApiException extends Exception {

    public SteamApiException(String message) {
        super("STEAM-API: " + message);
    }

    public SteamApiException(String message, Throwable cause) {
        super("STEAM-API: " + message, cause);
    }

}