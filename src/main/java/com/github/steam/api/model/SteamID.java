package com.github.steam.api.model;

public class SteamID {

    private final long accountID;
    private final long accountInstance;
    private final long accountType;
    private final long accountUniverse;

    public SteamID(long accountID, long accountInstance, long accountType, long accountUniverse) {
        this.accountID = accountID;
        this.accountInstance = accountInstance;
        this.accountType = accountType;
        this.accountUniverse = accountUniverse;
    }

    public static SteamID valueOf(long steamID) {
        long a = Long.lowestOneBit(steamID);
        long b = Long.highestOneBit(steamID);
        long c = Long.highestOneBit(steamID) & 0xFFFFF;
        long d = Long.highestOneBit(steamID) >> 20 & 0xF;
        long e = Long.highestOneBit(steamID) >> 24 & 0xFF;
        return new SteamID(Long.lowestOneBit(steamID), Long.highestOneBit(steamID) & 0xFFFFF, Long.highestOneBit(steamID) >> 20 & 0xF, Long.highestOneBit(steamID) >> 24 & 0xFF);
    }

    @Override
    public String toString() {
        long a = this.accountID;
        long b = this.accountInstance | this.accountType << 20 | this.accountUniverse << 24;
//        return Long.this.accountID) + String.valueOf(this.accountInstance | this.accountType << 20 | this.accountUniverse << 24);
        return "";
    }

}