package com.github.steam.api.common;

import java.util.Arrays;

/**
 * Both GetTradeOffers and GetTradeOffer return this structure
 * @author Andrey Marchenkov
 */
public class CEconTradeOffer {

    /** A unique identifier for the trade offer */
    private String tradeofferid;

    /** Your partner in the trade offer */
    private String accountid_other;

    /** A message included by the creator of the trade offer */
    private String message;

    /** UNIX time when the offer will expire (or expired, if it is in the past) */
    private long expiration_time;

    /** @see ETradeOfferState above */
    private ETradeOfferState trade_offer_state;

    /** Array of CEcon_Asset, items you will give up in the trade (regardless of who created the offer) */
    private CEconAsset[] items_to_give;

    /** Array of CEcon_Asset, items you will receive in the trade (regardless of who created the offer)*/
    private CEconAsset[] items_to_receive;

    /** Boolean to indicate this is an offer you created */
    private boolean is_our_offer;

    /** UNIX timestamp of the time the offer was sent */
    private long time_created;

    /** UNIX timestamp of the time the trade_offer_state last changed */
    private long time_updated;

    /** Boolean to indicate this is an offer automatically created from a real-time trade */
    private boolean from_real_time_trade;

    public CEconTradeOffer(String tradeofferid, String accountid_other, String message, long expiration_time, ETradeOfferState trade_offer_state, CEconAsset[] items_to_give, CEconAsset[] items_to_receive, boolean is_our_offer, long time_created, long time_updated, boolean from_real_time_trade) {
        this.tradeofferid = tradeofferid;
        this.accountid_other = accountid_other;
        this.message = message;
        this.expiration_time = expiration_time;
        this.trade_offer_state = trade_offer_state;
        this.items_to_give = items_to_give;
        this.items_to_receive = items_to_receive;
        this.is_our_offer = is_our_offer;
        this.time_created = time_created;
        this.time_updated = time_updated;
        this.from_real_time_trade = from_real_time_trade;
    }

    public CEconTradeOffer() {
    }

    public String getTradeOfferID() {
        return tradeofferid;
    }

    public void setTradeOfferID(String tradeofferid) {
        this.tradeofferid = tradeofferid;
    }

    public String getAccountIDOther() {
        return accountid_other;
    }

    public void setAccountIDOther(String accountid_other) {
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
