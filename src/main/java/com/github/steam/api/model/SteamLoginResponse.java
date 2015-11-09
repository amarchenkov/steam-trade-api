package com.github.steam.api.model;

import java.util.HashMap;

class SteamLoginResponse {

    private boolean success;
    private String message;
    private boolean captcha_needed;
    private String captcha_gid;
    private boolean emailauth_needed;
    private String emailsteamid;
    private HashMap<String, String> transfer_parameters;
    private String transfer_url;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCaptchaNeeded() {
        return captcha_needed;
    }

    public void setCaptchaNeeded(boolean captcha_needed) {
        this.captcha_needed = captcha_needed;
    }

    public String getCaptchaGid() {
        return captcha_gid;
    }

    public void setCaptchaGid(String captcha_gid) {
        this.captcha_gid = captcha_gid;
    }

    public boolean isEmailauthNeeded() {
        return emailauth_needed;
    }

    public void setEmailauthNeeded(boolean emailauth_needed) {
        this.emailauth_needed = emailauth_needed;
    }

    public String getEmailsteamID() {
        return emailsteamid;
    }

    public void setEmailsteamID(String emailsteamid) {
        this.emailsteamid = emailsteamid;
    }

    public HashMap<String, String> getTransferParameters() {
        return transfer_parameters;
    }

    public void setTransferParameters(HashMap<String, String> transfer_parameters) {
        this.transfer_parameters = transfer_parameters;
    }

    public String getTransferUrl() {
        return transfer_url;
    }

    public void setTransferUrl(String transfer_url) {
        this.transfer_url = transfer_url;
    }
}
