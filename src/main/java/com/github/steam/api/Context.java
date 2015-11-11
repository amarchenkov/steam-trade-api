package com.github.steam.api;

class Context {

    private int asset_count;
    private String id;
    private String name;

    public int getAsset_count() {
        return asset_count;
    }

    public void setAsset_count(int asset_count) {
        this.asset_count = asset_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return asset_count == context.asset_count && id.equals(context.id) && name.equals(context.name);
    }

    @Override
    public int hashCode() {
        int result = asset_count;
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Context{" +
                "asset_count=" + asset_count +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
