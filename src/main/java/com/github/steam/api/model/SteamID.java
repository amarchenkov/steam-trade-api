package com.github.steam.api.model;

import com.github.steam.api.http.HttpHelper;

public class SteamID {

    private final long accountID;
    private final long accountInstance;
    private final long accountType;
    private final long accountUniverse;

    private HttpHelper httpHelper = new HttpHelper();

    public SteamID(long steamID) {
        this.accountID = ((steamID >> 32) << 32);
        this.accountInstance = Long.highestOneBit(steamID) & 0xFFFFF;
        this.accountType = Long.highestOneBit(steamID) >> 20 & 0xF;
        this.accountUniverse = Long.highestOneBit(steamID) >> 24 & 0xFF;
    }

    //    public SteamID(String accountName) {
//    }

//    public SteamID(long accountID, long accountInstance, long accountType, long accountUniverse) {
//        this.accountID = accountID;
//        this.accountInstance = accountInstance;
//        this.accountType = accountType;
//        this.accountUniverse = accountUniverse;
//    }

    public String getLegacyRepresentation() {
        return "STEAM_X:Y:Z";
    }

    public String getModernRepresentation() {
        return "[C:U:A]";
    }

    @Override
    public String toString() {
        long textRepresentation = 0L;
//        textRepresentation = textRepresentation | (accountID << 32) | () | ();
        return "";
    }

}