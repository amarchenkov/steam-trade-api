package com.github.steam.api.adapter;

import com.github.steam.api.enumeration.EAppID;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AppIdAdapter implements JsonDeserializer<EAppID> {

    @Override
    public EAppID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return EAppID.valueOf(jsonElement.getAsInt());
    }

}
