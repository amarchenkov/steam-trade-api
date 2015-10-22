package com.github.steam.api.common;

import java.util.Arrays;

public class CEconTradeOffer {

    private String tradeofferid;
    private String accountid_other;
    private String message;
    private long expiration_time;
    private ETradeOfferState trade_offer_state;
    private CEconAsset[] items_to_give;
    private CEconAsset[] items_to_receive;
    private boolean is_our_offer;
    private long time_created;
    private long time_updated;
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

    public String getTradeofferid() {
        return tradeofferid;
    }

    public void setTradeofferid(String tradeofferid) {
        this.tradeofferid = tradeofferid;
    }

    public String getAccountid_other() {
        return accountid_other;
    }

    public void setAccountid_other(String accountid_other) {
        this.accountid_other = accountid_other;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getExpiration_time() {
        return expiration_time;
    }

    public void setExpiration_time(long expiration_time) {
        this.expiration_time = expiration_time;
    }

    public ETradeOfferState getTrade_offer_state() {
        return trade_offer_state;
    }

    public void setTrade_offer_state(ETradeOfferState trade_offer_state) {
        this.trade_offer_state = trade_offer_state;
    }

    public CEconAsset[] getItems_to_give() {
        return items_to_give;
    }

    public void setItems_to_give(CEconAsset[] items_to_give) {
        this.items_to_give = items_to_give;
    }

    public CEconAsset[] getItems_to_receive() {
        return items_to_receive;
    }

    public void setItems_to_receive(CEconAsset[] items_to_receive) {
        this.items_to_receive = items_to_receive;
    }

    public boolean is_our_offer() {
        return is_our_offer;
    }

    public void setIs_our_offer(boolean is_our_offer) {
        this.is_our_offer = is_our_offer;
    }

    public long getTime_created() {
        return time_created;
    }

    public void setTime_created(long time_created) {
        this.time_created = time_created;
    }

    public long getTime_updated() {
        return time_updated;
    }

    public void setTime_updated(long time_updated) {
        this.time_updated = time_updated;
    }

    public boolean isFrom_real_time_trade() {
        return from_real_time_trade;
    }

    public void setFrom_real_time_trade(boolean from_real_time_trade) {
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
