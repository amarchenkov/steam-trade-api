package com.github.steam.api.enumeration;

/**
 * Перечисление контекстов предметов для обмена
 *
 * @author Andrey Marchenkov
 */
public enum EContextID {

    GIFT(1, "Gift"),
    BACKPACK(2, "Backpack"),
    COUPON(3, "Coupon"),
    COMMUNITY(6, "Community"),
    ITEM_REWARD(7, "Item reward"),;

    private int contextID;
    private String contextName;

    EContextID(int contextID, String contextName) {
        this.contextID = contextID;
        this.contextName = contextName;
    }

    public int getContextID() {
        return this.contextID;
    }

    public String getContextName() {
        return this.contextName;
    }

    public static EContextID valueOf(int contextID) {
        for (EContextID context : EContextID.values()) {
            if (context.getContextID() == contextID) {
                return context;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(this.contextID);
    }
}
