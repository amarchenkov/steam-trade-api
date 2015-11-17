package com.github.steam.api;

import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.IEconServiceException;
import com.google.gson.Gson;
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

/**
 * Структура, возвращаемая методами GetTradeOffers и GetTradeOffer
 *
 * @author Andrey Marchenkov
 */
class CEconTradeOffer_old {

    /*===================== INSTANCE PROPERTIES ===============*/
    private transient boolean newOrder;
    private transient TradeUser tradeUser;
    private transient String themInventoryLoadUrl;
    private transient String meInventoryLoadUrl;
    private transient String sessionId;

    /*===================== JSON PROPERTIES ===================*/
    /**
     * Уникальный идентификатор предложения обмена
     */
    private long tradeofferid;

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
     *
     * @see ETradeOfferState
     */
    private ETradeOfferState trade_offer_state;

    /**
     * Массив объектов класса CEconAsset, предметы отправленные для обмена (независимо от того, кто создал предложение обмена)
     */
    private List<CEconAsset> items_to_give = new ArrayList<>();

    /**
     * Массив объектов класса CEconAsset, предметы полученные для обмена (независимо от того, кто создал предложение обмена)
     */
    private List<CEconAsset> items_to_receive = new ArrayList<>();

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
     * Флаг указывающий, что предложение создано автоматически в реальном времени
     */
    private boolean from_real_time_trade;

    /*================= INSTANCE METHODS ===================*/
    protected CEconTradeOffer_old() throws IOException {
        this.newOrder = false;
    }

    protected CEconTradeOffer_old(TradeUser tradeUser, SteamID partnerID) throws Exception {
        this.newOrder = true;
        this.setAccountIDOther(partnerID.getCommunityId());
        this.setTradeUser(tradeUser);

        String html = tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/new/?partner=" + partnerID.getAccountId(), HttpMethod.GET, null, false);
        this.setIsOurOffer(true);
        this.setTimeCreated(System.currentTimeMillis());
        this.setTimeUpdated(System.currentTimeMillis());
        this.setTradeOfferState(ETradeOfferState.k_ETradeOfferStateActive);

        initializeWeb(html);
    }

    /**
     * Инициализация Web части
     *
     * @param html Код страницы
     */
    private void initializeWeb(String html) {
        Gson gson = new Gson();
        Pattern pattern = Pattern.compile("^\\s*var\\s+(g_.+?)\\s+=\\s+(.+?);\\r?$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        Map<String, String> javascriptGlobals = new HashMap<>();
        while (matcher.find()) {
            javascriptGlobals.put(matcher.group(1), matcher.group(2));
        }

        this.sessionId = gson.fromJson(javascriptGlobals.get("g_sessionID"), String.class);
        this.setMessage("");

        this.meInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strInventoryLoadURL"), String.class);
        this.themInventoryLoadUrl = gson.fromJson(javascriptGlobals.get("g_strTradePartnerInventoryLoadURL"), String.class);
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
        this.check();
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
        this.check();
        URI uri = new URIBuilder(this.themInventoryLoadUrl)
                .setParameter("sessionid", sessionId)
                .setParameter("partner", String.valueOf(this.getAccountIDOther()))
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
        this.check();
        CEconTradePartipiant me = new CEconTradePartipiant();
        me.setReady(true);
        me.setAssets(this.getItemsToGive());

        CEconTradePartipiant partner = new CEconTradePartipiant();
        partner.setReady(false);
        partner.setAssets(this.getItemsToReceive());

        CEconTradeStatus tradeStatus = new CEconTradeStatus();
        tradeStatus.setNewVersion(true);
        tradeStatus.setVersion(this.getTradeOfferID());
        tradeStatus.setMe(me);
        tradeStatus.setThem(partner);

        Gson gson = new Gson();
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionid", sessionId));
        data.add(new BasicNameValuePair("partner", Long.toString(this.getAccountIDOther())));
        data.add(new BasicNameValuePair("tradeoffermessage", this.getMessage()));
        data.add(new BasicNameValuePair("json_tradeoffer", gson.toJson(tradeStatus)));
        if (getTradeOfferID() != 0) {
            data.add(new BasicNameValuePair("tradeofferid_countered", String.valueOf(this.getTradeOfferID())));
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
        this.check();
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionid", this.sessionId));
        data.add(new BasicNameValuePair("tradeofferid", String.valueOf(this.getTradeOfferID())));

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
        //TODO Parse result
    }

    /**
     * Проверка перед выполнением методов связанных с Web
     *
     * @throws IOException
     * @throws IEconServiceException
     */
    private void check() throws IOException, IEconServiceException {
        if (this.tradeUser == null) {
            throw new IEconServiceException("Steam user for trading is not found");
        }
        if (!this.newOrder) {
            String html = this.tradeUser.doCommunityCall("https://steamcommunity.com/tradeoffer/" + this.getTradeOfferID() + "/", HttpMethod.GET, null, false);
            this.initializeWeb(html);
        }
    }

    /*================== GETTERS AND SETTERS ===================*/
    public long getTradeOfferID() {
        return tradeofferid;
    }

    public void setTradeOfferID(long tradeofferid) {
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

    public List<CEconAsset> getItemsToGive() {
        return items_to_give;
    }

    public void addItemsToGive(CEconAsset itemToGive) {
        this.items_to_give.add(itemToGive);
    }

    public List<CEconAsset> getItemsToReceive() {
        return items_to_receive;
    }

    public void addItemsToReceive(CEconAsset itemToReceive) {
        this.items_to_receive.add(itemToReceive);
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

    public void setTradeUser(TradeUser tradeUser) {
        this.tradeUser = tradeUser;
    }

    @Override
    public String toString() {
        return "CEconTradeOffer{" +
                "tradeofferid='" + tradeofferid + '\'' +
                ", accountid_other='" + accountid_other + '\'' +
                ", message='" + message + '\'' +
                ", expiration_time=" + expiration_time +
                ", trade_offer_state=" + trade_offer_state +
                ", items_to_give=" + items_to_give +
                ", items_to_receive=" + items_to_receive +
                ", is_our_offer=" + is_our_offer +
                ", time_created=" + time_created +
                ", time_updated=" + time_updated +
                ", from_real_time_trade=" + from_real_time_trade +
                '}';
    }
}
