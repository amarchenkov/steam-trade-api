package com.github.steam.api;

/**
 * Описание предмета отправленого для обмена (независимо от того, ксто создал предложение)
 *
 * @author Andrey Marchenkov
 */
class CEconAsset {

    private long appid;
    private long contextid;
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
    private float amount;

    /**
     * a boolean that indicates the item is no longer present in the user's inventory
     */
    private boolean missing;

    public long getAppID() {
        return appid;
    }

    public long getContextID() {
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

    public float getAmount() {
        return amount;
    }

    public boolean isMissing() {
        return missing;
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