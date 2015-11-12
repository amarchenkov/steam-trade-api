package com.github.steam.api.enumeration;

/**
 * Игры Steam
 *
 * @author Andrey Marchenkov
 */
public enum EAppID {

    DOTA2(570, "Dota 2"),
    CSGO(730, "Counter-Strike: Global Offensive"),
    TF2(440, "Team Fortress 2"),
    STEAM(753, "Steam");

    private int appID;
    private String appName;

    EAppID(int appID, String appName) {
        this.appID = appID;
        this.appName = appName;
    }

    public int getAppID() {
        return this.appID;
    }

    public String getAppName() {
        return this.appName;
    }

    public static EAppID valueOf(int appID) {
        for (EAppID state : EAppID.values()) {
            if (state.getAppID() == appID) {
                return state;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(this.appID);
    }
}
