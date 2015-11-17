package com.github.steam.api.adapter;

import com.github.steam.api.CEconTradeOffer;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GetOfferAdapter implements JsonDeserializer<CEconTradeOffer> {

    @Override
    public CEconTradeOffer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject jsonObject1 = jsonObject.getAsJsonObject("response");
        JsonObject jsonObject2 = jsonObject1.getAsJsonObject("offer");
        return null;
    }

}
