package com.github.steam.api;

class RsaKeyResponse {

    private boolean success;
    private String publickey_mod;
    private String publickey_exp;
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPublickeyMod() {
        return publickey_mod;
    }

    public void setPublickeyMod(String publickey_mod) {
        this.publickey_mod = publickey_mod;
    }

    public String getPublickeyExp() {
        return publickey_exp;
    }

    public void setPublickeyExp(String publickey_exp) {
        this.publickey_exp = publickey_exp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
