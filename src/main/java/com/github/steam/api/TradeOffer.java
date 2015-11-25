package com.github.steam.api;

import com.github.steam.api.adapter.InventoryAdapter;
import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.IEconServiceException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Модель предложения обмена
 *
 * @author Andrey Marchenkov
 */
public class TradeOffer {

    private static final Log LOG = LogFactory.getLog(TradeOffer.class);

    private TradeUser tradeUser;
    private String themInventoryLoadUrl;
    private String meInventoryLoadUrl;
    private String sessionId;
    private CEconTradeOffer tradeOfferData;
    private int version;

    /**
     * Новое предложение обмена
     *
     * @param tradeUser Пользователь от имени которого осуществляется обмен
     * @param partnerID Идентификатор партнера по обмену
     * @throws IEconServiceException
     */
    protected TradeOffer(TradeUser tradeUser, SteamID partnerID) throws IEconServiceException {
        this(tradeUser, null, "https://steamcommunity.com/tradeoffer/new/?partner=" + partnerID.getAccountId());

        this.tradeOfferData = new CEconTradeOffer();
        this.tradeOfferData.setAccountIdOther(partnerID.getAccountId());
        this.tradeOfferData.setIsOurOffer(true);
        this.tradeOfferData.setTimeCreated(System.currentTimeMillis());
        this.tradeOfferData.setTimeUpdated(System.currentTimeMillis());
        this.tradeOfferData.setTradeOfferState(ETradeOfferState.k_ETradeOfferStateActive);
        this.tradeOfferData.setMessage("");
    }

    /**
     * Существующее предложение обмена
     *
     * @param tradeUser      Пользователь от имени которого осуществляется обмен
     * @param tradeOfferData СОдержание предложения обмена
     * @throws IEconServiceException
     */
    protected TradeOffer(TradeUser tradeUser, CEconTradeOffer tradeOfferData) throws IEconServiceException {
        this(tradeUser, tradeOfferData, "https://steamcommunity.com/tradeoffer/" + tradeOfferData.getTradeOfferID() + "/");
    }

