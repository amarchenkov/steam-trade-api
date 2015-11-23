package com.github.steam.api.adapter;

import com.github.steam.api.CEconAsset;
import com.github.steam.api.CEconTradeOffer;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GetOfferAdapter implements JsonDeserializer<CEconTradeOffer> {

    @Override
    public CEconTradeOffer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject responseNode = jsonElement.getAsJsonObject().getAsJsonObject("response");
        JsonObject offerNode = responseNode.getAsJsonObject("offer");
        if (offerNode == null) {
            return null;
        }
        CEconTradeOffer econTradeOffer = new CEconTradeOffer();
        econTradeOffer.setTradeOfferID(offerNode.getAsJsonPrimitive("tradeofferid").getAsLong());
        econTradeOffer.setAccountIdOther(offerNode.getAsJsonPrimitive("accountid_other").getAsLong());
        econTradeOffer.setExpirationTime(offerNode.getAsJsonPrimitive("expiration_time").getAsLong());
        econTradeOffer.setFromRealTimeTrade(offerNode.getAsJsonPrimitive("from_real_time_trade").getAsBoolean());
        econTradeOffer.setIsOurOffer(offerNode.getAsJsonPrimitive("is_our_offer").getAsBoolean());
        econTradeOffer.setMessage(offerNode.getAsJsonPrimitive("message").getAsString());
        econTradeOffer.setTimeCreated(offerNode.getAsJsonPrimitive("time_created").getAsLong());
        econTradeOffer.setTimeUpdated(offerNode.getAsJsonPrimitive("time_updated").getAsLong());
        econTradeOffer.setTradeOfferState(ETradeOfferState.valueOf(offerNode.getAsJsonPrimitive("trade_offer_state").getAsInt()));
        Type listType = new TypeToken<List<CEconAsset>>() {
        }.getType();
        econTradeOffer.setItemsToReceive(jsonDeserializationContext.deserialize(offerNode.getAsJsonArray("items_to_receive"), listType));
        econTradeOffer.setItemsToGive(jsonDeserializationContext.deserialize(offerNode.getAsJsonArray("items_to_give"), listType));
        return econTradeOffer;
    }

}
