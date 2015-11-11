package com.github.steam.api;

/**
 * Описание предмета отправленого для обмена (независимо от того, ксто создал предложение)
 *
 * @author Andrey Marchenkov
 */
public class CEconAsset {

    private long appid;
    private long contextid;

    /**
     * either assetid or currencyid will be set
     */
    private long assetid;

    /**
     * either assetid or currencyid will be set
     */
    private String currencyid;

    /**
     * together with instanceid, uniquely identifies the display of the item
     */
    private String classid;

    /**
     * together with classid, uniquely identifies the display of the item
     */
    private String instanceid;

    /**
     * the amount offered in the trade, for stackable items and currency
     */
    private float amount;

    /**
     * a boolean that indicates the item is no longer present in the user's inventory
     */
    private boolean missing;

    public long getAppID() {
        return appid;
    }

    public void setAppID(long appid) {
        this.appid = appid;
    }

    public long getContextID() {
        return contextid;
    }

    public void setContextID(long contextid) {
        this.contextid = contextid;
    }

    public long getAssetID() {
        return assetid;
    }

    public void setAssetID(long assetid) {
        this.assetid = assetid;
    }

    public String getCurrencyID() {
        return currencyid;
    }

    public void setCurrencyID(String currencyid) {
        this.currencyid = currencyid;
    }

    public String getClassID() {
        return classid;
    }

    public void setClassID(String classid) {
        this.classid = classid;
    }

    public String getInstanceID() {
        return instanceid;
    }

    public void setInstanceID(String instanceid) {
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
