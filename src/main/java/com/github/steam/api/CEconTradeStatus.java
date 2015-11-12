package com.github.steam.api;

class CEconTradeStatus {

    private boolean newversion;
    private long version;
    private TradePartipiant me;
    private TradePartipiant them;
    private transient CEconTradeOffer tradeOffer;

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

    public CEconTradeOffer getTradeOffer() {
        return tradeOffer;
    }

    public void setTradeOffer(CEconTradeOffer tradeOffer) {
        this.tradeOffer = tradeOffer;
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
