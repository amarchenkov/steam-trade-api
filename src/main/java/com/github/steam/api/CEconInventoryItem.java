package com.github.steam.api;

public class CEconInventoryItem {

    private String id;
    private String classid;
    private String instanceid;
    private String amount;
    private String pos;
    private CEconInventory inventory;

    public String getId() {
        return id;
    }

    public String getClassid() {
        return classid;
    }

    public String getInstanceid() {
        return instanceid;
    }

    public String getAmount() {
        return amount;
    }

    public String getPos() {
        return pos;
    }

    public void setInventory(CEconInventory inventory) {
        this.inventory = inventory;
    }

    public CEconInventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return "CEconInventoryItem{" +
                "id='" + id + '\'' +
                ", classid='" + classid + '\'' +
                ", instanceid='" + instanceid + '\'' +
                ", amount='" + amount + '\'' +
                ", pos='" + pos + '\'' +
                '}';
    }

}
