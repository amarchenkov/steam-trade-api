package com.github.steam.api.adapter;

import com.github.steam.api.CEconAsset;
import com.github.steam.api.CEconTradeOffer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {
 "response": {
 "trade_offers_sent": [
 {
 "tradeofferid": "853093299",
 "accountid_other": 97360461,
 "message": "",
 "expiration_time": 1448970308,
 "trade_offer_state": 2,
 "items_to_receive": [
 {
 "appid": "570",
 "contextid": "2",
 "assetid": "2423603122",
 "classid": "190098375",
 "instanceid": "0",
 "amount": "1",
 "missing": false
 }
 ]
 ,
 "is_our_offer": true,
 "time_created": 1447760708,
 "time_updated": 1447760708,
 "from_real_time_trade": false
 }
 ]

 }
 }
 */
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