    private TradeOffer(TradeUser tradeUser, CEconTradeOffer tradeOfferData, String url) throws IEconServiceException {
        this.tradeOfferData = tradeOfferData;
        this.tradeUser = tradeUser;

        if (tradeOfferData == null || !tradeOfferData.isOurOffer()) {
            String html = tradeUser.doCommunityCall(url, HttpMethod.GET, null, false);

            Pattern pattern = Pattern.compile("^\\s*var\\s+(g_.+?)\\s+=\\s+(.+?);\\r?$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(html);
            Map<String, String> javascriptGlobals = new HashMap<>();
            while (matcher.find()) {
                javascriptGlobals.put(matcher.group(1), matcher.group(2));
            }
            LOG.debug(MessageFormat.format("Globals = [{0}]", javascriptGlobals));
            Gson gson = new Gson();
            this.sessionId = gson.fromJson(javascriptGlobals.get("g_sessionID"), String.class);
            this.meInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strInventoryLoadURL"), String.class);
            this.themInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strTradePartnerInventoryLoadURL"), String.class);
        }
    }

    /**
     * Получить содержимое инвентаря авторизованного пользователя Steam
     *
     * @param appID     Идентификатор приложения
     * @param contextID Тип предмета
     * @return Инвентарь
     * @throws IEconServiceException
     */
    public List<CEconAsset> getMyInventory(EAppID appID, EContextID contextID) throws IEconServiceException {
        Type type = new TypeToken<List<CEconAsset>>() {
        }.getType();
        return new GsonBuilder().registerTypeAdapter(type, new InventoryAdapter(appID, contextID)).create()
                .fromJson(tradeUser.doCommunityCall(this.meInventoryLoadUrl + appID + "/" + contextID + "/?trading=1",
                        HttpMethod.GET, null, true), type);
    }

    /**
     * Получить содержимое инвентаря другого пользователя
     *
     * @param appID     Идентификатор приложения
     * @param contextID Тип предмета
     * @return Инвентарь
     * @throws IEconServiceException
     */
    public List<CEconAsset> getTheirInventory(EAppID appID, EContextID contextID) throws IEconServiceException {
        try {
            URI uri = new URIBuilder(this.themInventoryLoadUrl)
                    .setParameter("sessionid", sessionId)
                    .setParameter("partner", String.valueOf(SteamID.getCommunityIdByAccountId(this.tradeOfferData.getAccountIdOther())))
                    .setParameter("appid", Long.toString(appID.getAppID()))
                    .setParameter("contextid", Long.toString(contextID.getContextID()))
                    .build();
            Type type = new TypeToken<List<CEconAsset>>() {
            }.getType();
            return new GsonBuilder()
                    .registerTypeAdapter(type, new InventoryAdapter(appID, contextID)).create()
                    .fromJson(tradeUser.doCommunityCall(uri.toString(), HttpMethod.GET, null, true), type);
        } catch (URISyntaxException e) {
            throw new IEconServiceException("Wrong URL for getting inventory", e);
        }
    }

    /**
     * Отправить новое предложение обмена или контрпредложение
     *
     * @throws IEconServiceException
     */
    public void send() throws IEconServiceException {
        this.version++;
        if (!this.tradeOfferData.isOurOffer()) {
            throw new IEconServiceException("Offer has already sent");
        }
        CEconTradePartipiant me = new CEconTradePartipiant();
        me.ready = false;
        me.assets = this.tradeOfferData.getItemsToGive();

        CEconTradePartipiant partner = new CEconTradePartipiant();
        partner.ready = false;
        partner.assets = this.tradeOfferData.getItemsToReceive();

        CEconTradeStatus tradeStatus = new CEconTradeStatus();
        tradeStatus.newVersion = true;
        tradeStatus.version = this.version;
        tradeStatus.me = me;
        tradeStatus.them = partner;

        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<List<CEconAsset>>() {
        }.getType(), new InventoryAdapter()).create();
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(SteamID.getCommunityIdByAccountId(this.tradeOfferData.getAccountIdOther()))));
        data.add(new BasicNameValuePair("tradeoffermessage", this.tradeOfferData.getMessage()));
        data.add(new BasicNameValuePair("serverid", "1"));
        String s = gson.toJson(tradeStatus);
        data.add(new BasicNameValuePair("json_tradeoffer", gson.toJson(tradeStatus)));
        if (tradeStatus.version != 1) {
            data.add(new BasicNameValuePair("tradeofferid_countered", String.valueOf(this.tradeOfferData.getTradeOfferID())));
        }
        String response = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/new/send", HttpMethod.POST, data, true);
        LOG.debug(MessageFormat.format("[/tradeoffer/new/send] response = [{0}]", response));
        if (response.equals("null")) {
            throw new IEconServiceException("Wrong response. TradeOfferID expected");
        }
        this.tradeOfferData.setTradeOfferID(new JsonParser().parse(response).getAsJsonObject().get("tradeofferid").getAsLong());
    }

    /**
     * Принять предложение обмена
     * После вызова дальнейшее использование объекта невозможно
     *
     * @throws IEconServiceException
     */
    public void accept() throws IEconServiceException {
        if (this.tradeOfferData.isOurOffer()) {
            throw new IEconServiceException("You cannot accept your offer. Only partner can do that.");
        }
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionid", this.sessionId));
        data.add(new BasicNameValuePair("tradeofferid", String.valueOf(this.tradeOfferData.getTradeOfferID())));

        String response = tradeUser.doCommunityCall(
                "https://steamcommunity.com/tradeoffer/" + this.tradeOfferData.getTradeOfferID() + "/accept",
                HttpMethod.POST, data, true);
        LOG.debug(MessageFormat.format("[/tradeoffer/accept] response = [{0}]", response));
        // TODO: parse/return the result
    }

    /**
     * Отклонение предложения обмена
     * После вызова дальнейшее использование объекта невозможно
     *
     * @throws IEconServiceException
     */
    public void decline() throws IEconServiceException {
        if (this.tradeOfferData.isOurOffer()) {
            throw new IEconServiceException("You cannot decline your offer. Only partner can do that.");
        }
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(this.tradeOfferData.getTradeOfferID()));
        tradeUser.doAPICall("DeclineTradeOffer/v1", HttpMethod.POST, params);
    }

    /**
     * Отменить исходящее предложение обмена
     *
     * @throws IEconServiceException
     */
    public void cancel() throws IEconServiceException {
        if (!this.tradeOfferData.isOurOffer()) {
            throw new IEconServiceException("You cannot cancel partner's offer.");
        }
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(this.tradeOfferData.getTradeOfferID()));
        tradeUser.doAPICall("CancelTradeOffer/v1", HttpMethod.POST, params);
    }

    public void addItemsToGive(CEconAsset itemToGive) {
        this.tradeOfferData.addItemsToGive(itemToGive);
    }

    public void addItemsToReceive(CEconAsset itemToReceive) {
        this.tradeOfferData.addItemsToReceive(itemToReceive);
    }

    public void removeItemsToGive(CEconAsset itemToGive) {
        this.tradeOfferData.getItemsToGive().remove(itemToGive);
    }

    public void removeItemsToReceive(CEconAsset itemToReceive) {
        this.tradeOfferData.getItemsToReceive().remove(itemToReceive);
    }

    private class CEconTradeStatus {

        @SerializedName("newversion")
        private boolean newVersion;

        @SerializedName("version")
        private long version;

        @SerializedName("me")
        private CEconTradePartipiant me;

        @SerializedName("them")
        private CEconTradePartipiant them;

    }

    private class CEconTradePartipiant {

        @SerializedName("ready")
        private boolean ready;

        @SerializedName("assets")
        private List<CEconAsset> assets;

    }
}