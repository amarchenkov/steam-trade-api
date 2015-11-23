package com.github.steam.api.adapter;

import com.github.steam.api.CEconAsset;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

public class InventoryAdapter implements JsonDeserializer<List<CEconAsset>> {

    @Override
    public List<CEconAsset> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

}
