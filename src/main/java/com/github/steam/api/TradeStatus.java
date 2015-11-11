package com.github.steam.api;

class TradeStatus {

    private boolean newversion;
    private long version;
    private TradePartipiant me;
    private TradePartipiant them;

    public boolean isNewVersion() {
        return newversion;
    }

    public long getVersion() {
        return version;
    }

    public TradePartipiant getMe() {
        return me;
    }

    public TradePartipiant getThem() {
        return them;
    }

    public void setNewVersion(boolean newversion) {
        this.newversion = newversion;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setMe(TradePartipiant me) {
        this.me = me;
    }

    public void setThem(TradePartipiant them) {
        this.them = them;
    }

    @Override
    public String toString() {
        return "TradeStatus{" +
                "newversion=" + newversion +
                ", version=" + version +
                ", me=" + me +
                ", them=" + them +
                '}';
    }

}
