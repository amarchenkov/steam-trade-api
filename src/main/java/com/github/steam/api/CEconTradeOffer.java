package com.github.steam.api;

import com.github.steam.api.enumeration.ETradeOfferState;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.IEconServiceException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Структура, возвращаемая методами GetTradeOffers и GetTradeOffer
 *
 * @author Andrey Marchenkov
 */
//TODO Навести порядок с сущностями
class CEconTradeOffer {

    private transient String themInventoryLoadUrl;
    private transient String meInventoryLoadUrl;
    private transient String sessionId;
    private transient CEconTradeStatus tradeStatus;

    /**
     * Уникальный идентификатор предложения обмена
     */
    private String tradeofferid;

    /**
     * Идентификатор партнера по предложению обмена
     */
    private long accountid_other;

    /**
     * Сообщение создателя предложения обмена
     */
    private String message;

    /**
     * Метка времени UNIX истечени срока действия
     */
    private long expiration_time;

    /**
     * Состояние предложения обмена
     * @see ETradeOfferState
     */
    private ETradeOfferState trade_offer_state;

    /**
     * Массив объектов класса CEconAsset, предметы отправленные для обмена (независимо от того, кто создал предложение обмена)
     */
    private CEconAsset[] items_to_give;

    /**
     * Массив объектов класса CEconAsset, предметы полученные для обмена (независимо от того, кто создал предложение обмена)
     */
    private CEconAsset[] items_to_receive;

    /**
     * Флаг, указывающий на принадлежность к текущему пользователю
     */
    private boolean is_our_offer;

    /**
     * Метка времени UNIX создания предложения обмена
     */
    private long time_created;

    /**
     * Метка времени UNIX последнего изменения предложения обмена
     */
    private long time_updated;

    /**
     * Boolean to indicate this is an offer automatically created from a real-time trade
     */
    private boolean from_real_time_trade;

    /**
     * Steam-пользователь
     */
    private transient TradeUser tradeUser;

    protected CEconTradeOffer() {
    }

    protected CEconTradeOffer(TradeUser tradeUser, int tradeOfferID, SteamID accountIDOther) throws Exception {
        this.tradeofferid = String.valueOf(tradeOfferID);
        this.accountid_other = accountIDOther.getCommunityId();
        this.tradeUser = tradeUser;

        Gson gson = new GsonBuilder().create();
        String html;
        if (tradeOfferID == 0) {
            html = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/new/?partner=" + accountIDOther.getAccountId(), HttpMethod.GET, null, false);
            this.setIsOurOffer(true);
            this.setTimeCreated(System.currentTimeMillis());
            this.setTimeUpdated(System.currentTimeMillis());
            this.setTradeOfferState(ETradeOfferState.k_ETradeOfferStateActive);
        } else {
            html = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/" + tradeOfferID + "/", HttpMethod.GET, null, false);
        }

        Pattern pattern = Pattern.compile("^\\s*var\\s+(g_.+?)\\s+=\\s+(.+?);\\r?$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        Map<String, String> javascriptGlobals = new HashMap<>();
        while (matcher.find()) {
            javascriptGlobals.put(matcher.group(1), matcher.group(2));
        }

        this.tradeStatus = gson.fromJson(javascriptGlobals.get("g_rgCurrentTradeStatus"), CEconTradeStatus.class);
        this.tradeStatus.setTradeOffer(this);
        this.sessionId = gson.fromJson(javascriptGlobals.get("g_sessionID"), String.class);
        this.setMessage("");

        this.meInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strInventoryLoadURL"), String.class);
        this.themInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strTradePartnerInventoryLoadURL"), String.class);

        Type type = new TypeToken<HashMap<String, CEconGameContext>>(){}.getType();
        this.tradeStatus.getMe().setPartner(false);
        this.tradeStatus.getMe().setGameContext(gson.<Map<String, CEconGameContext>>fromJson(javascriptGlobals.get("g_rgAppContextData"), type));
        this.tradeStatus.getMe().setTradeStatus(this.getTradeStatus());
        this.tradeStatus.getThem().setPartner(true);
        this.tradeStatus.getThem().setGameContext(gson.<Map<String, CEconGameContext>>fromJson(javascriptGlobals.get("g_rgPartnerAppContextData"), type));
        this.tradeStatus.getThem().setTradeStatus(this.getTradeStatus());
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

    protected CEconInventory fetchMyInventory(long appId, long contextId) throws Exception {
        return new Gson()
                .fromJson(tradeUser.doCommunityCall(this.meInventoryLoadUrl + appId + "/" + contextId + "/?trading=1",
                        HttpMethod.GET, null, true), CEconInventory.class);
    }

    protected CEconInventory fetchTheirInventory(long appId, long contextId) throws Exception {
        URI uri = new URIBuilder(this.themInventoryLoadUrl)
                .setParameter("sessionid", sessionId)
                .setParameter("partner", String.valueOf(this.getAccountIDOther()))
                .setParameter("appid", Long.toString(appId))
                .setParameter("contextid", Long.toString(contextId))
                .build();

        return new Gson().fromJson(tradeUser.doCommunityCall(uri.toString(), HttpMethod.GET, null, true), CEconInventory.class);
    }

    /**
     * Sends a new trade offer OR counter offer, depending on if this is a new trade offer
     *
     * @throws Exception
     */
    public void update() throws Exception {
        tradeStatus.setVersion(tradeStatus.getVersion() + 1);
        tradeStatus.setNewVersion(true);
        Gson gson = new Gson();
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(this.getAccountIDOther())));
        data.add(new BasicNameValuePair("tradeoffermessage", this.getMessage()));
        data.add(new BasicNameValuePair("json_tradeoffer", gson.toJson(tradeStatus)));
        if (!Objects.equals(getTradeOfferID(), "0")) {
            data.add(new BasicNameValuePair("tradeofferid_countered", getTradeOfferID()));
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
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("tradeofferid", this.getTradeOfferID()));

        String result = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/" + this.getTradeOfferID() + "/accept", HttpMethod.POST, data, true);
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
        params.put("tradeofferid", String.valueOf(getTradeOfferID()));
        String result = tradeUser.doAPICall("DeclineTradeOffer/v1", HttpMethod.POST, params);
    }

    public CEconTradeStatus getTradeStatus() {
        return tradeStatus;
    }

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
