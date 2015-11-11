package com.github.steam.api;

/**
 * Статус на портале steamcommunity
 *
 * @author Andrey Marchenkov
 */
public enum SteamWebState {
    NOT_LOGGED_IN,
    LOGGED_IN,
    LOGIN_FAILED,
    CAPTCHA_NEEDE,
    STEAM_GUARD_NEEDED,
    GET_RSA_FAILED
}