package com.github.steam.api;

import com.github.steam.api.enumeration.ETradeOfferState;

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

    public CEconTradeOffer() {
    }

    public long getTradeOfferID() {
        return tradeofferid;
    }

    public void setTradeOfferID(long tradeofferid) {
        this.tradeofferid = tradeofferid;
    }

    public long getAccountIdOther() {
        return accountid_other;
    }

    public void setAccountIdOther(long accountid_other) {
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

    public void setItemsToGive(List<CEconAsset> items_to_give) {
        this.items_to_give = items_to_give;
    }

    public void setItemsToReceive(List<CEconAsset> items_to_receive) {
        this.items_to_receive = items_to_receive;
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
