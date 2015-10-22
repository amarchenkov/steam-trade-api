package com.github.steam.api.model;

import com.github.steam.api.HttpHelper;
import com.github.steam.api.common.CEconTradeOffer;
import com.github.steam.api.exception.SteamApiException;
import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Unofficial SDK for Steam Trade API.
 * @author Andrey Marchenkov
 */
public class SteamTrade {

    private static final String communityUrl = "https://steamcommunity.com";
    private static final String apiUrl = "https://api.steampowered.com/IEconService/";
    private static final HttpHelper helper = new HttpHelper();

    private String webApiKey;

    public SteamTrade(String webApiKey) {
        this.webApiKey = webApiKey;
    }

    public List<CEconTradeOffer> getTradeOffers(boolean getSentOffers, boolean getReceivedOffers) throws SteamApiException {
        String result = doAPICall("GetTradeOffers/v1", new HashMap<>(), true);
        return null;
    }

    public List<CEconTradeOffer> getTradeOffers(boolean getSentOffers, boolean getReceivedOffers, boolean activeOnly) {
        return null;
    }

    /**
     * Получить детальную информацию о выбранном предложении обмена
     * @param tradeOfferID Идентификатор предложения обмена
     * @param language Язык
     * @return CEconTradeOffer
     */
    public CEconTradeOffer getTradeOffer(String tradeOfferID, String language) throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", tradeOfferID);
        params.put("language", language);
        String result = doAPICall("GetTradeOffer/v1", params, false);
        Gson gson = new Gson();
        return gson.fromJson(result, CEconTradeOffer.class);
    }

    /**
     * Отклонить входящее предложение обмена
     * @param tradeOfferID Идентификатор предложения обмена
     * @throws SteamApiException
     */
    public void declineTradeOffer(String tradeOfferID) throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", tradeOfferID);
        String result = doAPICall("DeclineTradeOffer/v1", params, true);
    }

    /**
     * Отменить исходящее предложение обмена
     * @param tradeOfferID Идентификатор предложения обмена
     * @throws SteamApiException
     */
    public void cancelTradeOffer(String tradeOfferID) throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", tradeOfferID);
        String result = doAPICall("CancelTradeOffer/v1", params, true);
    }

    public void acceptOffer() {

    }

    public void makeOffer() {

    }

    public void getItems() {

    }

    public void loadPartnerInventory() {

    }

    public void loadMyInventory() {

    }

    public void getOfferToken() {

    }

    /**
     * Выполнить HTTP-запрос к официальному SteamWebAPI
     * @param method Вызываемый в API метод
     * @param params Параметры запроса в виде карты "параметр" => "значение"
     * @param isHttpPost POST или GET запрос
     */
    private String doAPICall(String method, Map<String, String> params, boolean isHttpPost) throws SteamApiException {
        if (isHttpPost) {
            try {
                URIBuilder builder = new URIBuilder(apiUrl + method);
                builder.setParameter("key", this.webApiKey);
                builder.setParameter("format", "json");
                try {
                    return helper.sendPost(builder.build(), params);
                } catch (IOException e) {
                    throw new SteamApiException("Requset exception", e);
                }
            } catch (URISyntaxException e) {
                throw new SteamApiException("Invalid method", e);
            }
        } else {
            try {
                URIBuilder builder = new URIBuilder(apiUrl + method);
                builder.setParameter("key", this.webApiKey);
                builder.setParameter("format", "json");
                for(Map.Entry<String, String> param : params.entrySet()) {
                    builder.setParameter(param.getKey(), param.getValue());
                }
                try {
                    return helper.sendGet(builder.build());
                } catch (IOException e) {
                    throw new SteamApiException("Requset exception", e);
                }
            } catch (URISyntaxException e) {
                throw new SteamApiException("Invalid method", e);
            }
        }
    }

    private void doComunityCall() {

    }

}