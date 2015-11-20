package com.github.steam.api;

//TODO Сделать inner-классом
class CEconTradeStatus {

    private boolean newversion;
    private long version;
    private CEconTradePartipiant me;
    private CEconTradePartipiant them;

    public boolean isNewVersion() {
        return newversion;
    }

    public long getVersion() {
        return version;
    }

    public CEconTradePartipiant getMe() {
        return me;
    }

    public CEconTradePartipiant getThem() {
        return them;
    }

    public void setNewVersion(boolean newversion) {
        this.newversion = newversion;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setMe(CEconTradePartipiant me) {
        this.me = me;
    }

    public void setThem(CEconTradePartipiant them) {
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
