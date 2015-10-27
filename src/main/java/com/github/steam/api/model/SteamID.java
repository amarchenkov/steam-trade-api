package com.github.steam.api.model;

/**
 * A SteamID is a unique identifier used to identify a Steam account.
 * It is also used to refer to a user's Steam Community profile page.
 * @author Andrey Marchenkov
 */
public class SteamID {

    private long accountID;
    private long accountInstance;
    private long accountType;
    private long accountUniverse;

    public SteamID(long steamID) {
        this.accountID = ((steamID << 32) >> 32);
        this.accountInstance = ((steamID >> 32) & 0xFFFFF);
        this.accountType = (((steamID >> 32) >> 20) & 0xF);
        this.accountUniverse = (((steamID >> 32) >> 24) & 0xFF);
    }

    private long encode() {
        long low = this.accountID;
        long high = (this.accountInstance | this.accountType << 20 | this.accountUniverse << 24) << 32;
        return low | high;
    }

    @Override
    public String toString() {
        return String.valueOf(this.encode());
    }

}