package com.github.steam.api;

import java.util.HashMap;

public class CEconInventory {

    private boolean success;
    private HashMap<String, CEconInventoryItem> rgInventory;
    private HashMap<String, CEconInventoryDescription> rgDescriptions;
    private HashMap<String, String> reAppInfo;

    public boolean isSuccess() {
        return success;
    }

    public HashMap<String, CEconInventoryItem> getRgInventory() {
        return rgInventory;
    }

    public HashMap<String, CEconInventoryDescription> getRgDescriptions() {
        return rgDescriptions;
    }

    public HashMap<String, String> getReAppInfo() {
        return reAppInfo;
    }

    public CEconInventoryDescription getDescription(CEconAsset tradeAsset) {
        return rgDescriptions.get(tradeAsset.getClassID() + "_" + tradeAsset.getInstanceID());
    }
}
