package com.github.steam.api;

import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Both GetTradeOffers and GetTradeOffer return this structure
 *
 * @author Andrey Marchenkov
 */
public class CEconTradeOffer {

    /**
     * A unique identifier for the trade offer
     */
    private String tradeofferid;

    /**
     * Your partner in the trade offer
     */
    private long accountid_other;

    /**
     * A message included by the creator of the trade offer
     */
    private String message;

    /**
     * UNIX time when the offer will expire (or expired, if it is in the past)
     */
    private long expiration_time;

    /**
     * @see ETradeOfferState above
     */
    private ETradeOfferState trade_offer_state;

    /**
     * Array of CEcon_Asset, items you will give up in the trade (regardless of who created the offer)
     */
    private CEconAsset[] items_to_give;

    /**
     * Array of CEcon_Asset, items you will receive in the trade (regardless of who created the offer)
     */
    private CEconAsset[] items_to_receive;

    /**
     * Boolean to indicate this is an offer you created
     */
    private boolean is_our_offer;

    /**
     * UNIX timestamp of the time the offer was sent
     */
    private long time_created;

    /**
     * UNIX timestamp of the time the trade_offer_state last changed
     */
    private long time_updated;

    /**
     * Boolean to indicate this is an offer automatically created from a real-time trade
     */
    private boolean from_real_time_trade;

    private transient SteamUser steamUser;

    public CEconTradeOffer() {
    }

