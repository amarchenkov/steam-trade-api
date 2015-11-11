package com.github.steam.api;

class CEconTradeStatus {

    private boolean newversion;
    private long version;
    private ETradePartipiant me;
    private ETradePartipiant them;
    private transient ETradeOffer tradeOffer;

    public boolean isNewVersion() {
        return newversion;
    }

    public long getVersion() {
        return version;
    }

    public ETradePartipiant getMe() {
        return me;
    }

    public ETradePartipiant getThem() {
        return them;
    }

    public void setNewVersion(boolean newversion) {
        this.newversion = newversion;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setMe(ETradePartipiant me) {
        this.me = me;
    }

    public void setThem(ETradePartipiant them) {
        this.them = them;
    }

    public ETradeOffer getTradeOffer() {
        return tradeOffer;
    }

    public void setTradeOffer(ETradeOffer tradeOffer) {
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
