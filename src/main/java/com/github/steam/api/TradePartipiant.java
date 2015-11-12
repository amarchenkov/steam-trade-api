package com.github.steam.api;

import java.util.List;
import java.util.Map;

class TradePartipiant {

    private boolean ready;
    private List<CEconAsset> assets;
    private transient boolean isPartner;
    private transient CEconTradeStatus tradeStatus;
    private transient Map<String, CEconGameContext> gameContext;

    public boolean isPartner() {
        return isPartner;
    }

    public void setPartner(boolean partner) {
        isPartner = partner;
    }

    public Map<String, CEconGameContext> getGameContext() {
        return gameContext;
    }

    public void setGameContext(Map<String, CEconGameContext> gameContext) {
        this.gameContext = gameContext;
    }

    public CEconInventory fetchInventory(long appId, long contextId) throws Exception {
        if (isPartner) {
            return tradeStatus.getTradeOffer().fetchTheirInventory(appId, contextId);
        } else {
            return tradeStatus.getTradeOffer().fetchMyInventory(appId, contextId);
        }
    }

    public CEconInventoryDescription getDescription(CEconAsset tradeAsset) throws Exception {
        return fetchInventory(tradeAsset.getAppID(), tradeAsset.getContextID()).getDescription(tradeAsset);
    }

    private boolean addItem(long appId, long contextId, String id) {
        CEconAsset tradeAsset = new CEconAsset();
        tradeAsset.setAppID(appId);
        tradeAsset.setContextID(contextId);
        tradeAsset.setAssetID(Long.parseLong(id));
        addItem(tradeAsset);
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

    public void setTradeStatus(CEconTradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Override
    public String toString() {
        return "TradeStatusUser{" +
                "ready=" + ready +
                ", assets=" + assets +
                ", isPartner=" + isPartner +
                ", gameContext=" + gameContext +
                '}';
    }

}
