package com.github.steam.api.model;

import com.github.steam.api.common.*;
import com.github.steam.api.exception.SteamApiException;
import com.github.steam.api.exception.SteamCommunityException;
import com.github.steam.api.http.HttpMethod;
import com.github.steam.api.model.adapter.ETradeOfferStateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.List;

/**
 * Unofficial SDK for Steam Trade API.
 *
 * @author Andrey Marchenkov
 */
public class SteamUser {

    private static final Logger LOG = LogManager.getLogger(SteamUser.class);

    /**
     * URL for official API
     */
    protected static final String apiUrl = "https://api.steampowered.com/IEconService/";

    private CookieStore cookieStore = new BasicCookieStore();
    private CloseableHttpClient httpClient;
    private Gson gson;
    private String webApiKey;
    private SteamUserState state;

    public SteamUser(String webApiKey, String username, String password) {
        this.webApiKey = webApiKey;
        this.state = SteamUserState.NOT_LOGGED_IN;
        this.login(username, password);
        this.gson = new GsonBuilder().registerTypeAdapter(ETradeOfferState.class, new ETradeOfferStateAdapter()).create();
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    /**
     * Получить всю информацию о предложениях обмена
     *
     * @param params Список параметров
     * @return Список CEconTradeOffer
     * @throws SteamApiException
     */
    public List<CEconTradeOffer> getTradeOffers(List<Map<String, String>> params) throws SteamApiException {
        return null;
    }

    /**
     * Получить детальную информацию о выбранном предложении обмена
     *
     * @param tradeOfferID Идентификатор предложения обмена
     * @param language     Язык
     * @return CEconTradeOffer
     */
    public CEconTradeOffer getTradeOffer(String tradeOfferID, String language) throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", tradeOfferID);
        params.put("language", language);
        String result = doAPICall("GetTradeOffer/v1", HttpMethod.GET, params);
        return gson.fromJson(result, CEconTradeOffer.class);
    }

