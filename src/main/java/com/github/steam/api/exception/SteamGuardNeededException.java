package com.github.steam.api.exception;

public class SteamGuardNeededException extends IEconServiceException {

    public SteamGuardNeededException(String message) {
        super(message);
    }

    public SteamGuardNeededException(String message, Throwable cause) {
        super(message, cause);
    }
}
