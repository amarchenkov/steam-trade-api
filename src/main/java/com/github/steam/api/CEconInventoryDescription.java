package com.github.steam.api;

import java.util.ArrayList;
import java.util.HashMap;

public class CEconInventoryDescription {

    private String appid;
    private String classid;
    private String instanceid;
    private String name;
    private String type;
    private int tradable;
    private ArrayList<HashMap<String, String>> tags;

    public String getAppid() {
        return appid;
    }

    public String getClassid() {
        return classid;
    }

    public String getInstanceid() {
        return instanceid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int isTradable() {
        return tradable;
    }

    public ArrayList<HashMap<String, String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "CEconInventoryDescription{" +
                "appid='" + appid + '\'' +
                ", classid='" + classid + '\'' +
                ", instanceid='" + instanceid + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", tradable=" + tradable +
                ", tags=" + tags +
                '}';
    }
}
