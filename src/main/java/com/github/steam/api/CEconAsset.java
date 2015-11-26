package com.github.steam.api;

import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.IEconServiceException;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Описание предмета отправленого для обмена (независимо от того, ксто создал предложение)
 *
 * @author Andrey Marchenkov
 */
public class CEconAsset {

    @SerializedName("appid")
    private EAppID appID;

    @SerializedName("contextid")
    private EContextID contextID;

    @SerializedName("assetid")
    private long assetID;

    @SerializedName("currencyid")
    private String currencyID;

    /**
     * together with instanceid, uniquely identifies the display of the item
     */
    @SerializedName("classid")
    private String classID;

    @SerializedName("instanceid")
    private String instanceID;

    /**
     * the amount offered in the trade, for stackable items and currency
     */
    @SerializedName("amount")
    private int amount;

    /**
     * a boolean that indicates the item is no longer present in the user's inventory
     */
    @SerializedName("missing")
    private boolean missing;

    @SerializedName("market_hash_name")
    private String marketHashName;

    public EAppID getAppID() {
        return appID;
    }

    public EContextID getContextID() {
        return contextID;
    }

    public long getAssetID() {
        return assetID;
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public String getClassID() {
        return classID;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setAppID(EAppID appid) {
        this.appID = appid;
    }

    public void setContextID(EContextID contextid) {
        this.contextID = contextid;
    }

    public void setAssetID(long assetid) {
        this.assetID = assetid;
    }

    public void setCurrencyID(String currencyid) {
        this.currencyID = currencyid;
    }

    public void setClassID(String classid) {
        this.classID = classid;
    }

    public void setInstanceID(String instanceid) {
        this.instanceID = instanceid;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public String getMarketHashName() {
        return marketHashName;
    }

    public void setMarketHashName(String marketHashName) {
        this.marketHashName = marketHashName;
    }

    /**
     * Получить цену предмета в steam
     *
     * @return Цена
     */
    public float getAssetPrice() {
        return this.getAssetPrice(5);
    }

    /**
     * Получить цену предмета в steam в заданной валюте
     *
     * @param currencyID Идентификатор валюты
     * @return Цена
     */
    public float getAssetPrice(int currencyID) {
        try {
            TradeUser user = new TradeUser();
            URIBuilder builder = new URIBuilder("http://steamcommunity.com/market/priceoverview/");
            builder.addParameter("country", "RU")
                    .addParameter("currency", String.valueOf(currencyID))
                    .addParameter("market_hash_name", this.marketHashName)
                    .addParameter("appid", String.valueOf(this.appID.getAppID()));
            String response = user.doCommunityCall(builder.build().toString(), HttpMethod.GET, null, false);
            PriceOverviewResponse jsonResponse = new Gson().fromJson(response, PriceOverviewResponse.class);
            Pattern pattern = Pattern.compile("[-+]?(\\d*[,]?\\d+\\d)\\s+\\S+");
            Matcher matcher = pattern.matcher(jsonResponse.medianPrice);

            if (matcher.find()) {
                DecimalFormatSymbols sym = new DecimalFormatSymbols();
                sym.setDecimalSeparator(',');
                DecimalFormat format = new DecimalFormat();
                format.setDecimalFormatSymbols(sym);
                Number value = format.parse(matcher.group(1));
                return value.floatValue();
            } else {
                return 0.0F;
            }
        } catch (IEconServiceException | URISyntaxException | ParseException e) {
            return 0.0F;
        }
    }

    private class PriceOverviewResponse {

        @SerializedName("success")
        private boolean success;

        @SerializedName("lowest_price")
        private String lowestPrice;

        @SerializedName("volume")
        private int volume;

        @SerializedName("median_price")
        private String medianPrice;

    }

    @Override
    public String toString() {
        return "CEconAsset{" +
                "appID='" + appID + '\'' +
                ", contextID='" + contextID + '\'' +
                ", assetID='" + assetID + '\'' +
                ", currencyID='" + currencyID + '\'' +
                ", classID='" + classID + '\'' +
                ", instanceID='" + instanceID + '\'' +
                ", amount=" + amount +
                ", missing=" + missing +
                '}';
    }
}