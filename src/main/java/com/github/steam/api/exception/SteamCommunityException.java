package com.github.steam.api.exception;

public class SteamCommunityException extends Exception {

    public SteamCommunityException(String message) {
        super("STEAM-COMMUNITY-API: " + message);
    }

    public SteamCommunityException(String message, Throwable cause) {
        super("STEAM-COMMUNITY-API: " + message, cause);
    }

}
