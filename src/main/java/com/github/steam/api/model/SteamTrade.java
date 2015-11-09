package com.github.steam.api.model;

import com.github.steam.api.exception.SteamApiException;
import com.github.steam.api.http.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamTrade {

    private SteamUser user;
    private String sessionId;
    private String partnerName;
    private SteamID partner;
    private int tradeOfferID;
    private String inventoryLoadUrl;
    private String partnerInventoryLoadUrl;
    public TradeStatus tradeStatus;
    private HashMap<Long, HashMap<Long, Inventory>> myInventoryCache;
    private HashMap<Long, HashMap<Long, Inventory>> theirInventoryCache;

    /**
     * Creates a new Trade
     *
     * @param steamUser    Стим-пользователь
     * @param tradeOfferID Идентификатор "trade offer"
     * @param partner      Партнер
     * @throws Exception
     */
    protected SteamTrade(SteamUser steamUser, int tradeOfferID, SteamID partner) throws Exception {
        theirInventoryCache = new HashMap<Long, HashMap<Long, Inventory>>();
        myInventoryCache = new HashMap<Long, HashMap<Long, Inventory>>();

        this.user = steamUser;
        this.tradeOfferID = tradeOfferID;

        Gson gson = new Gson();

        String html;
        if (tradeOfferID == 0) {
            System.out.println("http://steamcommunity.com/tradeoffer/new/?partner=" + partner.getAccountId());
            html = steamUser.doCommunityCall("http://steamcommunity.com/tradeoffer/new/?partner=" + partner.getAccountId(), HttpMethod.GET, null, false);
        } else {
            html = steamUser.doCommunityCall("http://steamcommunity.com/tradeoffer/" + tradeOfferID + "/", HttpMethod.GET, null, false);
        }
        Pattern pattern = Pattern.compile("^\\s*var\\s+(g_.+?)\\s+=\\s+(.+?);\\r?$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        Map<String, String> javascriptGlobals = new HashMap<String, String>();
        while (matcher.find()) {
            javascriptGlobals.put(matcher.group(1), matcher.group(2));
        }

        tradeStatus = gson.fromJson(javascriptGlobals.get("g_rgCurrentTradeStatus"), TradeStatus.class);
        partner = new SteamID(Long.parseLong(gson.fromJson(javascriptGlobals.get("g_ulTradePartnerSteamID"), String.class)));
        partnerName = gson.fromJson(javascriptGlobals.get("g_strTradePartnerPersonaName"), String.class);
        sessionId = gson.fromJson(javascriptGlobals.get("g_sessionID"), String.class);
        inventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strInventoryLoadURL"), String.class);
        partnerInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strTradePartnerInventoryLoadURL"), String.class);

        tradeStatus.trade = this;

        tradeStatus.me.tradeStatus = tradeStatus;
        tradeStatus.me.gameContextMap = gson.fromJson(javascriptGlobals.get("g_rgAppContextData"), new TypeToken<Map<String, GameContext>>() {
        }.getType());
        tradeStatus.me.isPartner = false;

        tradeStatus.them.tradeStatus = tradeStatus;
        tradeStatus.them.gameContextMap = gson.fromJson(javascriptGlobals.get("g_rgPartnerAppContextData"), new TypeToken<Map<String, GameContext>>() {
        }.getType());
        tradeStatus.them.isPartner = true;

        String tradeOfferMessage = "";
        String tradeOfferJson = gson.toJson(tradeStatus);


        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(partner.getCommunityId())));
        data.add(new BasicNameValuePair("tradeoffermessage", tradeOfferMessage));
        data.add(new BasicNameValuePair("json_tradeoffer", tradeOfferJson));
        data.add(new BasicNameValuePair("tradeofferid_countered", Integer.toString(tradeOfferID)));
    }

    private Inventory fetchMyInventory(long appId, long contextId) throws Exception {
        if (!myInventoryCache.containsKey(appId)) {
            myInventoryCache.put(appId, new HashMap<Long, Inventory>());
        }

        if (myInventoryCache.get(appId).containsKey(contextId)) {
            return myInventoryCache.get(appId).get(contextId);
        }

        Gson gson = new Gson();
        Inventory inventory = gson.fromJson(user.doCommunityCall(inventoryLoadUrl + appId + "/" + contextId + "/?trading=1", HttpMethod.GET, null, true), Inventory.class);
        inventory.appId = appId;
        inventory.contextId = contextId;
        inventory.updateItems();
        myInventoryCache.get(appId).put(contextId, inventory);
        return inventory;
    }

    private Inventory fetchTheirInventory(long appId, long contextId) throws Exception {
        if (!theirInventoryCache.containsKey(appId)) {
            theirInventoryCache.put(appId, new HashMap<Long, Inventory>());
        }

        if (theirInventoryCache.get(appId).containsKey(contextId)) {
            return theirInventoryCache.get(appId).get(contextId);
        }
        Gson gson = new Gson();

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(partner.getCommunityId())));
        data.add(new BasicNameValuePair("appid", Long.toString(appId)));
        data.add(new BasicNameValuePair("contextid", Long.toString(contextId)));

        Inventory inventory = gson.fromJson(user.doCommunityCall(partnerInventoryLoadUrl, HttpMethod.POST, data, true), Inventory.class);
        inventory.appId = appId;
        inventory.contextId = contextId;
        inventory.updateItems();
        theirInventoryCache.get(appId).put(contextId, inventory);
        return inventory;
    }

    /**
     * Sends a new trade offer OR counter offer, depending on if this is a new trade offer
     *
     * @param message The message to be sent along with the trade offer
     * @throws Exception
     */
    public void update(String message) throws Exception {
        tradeStatus.version++;
        tradeStatus.newversion = true;
        Gson gson = new Gson();
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(partner.getCommunityId())));
        data.add(new BasicNameValuePair("tradeoffermessage", message));
        data.add(new BasicNameValuePair("json_tradeoffer", gson.toJson(tradeStatus)));
        if (tradeOfferID != 0) {
            data.add(new BasicNameValuePair("'tradeofferid_countered'", Integer.toString(tradeOfferID)));
        }
        String result = user.doCommunityCall("http://steamcommunity.com/tradeoffer/new/send", HttpMethod.POST, data, true);
        // TODO: parse/return the result
    }

    /**
     * Accepts the trade
     * After calling this, the trade object will be in an unusable state
     *
     * @throws Exception
     */
    public void accept() throws Exception {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("tradeofferid", Long.toString(tradeOfferID)));

        String result = user.doCommunityCall("http://steamcommunity.com/tradeoffer/" + tradeOfferID + "/accept", HttpMethod.POST, data, true);
        // TODO: parse/return the result
    }

    /**
     * Declines the trade
     * After calling this, the trade object will be in an unusable state
     *
     * @throws SteamApiException
     */
    public void decline() throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(tradeOfferID));
        String result = user.doAPICall("DeclineTradeOffer/v1", HttpMethod.POST, params);
    }

    /*
     * These classes are mostly for GSON purposes
     */
    public class TradeStatus {
        public boolean newversion;
        public int version;
        public TradeStatusUser me;
        public TradeStatusUser them;
        public transient SteamTrade trade;

    }

    public class TradeStatusUser {
        boolean ready;
        private transient boolean isPartner;
        ArrayList<TradeAsset> assets;
        Map<String, GameContext> gameContextMap;
        public transient TradeStatus tradeStatus;

        public Inventory fetchInventory(long appId, long contextId) throws Exception {
            if (isPartner)
                return tradeStatus.trade.fetchTheirInventory(appId, contextId);
            else
                return tradeStatus.trade.fetchMyInventory(appId, contextId);
        }

        public Inventory.Description getDescription(TradeAsset tradeAsset) throws Exception {
            return fetchInventory(tradeAsset.appid, tradeAsset.contextid).getDescription(tradeAsset);
        }

        public boolean addItem(Inventory.Item item) {
            return addItem(item.inventory.appId, item.inventory.contextId, item.id);
        }

        private boolean addItem(long appId, long contextId, String id) {
            TradeAsset tradeAsset = new TradeAsset();
            tradeAsset.appid = appId;
            tradeAsset.contextid = contextId;
            tradeAsset.assetid = Long.parseLong(id);
            addItem(tradeAsset);
            return true;
        }

        public boolean addItem(TradeAsset asset) {
            if (!assets.contains(asset)) {
                assets.add(asset);
                return true;
            }
            return false;
        }

        public boolean removeItem(TradeAsset asset) {
            if (assets.contains(asset)) {
                assets.remove(asset);
                return true;
            }
            return false;
        }

        public boolean containsItem(int appId, int contextId, int assetId) {
            for (TradeAsset tradeAsset : assets) {
                if (tradeAsset.appid == appId && tradeAsset.contextid == contextId && tradeAsset.assetid == assetId)
                    return true;
            }
            return false;
        }
    }

    public class TradeAsset {
        public long appid;
        public long contextid;
        public long amount;
        public long assetid;
    }

    public class Context {
        public int asset_count;
        public String id;
        public String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Context context = (Context) o;

            if (asset_count != context.asset_count) return false;
            if (!id.equals(context.id)) return false;
            if (!name.equals(context.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = asset_count;
            result = 31 * result + id.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    public class GameContext {
        public int appid;
        public String name;
        public String icon;
        public int asset_count;
        public String inventory_logo;
        public String trade_permissions;
        public HashMap<String, Context> rgContexts;
    }

}
