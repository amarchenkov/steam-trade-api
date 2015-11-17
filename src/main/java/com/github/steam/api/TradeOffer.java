package com.github.steam.api;

import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.IEconServiceException;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeOffer {

    private static final Log LOG = LogFactory.getLog(TradeOffer.class);

    private TradeUser tradeUser;
    private String themInventoryLoadUrl;
    private String meInventoryLoadUrl;
    private String sessionId;
    private CEconTradeOffer tradeOfferData;

    /**
     * Новое предложение обмена
     *
     * @param tradeUser Пользователь от имени которого осуществляется обмен
     * @param partnerID Идентификатор партнера по обмену
     * @throws IEconServiceException
     */
    protected TradeOffer(TradeUser tradeUser, SteamID partnerID) throws IEconServiceException {
        this(tradeUser, partnerID, null, "https://steamcommunity.com/tradeoffer/new/?partner=" + partnerID.getAccountId());

        this.tradeOfferData = new CEconTradeOffer();
        this.tradeOfferData.setAccountIDOther(partnerID.getCommunityId());
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
        this(tradeUser, new SteamID(tradeOfferData.getAccountIDOther()), tradeOfferData, "https://steamcommunity.com/tradeoffer/" + tradeOfferData.getTradeOfferID() + "/");
    }

    private TradeOffer(TradeUser tradeUser, SteamID partnerID, CEconTradeOffer tradeOfferData, String url) throws IEconServiceException {
        this.tradeOfferData = tradeOfferData;
        this.tradeUser = tradeUser;

        try {
            String html = tradeUser.doCommunityCall(url, HttpMethod.GET, null, false);
            Gson gson = new Gson();
            Pattern pattern = Pattern.compile("^\\s*var\\s+(g_.+?)\\s+=\\s+(.+?);\\r?$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(html);
            Map<String, String> javascriptGlobals = new HashMap<>();
            while (matcher.find()) {
                javascriptGlobals.put(matcher.group(1), matcher.group(2));
            }
            this.sessionId = gson.fromJson(javascriptGlobals.get("g_sessionID"), String.class);
            this.meInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strInventoryLoadURL"), String.class);
            this.themInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strTradePartnerInventoryLoadURL"), String.class);
        } catch (IOException e) {
            LOG.error("Unable to get data about trade offer", e);
            throw new IEconServiceException("Unable to get data about trade offer");
        }
    }

    /**
     * Получить содержимое инвентаря авторизованного пользователя Steam
     *
     * @param appID     Идентификатор приложения
     * @param contextID Тип предмета
     * @return Инвентарь
     * @throws Exception
     */
    public CEconInventory getMyInventory(EAppID appID, EContextID contextID) throws Exception {
        return new Gson()
                .fromJson(tradeUser.doCommunityCall(this.meInventoryLoadUrl + appID + "/" + contextID + "/?trading=1",
                        HttpMethod.GET, null, true), CEconInventory.class);
    }

    /**
     * Получить содержимое инвентаря другого пользователя
     *
     * @param appID     Идентификатор приложения
     * @param contextID Тип предмета
     * @return Инвентарь
     * @throws Exception
     */
    public CEconInventory getTheirInventory(EAppID appID, EContextID contextID) throws Exception {
        URI uri = new URIBuilder(this.themInventoryLoadUrl)
                .setParameter("sessionid", sessionId)
                .setParameter("partner", String.valueOf(this.tradeOfferData.getAccountIDOther()))
                .setParameter("appid", Long.toString(appID.getAppID()))
                .setParameter("contextid", Long.toString(contextID.getContextID()))
                .build();

        return new Gson().fromJson(tradeUser.doCommunityCall(uri.toString(), HttpMethod.GET, null, true), CEconInventory.class);
    }

    /**
     * Отправить новое предложение обмена или контрпредложение
     *
     * @throws Exception
     */
    public void send() throws Exception {
        CEconTradePartipiant me = new CEconTradePartipiant();
        me.setReady(true);
        me.setAssets(this.tradeOfferData.getItemsToGive());

        CEconTradePartipiant partner = new CEconTradePartipiant();
        partner.setReady(false);
        partner.setAssets(this.tradeOfferData.getItemsToReceive());

        CEconTradeStatus tradeStatus = new CEconTradeStatus();
        tradeStatus.setNewVersion(true);
        tradeStatus.setVersion(this.tradeOfferData.getTradeOfferID());
        tradeStatus.setMe(me);
        tradeStatus.setThem(partner);

        Gson gson = new Gson();
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(this.tradeOfferData.getAccountIDOther())));
        data.add(new BasicNameValuePair("tradeoffermessage", this.tradeOfferData.getMessage()));
        data.add(new BasicNameValuePair("json_tradeoffer", gson.toJson(tradeStatus)));
        if (this.tradeOfferData.getTradeOfferID() != 0) {
            data.add(new BasicNameValuePair("tradeofferid_countered", String.valueOf(this.tradeOfferData.getTradeOfferID())));
        }
        String result = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/new/send", HttpMethod.POST, data, true);
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
        data.add(new BasicNameValuePair("sessionid", this.sessionId));
        data.add(new BasicNameValuePair("tradeofferid", String.valueOf(this.tradeOfferData.getTradeOfferID())));

        String result = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/" + this.tradeOfferData.getTradeOfferID() + "/accept", HttpMethod.POST, data, true);
        // TODO: parse/return the result
    }

    /**
     * Отклонение предложения обмена
     * После вызова дальнейшее использование объекта невозможно
     *
     * @throws IEconServiceException
     */
    public void decline() throws IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(this.tradeOfferData.getTradeOfferID()));
        String result = tradeUser.doAPICall("DeclineTradeOffer/v1", HttpMethod.POST, params);
        //TODO Parse result
    }
}