    /**
     * Отменить исходящее предложение обмена
     *
     * @param tradeOfferID Идентификатор предложения обмена
     * @throws SteamApiException
     */
    public void cancelTradeOffer(String tradeOfferID) throws SteamApiException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", tradeOfferID);
        String result = doAPICall("CancelTradeOffer/v1", HttpMethod.POST, params);
    }

    /**
     * Новое предложение обмена
     *
     * @param partner Идентификатор пользователя, кому предназначено предложение
     */
    public void makeOffer(SteamID partner) {

    }

    /**
     * Получить список входящих предложений обмена
     *
     * @return Список CEconTradeOffer
     * @throws IOException
     */
    public List<CEconTradeOffer> getOutcomingTradeOffers() throws IOException {
        return null;
    }

    /**
     * Получить список исходящих предложений обмена
     *
     * @return Список CEconTradeOffer
     * @throws IOException
     */
    public List<CEconTradeOffer> getIncomingTradeOffers() throws IOException {
        return null;
    }

    /**
     * Получить состояние веб-авторизации
     * @return SteamUserState
     */
    public SteamUserState getState() {
        return state;
    }

    public void login(String username, String password) {
        this.login(username, password, null, null);
    }

    /**
     * Выполнить залогинивание на сайте steamcommunity
     *
     * @param username Имя пользователя
     * @param password Пароль пользователя
     */
    public void login(String username, String password, String steamGuardText, String capText) {
        try {
            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair("username", username));
            String response = doCommunityCall("https://steamcommunity.com/login/getrsakey", HttpMethod.POST, data, false);
            RsaKeyResponse rsaKeyResponse = gson.fromJson(response, RsaKeyResponse.class);

            if (!rsaKeyResponse.isSuccess()) {
                throw new SteamCommunityException("Getting RSA key error");
            }

            BigInteger mod = new BigInteger(rsaKeyResponse.getPublickeyMod(), 16);
            BigInteger exp = new BigInteger(rsaKeyResponse.getPublickeyExp(), 16);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(spec);
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, key);
            byte[] encodedPassword = rsa.doFinal(password.getBytes("ASCII"));
            String encryptedBase64Password = DatatypeConverter.printBase64Binary(encodedPassword);

            SteamLoginResponse loginJson = null;
            String steamGuardId = "";
            do {
                LOG.info("SteamWeb: Logging In...");

                boolean captcha = loginJson != null && loginJson.isCaptchaNeeded();
                boolean steamGuard = loginJson != null && loginJson.isEmailauthNeeded();

                String time = rsaKeyResponse.getTimestamp();
                String capGID = loginJson == null ? null : loginJson.getCaptchaGid();

                data = new ArrayList<>();
                data.add(new BasicNameValuePair("password", encryptedBase64Password));
                data.add(new BasicNameValuePair("username", username));

                /** Captcha */
                if (captcha) {
                    this.state = SteamUserState.CAPTCHA_NEEDE;
                    LOG.info("SteamWeb: Captcha is needed.");
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(new URI("https://steamcommunity.com/public/captcha.php?gid=" + loginJson.getCaptchaGid()));
                    } else {
                        LOG.info("https://steamcommunity.com/public/captcha.php?gid=" + loginJson.getCaptchaGid());
                    }
                    LOG.info("SteamWeb: Type the captcha:");
                }

                data.add(new BasicNameValuePair("captchagid", captcha ? capGID : "-1"));
                data.add(new BasicNameValuePair("captcha_text", captcha ? capText : ""));
                /** Captcha end */

                /** SteamGuard */
                if (steamGuard) {
                    this.state = SteamUserState.STEAM_GUARD;
                    LOG.info("SteamWeb: SteamGuard is needed.");
                    LOG.info("SteamWeb: Type the code:");
                    steamGuardId = loginJson.getEmailsteamID();
                }

                data.add(new BasicNameValuePair("emailauth", steamGuardText));
                data.add(new BasicNameValuePair("emailsteamid", steamGuardId));
                /** SteamGuard end */

                data.add(new BasicNameValuePair("rsatimestamp", time));
                HttpResponse webResponse = communityRequest("https://steamcommunity.com/login/dologin/", HttpMethod.POST, data, false);
                loginJson = gson.fromJson(new InputStreamReader(webResponse.getEntity().getContent()), SteamLoginResponse.class);
            } while (loginJson.isCaptchaNeeded() || loginJson.isEmailauthNeeded());

            if (loginJson.isSuccess()) {
                data = new ArrayList<>();
                for (Map.Entry<String, String> stringStringEntry : loginJson.getTransferParameters().entrySet()) {
                    Map.Entry pairs = (Map.Entry) stringStringEntry;
                    data.add(new BasicNameValuePair((String) pairs.getKey(), (String) pairs.getValue()));
                }
                doCommunityCall(loginJson.getTransferUrl(), HttpMethod.POST, data, false);
                this.state = SteamUserState.LOGGED_IN;
            } else {
                this.state = SteamUserState.LOGIN_FAILED;
                throw new SteamCommunityException("Login failed with reason - " + loginJson.getMessage());
            }
        } catch (Exception e) {
            LOG.error("SteamWeb login exception.", e);
        }
    }

    /**
     * Выполнить HTTP-запрос к официальному SteamWebAPI
     *
     * @param method     Вызываемый в API метод
     * @param params     Параметры запроса в виде карты "параметр" => "значение"
     * @param httpMethod POST или GET запрос
     */
    protected String doAPICall(String method, HttpMethod httpMethod, Map<String, String> params) throws SteamApiException {
        try {
            URIBuilder builder = new URIBuilder(apiUrl + method);
            builder.setParameter("key", this.webApiKey);
            builder.setParameter("format", "json");
            switch (httpMethod) {
                case GET:
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        builder.setParameter(param.getKey(), param.getValue());
                    }
                    return IOUtils.toString(apiRequest(builder.build().toString(), HttpMethod.GET, null).getEntity().getContent());
                case POST:
                    List<NameValuePair> data = new ArrayList<>();
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        data.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                    }
                    return IOUtils.toString(apiRequest(builder.build().toString(), HttpMethod.POST, data).getEntity().getContent());
                default:
                    throw new IllegalStateException("Undefined http method");
            }
        } catch (URISyntaxException e) {
            throw new SteamApiException("Invalid API URI", e);
        } catch (IOException e) {
            throw new SteamApiException("HTTP Requset exception", e);
        }

    }

    /**
     * Выполнить HTTP-запрос к сайту steamcommunity
     *
     * @param url        Идентификатор ресурса
     * @param httpMethod HTTP-метод
     * @param data       Данные для тела запроса
     * @param isAjax     Флаг ajax-запроса
     * @return HTTP-ответ в строковом виде
     * @throws IOException
     */
    protected String doCommunityCall(String url, HttpMethod httpMethod, List<NameValuePair> data, boolean isAjax) throws IOException {
        HttpResponse response = communityRequest(url, httpMethod, data, isAjax);
        return IOUtils.toString(response.getEntity().getContent());
    }

    /**
     * Добавить cookie
     *
     * @param cookie Объект cookie
     */
    public void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    /**
     * Добавить cookie
     *
     * @param name   Имя
     * @param value  Значение
     * @param secure Флаг защищенности
     */
    public void addCookie(String name, String value, boolean secure) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setVersion(0);
        cookie.setDomain("steamcommunity.com");
        cookie.setPath("/");
        cookie.setSecure(secure);
        this.addCookie(cookie);
    }

    /**
     * Выполнить HTTP-запрос к steamcommunity
     *
     * @param url    Идентификатор ресурса
     * @param method HTTP-метод
     * @param data   Данные для тела запроса
     * @param isAjax Флаг ajax-запроса
     * @return HTTP-ответ
     * @throws IOException
     */
    private HttpResponse communityRequest(String url, HttpMethod method, List<NameValuePair> data, boolean isAjax) throws IOException {
        HttpRequest request;
        if (method == HttpMethod.POST) {
            request = new HttpPost(url);
        } else {
            request = new HttpGet(url);
        }

        request.setHeader("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        request.setHeader("Host", "steamcommunity.com");
        request.setHeader("Referer", "http://steamcommunity.com/tradeoffer/1");

        if (isAjax) {
            request.setHeader("X-Requested-With", "XMLHttpRequest");
            request.setHeader("X-Prototype-Version", "1.7");
        }

        if (data != null && request instanceof HttpPost) {
            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(data, Consts.UTF_8));
        }
        return httpClient.execute((HttpUriRequest) request);
    }

    /**
     * Выполнить HTTP-запрос к официальному API
     *
     * @param url    Идентификатор ресурка
     * @param method HTTP-метод
     * @param data   Данные для тела запроса
     * @return HTTP-ответ
     * @throws IOException
     */
    private HttpResponse apiRequest(String url, HttpMethod method, List<NameValuePair> data) throws IOException {
        HttpRequest request;
        if (method == HttpMethod.POST) {
            request = new HttpPost(url);
        } else {
            request = new HttpGet(url);
        }

        if (data != null && request instanceof HttpPost) {
            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(data, Consts.UTF_8));
        }
        return httpClient.execute((HttpUriRequest) request);
    }
}