package com.github.steam.api.common;

/**
 * Description of item you will give up in the trade (regardless of who created the offer)
 * @author Andrey Marchenkov
 */
public class CEconAsset {

    private String appid;
    private String contextid;

    /** either assetid or currencyid will be set */
    private String assetid;

    /** either assetid or currencyid will be set */
    private String  currencyid;

    /** together with instanceid, uniquely identifies the display of the item */
    private String classid;

    /** together with classid, uniquely identifies the display of the item */
    private String instanceid;

    /** the amount offered in the trade, for stackable items and currency */
    private float amount;

    /** a boolean that indicates the item is no longer present in the user's inventory */
    private boolean missing;

    public CEconAsset() {
    }

    public CEconAsset(String appid, String contextid, String assetid, String currencyid, String classid, String instanceid, float amount, boolean missing) {
        this.appid = appid;
        this.contextid = contextid;
        this.assetid = assetid;
        this.currencyid = currencyid;
        this.classid = classid;
        this.instanceid = instanceid;
        this.amount = amount;
        this.missing = missing;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getContextid() {
        return contextid;
    }

    public void setContextid(String contextid) {
        this.contextid = contextid;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getCurrencyid() {
        return currencyid;
    }

    public void setCurrencyid(String currencyid) {
        this.currencyid = currencyid;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    @Override
    public String toString() {
        return "CEconAsset{" +
                "appid='" + appid + '\'' +
                ", contextid='" + contextid + '\'' +
                ", assetid='" + assetid + '\'' +
                ", currencyid='" + currencyid + '\'' +
                ", classid='" + classid + '\'' +
                ", instanceid='" + instanceid + '\'' +
                ", amount=" + amount +
                ", missing=" + missing +
                '}';
    }
}
