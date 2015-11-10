package com.github.steam.api;

class SteamException extends Exception {

    public SteamException(String message) {
        super("STEAM-API: " + message);
    }

    public SteamException(String message, Throwable cause) {
        super("STEAM-API: " + message, cause);
    }

}