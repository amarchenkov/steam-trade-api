package com.github.steam.api.adapter;

import com.github.steam.api.enumeration.EContextID;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ContextIdAdapter implements JsonDeserializer<EContextID> {

    @Override
    public EContextID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return EContextID.valueOf(jsonElement.getAsInt());
    }

}
