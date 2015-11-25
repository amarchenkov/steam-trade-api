package com.github.steam.api.adapter;

import com.github.steam.api.CEconAsset;
import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryAdapter implements JsonDeserializer<List<CEconAsset>>, JsonSerializer<List<CEconAsset>> {

    private EAppID appID;
    private EContextID contextID;

    public InventoryAdapter(EAppID appID, EContextID contextID) {
        this.appID = appID;
        this.contextID = contextID;
    }

    public InventoryAdapter() {

    }

    @Override
    public List<CEconAsset> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final List<CEconAsset> result = new ArrayList<>();
        if (jsonElement.getAsJsonObject().getAsJsonPrimitive("success").getAsString().equals("false")) {
            return result;
        }
        JsonObject inventoryNode = new JsonObject();
        JsonObject descriptionNode =  new JsonObject();
        if (!jsonElement.getAsJsonObject().get("rgInventory").isJsonArray()) {
            inventoryNode = jsonElement.getAsJsonObject().getAsJsonObject("rgInventory");
        }
        if (!jsonElement.getAsJsonObject().get("rgDescriptions").isJsonArray()) {
            descriptionNode = jsonElement.getAsJsonObject().getAsJsonObject("rgDescriptions");
        }
        Type mapType = new TypeToken<Map<String, JsonObject>>(){}.getType();
        Map<String, JsonObject> inventoryMap = jsonDeserializationContext.deserialize(inventoryNode, mapType);
        Map<String, JsonObject> descriptionMap = jsonDeserializationContext.deserialize(descriptionNode, mapType);
        for (Map.Entry<String, JsonObject> item : inventoryMap.entrySet()) {
            CEconAsset econAsset = new CEconAsset();
            econAsset.setAmount(item.getValue().getAsJsonPrimitive("amount").getAsInt());
            econAsset.setAssetID(item.getValue().getAsJsonPrimitive("id").getAsLong());
            econAsset.setClassID(item.getValue().getAsJsonPrimitive("classid").getAsString());
            econAsset.setCurrencyID(null);
            econAsset.setInstanceID(item.getValue().getAsJsonPrimitive("instanceid").getAsString());
            econAsset.setMissing(false);
            econAsset.setAppID(this.appID);
            econAsset.setContextID(this.contextID);
            econAsset.setMarketHashName(
                    descriptionMap.get(econAsset.getClassID() + "_" + econAsset.getInstanceID())
                            .getAsJsonPrimitive("market_hash_name").getAsString());
            result.add(econAsset);
        }
        return result;
    }

    @Override
    public JsonElement serialize(List<CEconAsset> assets, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonArray assetsArray = new JsonArray();
        for (CEconAsset asset : assets) {
            JsonObject assetJson = new JsonObject();
            assetJson.addProperty("appid", asset.getAppID().getAppID());
            assetJson.addProperty("contextid", asset.getContextID().getContextID());
            assetJson.addProperty("amount", asset.getAmount());
            assetJson.addProperty("assetid", asset.getAssetID());
            assetsArray.add(assetJson);
        }
        return assetsArray;
    }
}
