package com.github.steam.api;

import java.util.List;

//TODO Сделать inner-классом
class CEconTradePartipiant {

    private boolean ready;

    private List<CEconAsset> assets;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public List<CEconAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<CEconAsset> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "TradePartipiant{" +
                "ready=" + ready +
                ", assets=" + assets +
                '}';
    }
}
