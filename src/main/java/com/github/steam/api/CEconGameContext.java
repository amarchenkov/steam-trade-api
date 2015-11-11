package com.github.steam.api;

import java.util.Map;

class CEconGameContext {
    private int appid;
    private String name;
    private String icon;
    private int asset_count;
    private String inventory_logo;
    private String trade_permissions;
    private Map<String, CEconContext> rgContexts;

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getAsset_count() {
        return asset_count;
    }

    public void setAsset_count(int asset_count) {
        this.asset_count = asset_count;
    }

    public String getInventory_logo() {
        return inventory_logo;
    }

    public void setInventory_logo(String inventory_logo) {
        this.inventory_logo = inventory_logo;
    }

    public String getTrade_permissions() {
        return trade_permissions;
    }

    public void setTrade_permissions(String trade_permissions) {
        this.trade_permissions = trade_permissions;
    }

    public Map<String, CEconContext> getRgContexts() {
        return rgContexts;
    }

    public void setRgContexts(Map<String, CEconContext> rgContexts) {
        this.rgContexts = rgContexts;
    }

    @Override
    public String toString() {
        return "GameContext{" +
                "appid=" + appid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", asset_count=" + asset_count +
                ", inventory_logo='" + inventory_logo + '\'' +
                ", trade_permissions='" + trade_permissions + '\'' +
                ", rgContexts=" + rgContexts +
                '}';
    }
}
