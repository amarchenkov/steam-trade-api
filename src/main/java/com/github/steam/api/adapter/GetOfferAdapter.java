package com.github.steam.api.adapter;

import com.github.steam.api.CEconTradeOffer;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.google.gson.*;

import java.lang.reflect.Type;


/**
 * {
 "response": {
 "offer": {
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
 },
 "descriptions": [
 {
 "appid": 570,
 "classid": "190098375",
 "instanceid": "0",
 "currency": false,
 "background_color": "",
 "icon_url": "W_I_5GLm4wPcv9jJQ7z7tz_l_0sEIYUhRfbF4arNQkgGQGKd3kMuVpMgCwRZrg-fckaVmPhFb9zVDjE6EpfvVkGvJ5Kezka0V9RCaJtxMWom04WH1RFNFGGCR_V7S4h0-MaXh0inCwHDLDc9zYoC16DGiJfSCfSBrEDvdQZw0LE6DJ0bn01TYDJjyTNmd7E0O1NgoczNig",
 "icon_url_large": "W_I_5GLm4wPcv9jJQ7z7tz_l_0sEIYUhRfbF4arNQkgGQGKd3kMuVpMgCwRZrg-fckaVmPhFb9zVDjE6EpfvVkGvJ5Kezka0V9RCaJtxMWom04WH1RFNFGGCR_V7S4h0-MaXh0jWUVLTLmcjwopSgKbJ1M-HXPWCrEbvIgB5huM_Cc5AyR8AZDVjnztndvolNw0YrkUku1dPn92IUA",
 "descriptions": [
 {
 "type": "html",
 "value": "Used By: Pudge"
 },
 {
 "type": "html",
 "value": " "
 },
 {
 "type": "html",
 "value": "The Ol' Chopper",
 "color": "9da1a9"
 },
 {
 "type": "html",
 "value": "The Ol' Chopper's Grizzled Face",
 "color": "6c7075"
 },
 {
 "type": "html",
 "value": "The Ol' Chopper's Cleaver",
 "color": "6c7075"
 },
 {
 "type": "html",
 "value": "The Ol' Chopper's Choppin' Arm",
 "color": "6c7075"
 },
 {
 "type": "html",
 "value": "The Ol' Chopper's Chains",
 "color": "6c7075"
 },
 {
 "type": "html",
 "value": "The Ol' Chopper's Meat Jacket",
 "color": "6c7075"
 },
 {
 "type": "html",
 "value": "The Ol' Chopper's Dinner Bringer",
 "color": "6c7075"
 },
 {
 "type": "html",
 "value": "This the arm I do me choppin' with!"
 }
 ]
 ,
 "tradable": true,
 "name": "The Ol' Chopper's Choppin' Arm",
 "name_color": "D2D2D2",
 "type": "Common Arm",
 "market_name": "Ol' Chopper's Choppin' Arm",
 "market_hash_name": "Ol' Chopper's Choppin' Arm",
 "commodity": false,
 "market_tradable_restriction": 7,
 "market_marketable_restriction": 7
 }
 ]

 }
 }
 */
public class GetOfferAdapter implements JsonDeserializer<CEconTradeOffer> {

    @Override
    public CEconTradeOffer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject responseNode = jsonElement.getAsJsonObject().getAsJsonObject("response");
        JsonObject offerNode = responseNode.getAsJsonObject("offer");
        if (offerNode == null) {
            throw new JsonParseException("There is no [offer] element");
        }
        CEconTradeOffer econTradeOffer = new CEconTradeOffer();
        econTradeOffer.setTradeOfferID(offerNode.getAsJsonObject("tradeofferid").getAsLong());
        econTradeOffer.setAccountIdOther(offerNode.getAsJsonObject("accountid_other").getAsLong());
        econTradeOffer.setExpirationTime(offerNode.getAsJsonObject("expiration_time").getAsLong());
        econTradeOffer.setFromRealTimeTrade(offerNode.getAsJsonObject("from_real_time_trade").getAsBoolean());
        econTradeOffer.setIsOurOffer(offerNode.getAsJsonObject("is_our_offer").getAsBoolean());
        econTradeOffer.setMessage(offerNode.getAsJsonObject("message").getAsString());
        econTradeOffer.setTimeCreated(offerNode.getAsJsonObject("time_created").getAsLong());
        econTradeOffer.setTimeUpdated(offerNode.getAsJsonObject("time_updated").getAsLong());
        econTradeOffer.setTradeOfferState(ETradeOfferState.valueOf(offerNode.getAsJsonObject("trade_offer_state").getAsInt()));
        return econTradeOffer;
    }

}
