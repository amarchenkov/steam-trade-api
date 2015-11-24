package com.github.steam.api;

import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;

/**
 * Описание предмета отправленого для обмена (независимо от того, ксто создал предложение)
 *
 * @author Andrey Marchenkov
 */
public class CEconAsset {

    private EAppID appid;
    private EContextID contextid;
    private long assetid;
    private String currencyid;

    /**
     * together with instanceid, uniquely identifies the display of the item
     */
    private String classid;
    private String instanceid;

    /**
     * the amount offered in the trade, for stackable items and currency
     */
    private int amount;

    /**
     * a boolean that indicates the item is no longer present in the user's inventory
     */
    private boolean missing;

    public EAppID getAppID() {
        return appid;
    }

    public EContextID getContextID() {
        return contextid;
    }

    public long getAssetID() {
        return assetid;
    }

    public String getCurrencyID() {
        return currencyid;
    }

    public String getClassID() {
        return classid;
    }

    public String getInstanceID() {
        return instanceid;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setAppID(EAppID appid) {
        this.appid = appid;
    }

    public void setContextID(EContextID contextid) {
        this.contextid = contextid;
    }

    public void setAssetID(long assetid) {
        this.assetid = assetid;
    }

    public void setCurrencyID(String currencyid) {
        this.currencyid = currencyid;
    }

    public void setClassID(String classid) {
        this.classid = classid;
    }

    public void setInstanceID(String instanceid) {
        this.instanceid = instanceid;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    /**
     * Получить цену предмета в steam
     *
     * @return Цена
     */
    public float getAssetPrice() {
        return 0;
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