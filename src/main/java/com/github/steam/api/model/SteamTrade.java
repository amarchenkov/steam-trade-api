package com.github.steam.api.model;

import com.github.steam.api.common.ETradeOfferState;
import com.github.steam.api.http.HttpHelper;
import com.github.steam.api.common.CEconTradeOffer;
import com.github.steam.api.exception.SteamApiException;
import com.github.steam.api.http.HttpMethod;
import com.github.steam.api.model.adapter.ETradeOfferStateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unofficial SDK for Steam Trade API.
 * @author Andrey Marchenkov
 */
public class SteamTrade {

    protected static final String communityUrl = "https://steamcommunity.com";
    protected static final String apiUrl = "https://api.steampowered.com/IEconService/";
    protected static final HttpHelper helper = new HttpHelper();
    protected Gson gson;

    private String webApiKey;

    public SteamTrade(String webApiKey) {
        this.webApiKey = webApiKey;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ETradeOfferState.class, new ETradeOfferStateAdapter())
                .create();
    }

    public List<CEconTradeOffer> getTradeOffers(boolean getSentOffers, boolean getReceivedOffers) throws SteamApiException {
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
        String result = doAPICall("GetTradeOffer/v1", params, HttpMethod.GET);
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
        String result = doAPICall("DeclineTradeOffer/v1", params, HttpMethod.POST);
    }

    /**
     * Отменить исходящее предложение обмена
     * @param tradeOfferID Идентификатор предложения обмена
     * @throws SteamApiException
     */
    public void cancelTradeOffer(String tradeOfferID) throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", tradeOfferID);
        String result = doAPICall("CancelTradeOffer/v1", params, HttpMethod.POST);
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
     * @param httpMethod POST или GET запрос
     */
    private String doAPICall(String method, Map<String, String> params, HttpMethod httpMethod) throws SteamApiException {
        try {
            URIBuilder builder = new URIBuilder(apiUrl + method);
            builder.setParameter("key", this.webApiKey);
            builder.setParameter("format", "json");
            switch (httpMethod) {
                case GET:
                    for(Map.Entry<String, String> param : params.entrySet()) {
                        builder.setParameter(param.getKey(), param.getValue());
                    }
                    return helper.sendGet(builder.build());
                case POST:
                    return helper.sendPost(builder.build(), params);
                default:
                    throw new IllegalStateException("Undefined http method");
            }
        } catch (URISyntaxException e) {
            throw new SteamApiException("Invalid API URI", e);
        } catch (IOException e) {
            throw new SteamApiException("HTTP Requset exception", e);
        }
    }

    private void doComunityCall() {

    }

}