    protected CEconTradeOffer(SteamUser steamUser, int tradeOfferID, SteamID accountIDOther) throws IOException {
        this.tradeofferid = String.valueOf(tradeOfferID);
        this.accountid_other = accountIDOther.getCommunityId();
        this.steamUser = steamUser;

        Gson gson = new Gson();

        String html;
        if (tradeOfferID == 0) {
            html = steamUser.doCommunityCall("https://steamcommunity.com/tradeoffer/new/?partner=" + accountIDOther.getAccountId(), HttpMethod.GET, null, false);
        } else {
            html = steamUser.doCommunityCall("https://steamcommunity.com/tradeoffer/" + tradeOfferID + "/", HttpMethod.GET, null, false);
        }
        Pattern pattern = Pattern.compile("^\\s*var\\s+(g_.+?)\\s+=\\s+(.+?);\\r?$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        Map<String, String> javascriptGlobals = new HashMap<>();
        while (matcher.find()) {
            javascriptGlobals.put(matcher.group(1), matcher.group(2));
        }

        String s1 = javascriptGlobals.get("g_rgCurrentTradeStatus");
        String s2 = javascriptGlobals.get("g_ulTradePartnerSteamID");
        String s3 = javascriptGlobals.get("g_strTradePartnerPersonaName");
        String s4 = javascriptGlobals.get("g_sessionID");
        String s5 = javascriptGlobals.get("g_strInventoryLoadURL");
        String s6 = javascriptGlobals.get("g_strTradePartnerInventoryLoadURL");
        String s7 = javascriptGlobals.get("g_rgAppContextData");
        String s8 = javascriptGlobals.get("g_rgPartnerAppContextData");

//        tradeStatus = gson.fromJson(javascriptGlobals.get("g_rgCurrentTradeStatus"), TradeStatus.class);
//        this.setAccountIDOther(new SteamID(Long.parseLong(gson.fromJson(javascriptGlobals.get("g_ulTradePartnerSteamID"), String.class))).getCommunityId());
//        partnerName = gson.fromJson(javascriptGlobals.get("g_strTradePartnerPersonaName"), String.class);
//        sessionId = gson.fromJson(javascriptGlobals.get("g_sessionID"), String.class);
//        inventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strInventoryLoadURL"), String.class);
//        partnerInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strTradePartnerInventoryLoadURL"), String.class);
//
//        tradeStatus.trade = this;
//
//        tradeStatus.me.tradeStatus = tradeStatus;
//        tradeStatus.me.gameContextMap = gson.fromJson(javascriptGlobals.get("g_rgAppContextData"), new TypeToken<Map<String, GameContext>>() {
//        }.getType());
//        tradeStatus.me.isPartner = false;
//
//        tradeStatus.them.tradeStatus = tradeStatus;
//        tradeStatus.them.gameContextMap = gson.fromJson(javascriptGlobals.get("g_rgPartnerAppContextData"), new TypeToken<Map<String, GameContext>>() {
//        }.getType());
//        tradeStatus.them.isPartner = true;
//
//        String tradeOfferMessage = "";
//        String tradeOfferJson = gson.toJson(tradeStatus);
    }

    public String getTradeOfferID() {
        return tradeofferid;
    }

    public void setTradeOfferID(String tradeofferid) {
        this.tradeofferid = tradeofferid;
    }

    public long getAccountIDOther() {
        return accountid_other;
    }

    public void setAccountIDOther(long accountid_other) {
        this.accountid_other = accountid_other;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getExpirationTime() {
        return expiration_time;
    }

    public void setExpirationTime(long expiration_time) {
        this.expiration_time = expiration_time;
    }

    public ETradeOfferState getTradeOfferState() {
        return trade_offer_state;
    }

    public void setTradeOfferState(ETradeOfferState trade_offer_state) {
        this.trade_offer_state = trade_offer_state;
    }

    public CEconAsset[] getItemsToGive() {
        return items_to_give;
    }

    public void setItemsToGive(CEconAsset[] items_to_give) {
        this.items_to_give = items_to_give;
    }

    public CEconAsset[] getItemsToReceive() {
        return items_to_receive;
    }

    public void setItemsToReceive(CEconAsset[] items_to_receive) {
        this.items_to_receive = items_to_receive;
    }

    public boolean isOurOffer() {
        return is_our_offer;
    }

    public void setIsOurOffer(boolean is_our_offer) {
        this.is_our_offer = is_our_offer;
    }

    public long getTimeCreated() {
        return time_created;
    }

    public void setTimeCreated(long time_created) {
        this.time_created = time_created;
    }

    public long getTimeUpdated() {
        return time_updated;
    }

    public void setTimeUpdated(long time_updated) {
        this.time_updated = time_updated;
    }

    public boolean isFromRealTimeTrade() {
        return from_real_time_trade;
    }

    public void setFromRealTimeTrade(boolean from_real_time_trade) {
        this.from_real_time_trade = from_real_time_trade;
    }


    /**
     * Sends a new trade offer OR counter offer, depending on if this is a new trade offer
     *
     * @param message The message to be sent along with the trade offer
     * @throws Exception
     */
    public void update(String message) throws Exception {
//        tradeStatus.version++;
//        tradeStatus.newversion = true;
        Gson gson = new Gson();
        List<NameValuePair> data = new ArrayList<NameValuePair>();
//        data.add(new BasicNameValuePair("sessionid", sessionId));
//        data.add(new BasicNameValuePair("partner", Long.toString(partner.getCommunityId())));
        data.add(new BasicNameValuePair("tradeoffermessage", message));
//        data.add(new BasicNameValuePair("json_tradeoffer", gson.toJson(tradeStatus)));
        if (!Objects.equals(getTradeOfferID(), "0")) {
            data.add(new BasicNameValuePair("'tradeofferid_countered'", getTradeOfferID()));
        }
        String result = steamUser.doCommunityCall("https://steamcommunity.com/tradeoffer/new/send", HttpMethod.POST, data, true);
        // TODO: parse/return the result
    }

    /**
     * Принять предложение обмена
     * После вызова дальнейшее использование объекта невозможно
     *
     * @throws Exception
     */
    public void accept() throws Exception {
        List<NameValuePair> data = new ArrayList<>();
//        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("tradeofferid", getTradeOfferID()));

        String result = steamUser.doCommunityCall("https://steamcommunity.com/tradeoffer/" + getTradeOfferID() + "/accept", HttpMethod.POST, data, true);
        // TODO: parse/return the result
    }

    /**
     * Отклонение предложения обмена
     * После вызова дальнейшее использование объекта невозможно
     *
     * @throws SteamException
     */
    public void decline() throws SteamException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(getTradeOfferID()));
        String result = steamUser.doAPICall("DeclineTradeOffer/v1", HttpMethod.POST, params);
    }

//    private Inventory fetchMyInventory(long appId, long contextId) throws Exception {
//        if (!myInventoryCache.containsKey(appId)) {
//            myInventoryCache.put(appId, new HashMap<Long, Inventory>());
//        }
//
//        if (myInventoryCache.get(appId).containsKey(contextId)) {
//            return myInventoryCache.get(appId).get(contextId);
//        }
//
//        Gson gson = new Gson();
//        Inventory inventory = gson.fromJson(user.doCommunityCall(inventoryLoadUrl + appId + "/" + contextId + "/?trading=1", HttpMethod.GET, null, true), Inventory.class);
//        inventory.appId = appId;
//        inventory.contextId = contextId;
//        inventory.updateItems();
//        myInventoryCache.get(appId).put(contextId, inventory);
//        return inventory;
//    }
//
//    private Inventory fetchTheirInventory(long appId, long contextId) throws Exception {
//        if (!theirInventoryCache.containsKey(appId)) {
//            theirInventoryCache.put(appId, new HashMap<Long, Inventory>());
//        }
//
//        if (theirInventoryCache.get(appId).containsKey(contextId)) {
//            return theirInventoryCache.get(appId).get(contextId);
//        }
//        Gson gson = new Gson();
//
//        List<NameValuePair> data = new ArrayList<NameValuePair>();
//        data.add(new BasicNameValuePair("sessionid", sessionId));
//        data.add(new BasicNameValuePair("partner", Long.toString(partner.getCommunityId())));
//        data.add(new BasicNameValuePair("appid", Long.toString(appId)));
//        data.add(new BasicNameValuePair("contextid", Long.toString(contextId)));
//
//        Inventory inventory = gson.fromJson(user.doCommunityCall(partnerInventoryLoadUrl, HttpMethod.POST, data, true), Inventory.class);
//        inventory.appId = appId;
//        inventory.contextId = contextId;
//        inventory.updateItems();
//        theirInventoryCache.get(appId).put(contextId, inventory);
//        return inventory;
//    }

    @Override
    public String toString() {
        return "CEconTradeOffer{" +
                "tradeofferid='" + tradeofferid + '\'' +
                ", accountid_other='" + accountid_other + '\'' +
                ", message='" + message + '\'' +
                ", expiration_time=" + expiration_time +
                ", trade_offer_state=" + trade_offer_state +
                ", items_to_give=" + Arrays.toString(items_to_give) +
                ", items_to_receive=" + Arrays.toString(items_to_receive) +
                ", is_our_offer=" + is_our_offer +
                ", time_created=" + time_created +
                ", time_updated=" + time_updated +
                ", from_real_time_trade=" + from_real_time_trade +
                '}';
    }
}
