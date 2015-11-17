package com.github.steam.api.adapter;

import com.github.steam.api.CEconTradeOffer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ApiResponseAdapter implements JsonDeserializer<CEconTradeOffer> {

    @Override
    public CEconTradeOffer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

}
