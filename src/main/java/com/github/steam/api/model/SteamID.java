package com.github.steam.api.model;

public class SteamID {

    private long communityId;

    public SteamID(long communityId) {
        this.communityId = communityId;
    }

    public long getAccountId() {
        return (communityId & 0xFFFFFFFFL);
    }

    public long getCommunityId() {
        return communityId;
    }

    public String render() {
        long accountId = getAccountId();
        return "STEAM_0:" + (accountId & 1) + ":" + (accountId >> 1);
    }

    @Override
    public String toString() {
        return "SteamID{" +
                "communityId=" + communityId +
                '}';
    }
}
