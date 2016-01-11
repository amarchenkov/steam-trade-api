package com.github.steam.api;

import java.io.Serializable;

public class SteamID implements Serializable {

    private long communityId;

    public SteamID() {
    }

    public SteamID(long communityId) {
        this.communityId = communityId;
    }

    public long getAccountId() {
        return (communityId & 0xFFFFFFFFL);
    }

    public long getCommunityId() {
        return communityId;
    }

    public static long getCommunityIdByAccountId(long accountId) {
        long universe = 1L;
        long accountType = 1L;
        long instance = 1L;
        return ((universe << 56) | (accountType << 52) | (instance << 32) | accountId);
    }

    public String render() {
        long accountId = getAccountId();
        return "STEAM_0:" + (accountId & 1) + ":" + (accountId >> 1);
    }

    public static SteamID valueOf(long communityId) {
        return new SteamID(communityId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SteamID steamID = (SteamID) obj;

        return communityId == steamID.getCommunityId();

    }

    @Override
    public int hashCode() {
        return new Long(communityId).hashCode();
    }

    @Override
    public String toString() {
        return "SteamID{" +
                "communityId=" + communityId +
                ",accountId=" + this.getAccountId() +
                ",textPresentation=" + this.render() +
                '}';
    }
}
