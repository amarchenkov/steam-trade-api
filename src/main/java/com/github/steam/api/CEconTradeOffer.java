package com.github.steam.api;

import com.github.steam.api.enumeration.ETradeOfferState;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель предложения обмена
 *
 * @author Andrey Marchenkov
 */
public class CEconTradeOffer {

    /**
     * Уникальный идентификатор предложения обмена
     */
    @SerializedName("tradeofferid")
    private long tradeOfferID;

    /**
     * Идентификатор партнера по предложению обмена
     */
    @SerializedName("accountid_other")
    private long accountIdOther;

    /**
     * Сообщение создателя предложения обмена
     */
    @SerializedName("message")
    private String message;

    /**
     * Метка времени UNIX истечени срока действия
     */
    @SerializedName("expiration_time")
    private long expirationTime;

    /**
     * Состояние предложения обмена
     *
     * @see ETradeOfferState
     */
    @SerializedName("trade_offer_state")
    private ETradeOfferState tradeOfferState;

    /**
     * Массив объектов класса CEconAsset, предметы отправленные для обмена (независимо от того, кто создал предложение обмена)
     */
    @SerializedName("items_to_give")
    private List<CEconAsset> itemsToGive = new ArrayList<>();

    /**
     * Массив объектов класса CEconAsset, предметы полученные для обмена (независимо от того, кто создал предложение обмена)
     */
    @SerializedName("items_to_receive")
    private List<CEconAsset> itemsToReceive = new ArrayList<>();

    /**
     * Флаг, указывающий на принадлежность к текущему пользователю
     */
    @SerializedName("is_our_offer")
    private boolean isOurOffer;

    /**
     * Метка времени UNIX создания предложения обмена
     */
    @SerializedName("time_created")
    private long timeCreated;

    /**
     * Метка времени UNIX последнего изменения предложения обмена
     */
    @SerializedName("time_updated")
    private long timeUpdated;

    /**
     * Флаг указывающий, что предложение создано автоматически в реальном времени
     */
    @SerializedName("from_real_time_trade")
    private boolean fromRealTimeTrade;

    public CEconTradeOffer() {
    }

    public long getTradeOfferID() {
        return tradeOfferID;
    }

    public void setTradeOfferID(long tradeofferid) {
        this.tradeOfferID = tradeofferid;
    }

    public long getAccountIdOther() {
        return accountIdOther;
    }

    public void setAccountIdOther(long accountid_other) {
        this.accountIdOther = accountid_other;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expiration_time) {
        this.expirationTime = expiration_time;
    }

    public ETradeOfferState getTradeOfferState() {
        return tradeOfferState;
    }

    public void setTradeOfferState(ETradeOfferState trade_offer_state) {
        this.tradeOfferState = trade_offer_state;
    }

    public List<CEconAsset> getItemsToGive() {
        return itemsToGive;
    }

    public void addItemsToGive(CEconAsset itemToGive) {
        this.itemsToGive.add(itemToGive);
    }

    public List<CEconAsset> getItemsToReceive() {
        return itemsToReceive;
    }

    public void addItemsToReceive(CEconAsset itemToReceive) {
        this.itemsToReceive.add(itemToReceive);
    }

    public boolean isOurOffer() {
        return isOurOffer;
    }

    public void setIsOurOffer(boolean is_our_offer) {
        this.isOurOffer = is_our_offer;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long time_created) {
        this.timeCreated = time_created;
    }

    public long getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(long time_updated) {
        this.timeUpdated = time_updated;
    }

    public boolean isFromRealTimeTrade() {
        return fromRealTimeTrade;
    }

    public void setFromRealTimeTrade(boolean from_real_time_trade) {
        this.fromRealTimeTrade = from_real_time_trade;
    }

    public void setItemsToGive(List<CEconAsset> items_to_give) {
        this.itemsToGive = items_to_give;
    }

    public void setItemsToReceive(List<CEconAsset> items_to_receive) {
        this.itemsToReceive = items_to_receive;
    }

    @Override
    public String toString() {
        return "CEconTradeOffer{" +
                "tradeOfferID='" + tradeOfferID + '\'' +
                ", accountIdOther='" + accountIdOther + '\'' +
                ", message='" + message + '\'' +
                ", expirationTime=" + expirationTime +
                ", tradeOfferState=" + tradeOfferState +
                ", itemsToGive=" + itemsToGive +
                ", itemsToReceive=" + itemsToReceive +
                ", isOurOffer=" + isOurOffer +
                ", timeCreated=" + timeCreated +
                ", timeUpdated=" + timeUpdated +
                ", fromRealTimeTrade=" + fromRealTimeTrade +
                '}';
    }

}
