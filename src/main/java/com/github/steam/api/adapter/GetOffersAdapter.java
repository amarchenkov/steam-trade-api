package com.github.steam.api.adapter;

import com.github.steam.api.CEconTradeOffer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetOffersAdapter implements JsonDeserializer<List<CEconTradeOffer>> {

    @Override
    public List<CEconTradeOffer> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject responseNode = jsonElement.getAsJsonObject().getAsJsonObject("response");
        if (responseNode == null) {
            return null;
        }
        Type listType = new TypeToken<List<CEconTradeOffer>>() {
        }.getType();
        List<CEconTradeOffer> tradeOffers = new ArrayList<>();
        tradeOffers.addAll(jsonDeserializationContext.deserialize(responseNode.getAsJsonArray("trade_offers_sent"), listType));
        tradeOffers.addAll(jsonDeserializationContext.deserialize(responseNode.getAsJsonArray("trade_offers_received"), listType));
        return tradeOffers;
    }

}
