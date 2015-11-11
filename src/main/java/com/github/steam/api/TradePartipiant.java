package com.github.steam.api;

import java.util.List;
import java.util.Map;

class TradePartipiant {

    private boolean ready;
    private Inventory assets;
    private transient boolean isPartner;
    private transient Map<String, GameContext> gameContext;
    private transient List<CEconAsset> inventoryItems;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Inventory getAssets() {
        return assets;
    }

    public void setAssets(Inventory assets) {
        this.assets = assets;
    }

    public boolean isPartner() {
        return isPartner;
    }

    public void setPartner(boolean partner) {
        isPartner = partner;
    }

    public Map<String, GameContext> getGameContext() {
        return gameContext;
    }

    public void setGameContext(Map<String, GameContext> gameContext) {
        this.gameContext = gameContext;
    }

    public void setInventoryItems(List<CEconAsset> items) {
        this.inventoryItems = items;

    }

    public List<CEconAsset> getInventoryItems() {
        return this.inventoryItems;
    }

    private boolean addItem(long appId, long contextId, long id) {
        CEconAsset asset = new CEconAsset();
        asset.setAppID(appId);
        asset.setContextID(contextId);
        asset.setAssetID(id);
        addItem(asset);
        return true;
    }

    public boolean addItem(CEconAsset asset) {
        if (!assets.contains(asset)) {
            assets.add(asset);
            return true;
        }
        return false;
    }

    public boolean removeItem(CEconAsset asset) {
        if (assets.contains(asset)) {
            assets.remove(asset);
            return true;
        }
        return false;
    }

    public boolean containsItem(int appId, int contextId, int assetId) {
        for (CEconAsset tradeAsset : assets) {
            if (tradeAsset.getAppID() == appId && tradeAsset.getContextID() == contextId && tradeAsset.getAssetID() == assetId)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TradeStatusUser{" +
                "ready=" + ready +
                ", assets=" + assets +
                ", isPartner=" + isPartner +
                ", gameContext=" + gameContext +
                ", inventoryLoadUrl='" + inventoryLoadUrl + '\'' +
                '}';
    }

}
