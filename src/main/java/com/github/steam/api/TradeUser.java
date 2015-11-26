package com.github.steam.api;

import com.github.steam.api.adapter.AppIdAdapter;
import com.github.steam.api.adapter.ContextIdAdapter;
import com.github.steam.api.adapter.TradeOfferStateAdapter;
import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.CaptchaNeededException;
import com.github.steam.api.exception.IEconServiceException;
import com.github.steam.api.exception.SteamGuardNeededException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
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
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Пользователь стима от имени которого осуществляются действия
 *
 * @author Andrey Marchenkov
 */
public class TradeUser {

    private static final Logger LOG = LogManager.getLogger(TradeUser.class);
    private static final String API_URL = "https://api.steampowered.com/IEconService/";
    private static final String API_RESPONSE_ROOT = "response";
    private static final String COOKIE_DOMAIN = "steamcommunity.com";
    private static final String SENT_OFFERS_FLAG = "get_sent_offers";
    private static final String RECEIVED_OFFERS_FLAG = "get_received_offers";
    private static final String OFFERS_SENT_NODE = "trade_offers_sent";
    private static final String OFFERS_RECEIVED_NODE = "trade_offers_received";
    private static final String TRADEOFFERID_PARAM = "tradeofferid";
    private static final String LANGUAGE_PARAM = "language";
    private static final String OFFER_NODE = "offer";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String CAPTCHAGID_PARAM = "captchagid";
    private static final String CAPTCHA_TEXT_PARAM = "captcha_text";
    private static final String EMAILAUTH_PARAM = "emailauth";
    private static final String EMAILSTEAMID_PARAM = "emailsteamid";
    private static final String RSATIMESTAMP_PARAM = "rsatimestamp";

    private static CookieStore cookieStore = new BasicCookieStore();

    private CloseableHttpClient httpClient;
    private String webApiKey;
    private CEconLoginResponse loginJson;
    private boolean isAuthorized;


    TradeUser() {
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        this.isAuthorized = false;
    }

    private TradeUser(String webApiKey) {
        this();
        this.webApiKey = webApiKey;
    }

    public TradeUser(String webApiKey, String username, String password) throws IEconServiceException {
        this(webApiKey);
        this.login(username, password);
    }

    public TradeUser(String webApiKey, String username, String password, String steamGuardText, String capText) throws IEconServiceException {
        this(webApiKey);
        this.login(username, password, steamGuardText, capText);
    }

    /**
     * Получить всю информацию о предложениях обмена
     *
     * @param params Список параметров
     * @return Список CEconTradeOffer
     * @throws IEconServiceException
     */
    public List<TradeOffer> getTradeOffers(Map<String, String> params) throws IEconServiceException {
        if (!params.containsKey(SENT_OFFERS_FLAG) && !params.containsKey(RECEIVED_OFFERS_FLAG)) {
            params.put(RECEIVED_OFFERS_FLAG, "1");
            params.put(SENT_OFFERS_FLAG, "1");
        }
        String response = this.doAPICall("GetTradeOffers/v1", HttpMethod.GET, params);
        LOG.debug(MessageFormat.format("[GetTradeOffers/v1] response = [{0}]", response));

        Type listType = new TypeToken<List<CEconTradeOffer>>() {
        }.getType();
        Gson gson = this.getGson();
        JsonElement rootElement = new JsonParser().parse(response);
        List<CEconTradeOffer> tradeOffersSent = gson.fromJson(rootElement.getAsJsonObject()
                .getAsJsonObject(API_RESPONSE_ROOT).getAsJsonArray(OFFERS_SENT_NODE), listType);
        List<CEconTradeOffer> tradeOffersReceived = gson.fromJson(rootElement.getAsJsonObject()
                .getAsJsonObject(API_RESPONSE_ROOT).getAsJsonArray(OFFERS_RECEIVED_NODE), listType);
        List<CEconTradeOffer> tradeOffersData = new ArrayList<>();

        if (tradeOffersSent != null) {
            tradeOffersData.addAll(tradeOffersSent);
        }
        if (tradeOffersReceived != null) {
            tradeOffersData.addAll(tradeOffersReceived);
        }

        List<TradeOffer> tradeOffers = new ArrayList<>();
        for (CEconTradeOffer tradeOfferData : tradeOffersData) {
            tradeOffers.add(new TradeOffer(this, tradeOfferData));
        }
        return tradeOffers;
    }

    /**
     * Получить детальную информацию о выбранном предложении обмена
     *
     * @param tradeOfferID Идентификатор предложения обмена
     * @param language     Язык
     * @return CEconTradeOffer
     */
    public TradeOffer getTradeOffer(long tradeOfferID, String language) throws IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put(TRADEOFFERID_PARAM, String.valueOf(tradeOfferID));
        params.put(LANGUAGE_PARAM, language);
        String response = this.doAPICall("GetTradeOffer/v1", HttpMethod.GET, params);
        LOG.debug(MessageFormat.format("[GetTradeOffer/v1] response = [{0}]", response));
        Gson gson = this.getGson();
        JsonElement rootElement = new JsonParser().parse(new StringReader(response));
        CEconTradeOffer tradeOfferData = gson.fromJson(rootElement.getAsJsonObject().getAsJsonObject(API_RESPONSE_ROOT)
                .getAsJsonObject(OFFER_NODE), CEconTradeOffer.class);
        return (tradeOfferData != null) ? new TradeOffer(this, tradeOfferData) : null;
    }

    /**
     * Новое предложение обмена
     *
     * @param partner Идентификатор пользователя, кому предназначено предложение
     * @return SteamTrade - экземпляр
     * @throws Exception
     */
    public TradeOffer makeOffer(SteamID partner) throws Exception {
        return new TradeOffer(this, partner);
    }

    /**
     * Получить список входящих предложений обмена
     *
     * @return Список CEconTradeOffer
     * @throws IEconServiceException
     */
    public List<TradeOffer> getOutcomingTradeOffers() throws IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put(SENT_OFFERS_FLAG, "1");
        return this.getTradeOffers(params);
    }

    /**
     * Получить список исходящих предложений обмена
     *
     * @return Список CEconTradeOffer
     */
    public List<TradeOffer> getIncomingTradeOffers() throws IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put(RECEIVED_OFFERS_FLAG, "1");
        return this.getTradeOffers(params);
    }

    private void login(String username, String password) throws IEconServiceException {
        this.login(username, password, null, null);
    }

    /**
     * Выполнить залогинивание на сайте steamcommunity
     *
     * @param username Имя пользователя
     * @param password Пароль пользователя
     */
    private void login(String username, String password, String steamGuardText, String capText) throws IEconServiceException {
        try {
            CEconRsaKeyResponse rsaKeyResponse = this.getRSAKey(username);
            String encryptedBase64Password = this.getEncryptedPassword(password, rsaKeyResponse);
            Gson gson = this.getGson();

            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair(PASSWORD_PARAM, encryptedBase64Password));
            data.add(new BasicNameValuePair(USERNAME_PARAM, username));
            data.add(new BasicNameValuePair(CAPTCHAGID_PARAM, loginJson == null ? "-1" : loginJson.captchaGID));
            data.add(new BasicNameValuePair(CAPTCHA_TEXT_PARAM, capText));
            data.add(new BasicNameValuePair(EMAILAUTH_PARAM, steamGuardText));
            data.add(new BasicNameValuePair(EMAILSTEAMID_PARAM, loginJson == null ? "" : loginJson.emailSteamID));

            data.add(new BasicNameValuePair(RSATIMESTAMP_PARAM, rsaKeyResponse.timestamp));
            String response = this.doCommunityCall("https://steamcommunity.com/login/dologin/", HttpMethod.POST, data, false);
            LOG.debug(MessageFormat.format("[/login/dologin] response = [{0}]", response));
            this.loginJson = gson.fromJson(response, CEconLoginResponse.class);

            if (loginJson.captchaNeeded) {
                LOG.info("SteamWeb: Captcha is needed. https://steamcommunity.com/public/captcha.php?gid=" + loginJson.captchaGID);
                throw new CaptchaNeededException("Captcha needed!");
            }

            if (loginJson.emailAuthNeeded) {
                LOG.info("SteamWeb: SteamGuard is needed.");
                throw new SteamGuardNeededException("SteamGuard is needed!");
            }

            if (loginJson.success) {
                data = new ArrayList<>();
                for (Map.Entry<String, String> stringStringEntry : loginJson.transferParameters.entrySet()) {
                    data.add(new BasicNameValuePair(stringStringEntry.getKey(), stringStringEntry.getValue()));
                }
                this.doCommunityCall(loginJson.transferURL, HttpMethod.POST, data, false);
                this.isAuthorized = true;
            } else {
                throw new IEconServiceException("Login failed!");
            }
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new IEconServiceException("Encryption password exception", e);
        }
    }

    /**
     * Получить заштфрованный по ранее полученному ключу пароль
     *
     * @param password       Пароль
     * @param rsaKeyResponse Полученный ключ
     * @return Зашифрованный пароль
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    private String getEncryptedPassword(String password, CEconRsaKeyResponse rsaKeyResponse) throws GeneralSecurityException, UnsupportedEncodingException {
        BigInteger mod = new BigInteger(rsaKeyResponse.publicKeyMod, 16);
        BigInteger exp = new BigInteger(rsaKeyResponse.publicKeyExp, 16);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(spec);
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.ENCRYPT_MODE, key);
        byte[] encodedPassword = rsa.doFinal(password.getBytes("ASCII"));
        return DatatypeConverter.printBase64Binary(encodedPassword);
    }

    /**
     * Получить ключ RSA
     *
     * @param username Имя пользователя
     * @return Структура CEconRsaKeyResponse
     * @throws IEconServiceException
     */
    private CEconRsaKeyResponse getRSAKey(String username) throws IEconServiceException {
        List<NameValuePair> data = new ArrayList<>();
        Gson gson = this.getGson();
        data.add(new BasicNameValuePair(USERNAME_PARAM, username));
        String response = this.doCommunityCall("https://steamcommunity.com/login/getrsakey", HttpMethod.POST, data, false);
        LOG.debug(MessageFormat.format("[/login/getrsakey] response = [{0}]", response));
        CEconRsaKeyResponse rsaKeyResponse = gson.fromJson(response, CEconRsaKeyResponse.class);
        if (!rsaKeyResponse.success) {
            throw new IEconServiceException("Unsuccess response to [/login/getrsakey]");
        }
        return rsaKeyResponse;
    }

    /**
     * Выполнить HTTP-запрос к официальному SteamWebAPI
     *
     * @param method     Вызываемый в API метод
     * @param params     Параметры запроса в виде карты "параметр" => "значение"
     * @param httpMethod POST или GET запрос
     */
    protected String doAPICall(String method, HttpMethod httpMethod, Map<String, String> params) throws IEconServiceException {
        try {
            URIBuilder builder = new URIBuilder(API_URL + method);
            builder.setParameter("key", this.webApiKey);
            builder.setParameter("format", "json");
            switch (httpMethod) {
                case GET:
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        builder.setParameter(param.getKey(), param.getValue());
                    }
                    LOG.debug(MessageFormat.format("API GET-request: URL = [{0}]", builder.build()));
                    return IOUtils.toString(request(builder.build().toString(), HttpMethod.GET, null, null).getEntity().getContent());
                case POST:
                    List<NameValuePair> data = params.entrySet().stream().map(param -> new BasicNameValuePair(param.getKey(), param.getValue())).collect(Collectors.toList());
                    LOG.debug(MessageFormat.format("API POST-request: URL = [{0}], DATA = [{1}]", builder.build(), data));
                    return IOUtils.toString(request(builder.build().toString(), HttpMethod.POST, data, null).getEntity().getContent());
                default:
                    throw new IllegalArgumentException("Undefined http method");
            }
        } catch (URISyntaxException e) {
            throw new IEconServiceException("Invalid API URI", e);
        } catch (IOException e) {
            throw new IEconServiceException(MessageFormat.format("IOException while doing http request to [{0}]", API_URL), e);
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
     * @throws IEconServiceException
     */
    protected String doCommunityCall(String url, HttpMethod httpMethod, List<NameValuePair> data, boolean isAjax) throws IEconServiceException {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.put("Host", "steamcommunity.com");
            headers.put("Referer", "http://steamcommunity.com/tradeoffer/1");

            if (isAjax) {
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("X-Prototype-Version", "1.7");
            }

            HttpResponse response = this.request(url, httpMethod, data, headers);
            LOG.debug(MessageFormat.format("Community request: URL = [{0}], HEADES = [{1}]", url, headers));
            return IOUtils.toString(response.getEntity().getContent());
        } catch (IOException e) {
            throw new IEconServiceException("IOException while doing http request to [steamcommunity.com]", e);
        }
    }

    /**
     * Добавить cookie
     *
     * @param cookie Объект cookie
     */
    public static void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    /**
     * Добавить cookie
     *
     * @param name   Имя
     * @param value  Значение
     * @param secure Флаг защищенности
     */
    public static void addCookie(String name, String value, boolean secure) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setVersion(0);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setSecure(secure);
        TradeUser.addCookie(cookie);
    }

    /**
     * Выполнить HTTP-запрос к steamcommunity
     *
     * @param url    Идентификатор ресурса
     * @param method HTTP-метод
     * @param data   Данные для тела запроса
     * @return HTTP-ответ
     * @throws IOException
     */
    private HttpResponse request(String url, HttpMethod method, List<NameValuePair> data, Map<String, String> headers) throws IOException {
        HttpRequest request;
        if (method == HttpMethod.POST) {
            request = new HttpPost(url);
        } else {
            request = new HttpGet(url);
        }
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        if (data != null && request instanceof HttpPost) {
            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(data, Consts.UTF_8));
        }
        return httpClient.execute((HttpUriRequest) request);
    }

    /**
     * Получить Gson-объект со всеми подключенными адаптерами
     *
     * @return Gson
     */
    private Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(EAppID.class, new AppIdAdapter())
                .registerTypeAdapter(EContextID.class, new ContextIdAdapter())
                .registerTypeAdapter(ETradeOfferState.class, new TradeOfferStateAdapter())
                .create();
    }

    /**
     * Является ли пользователь авторизованным на steamcommunity.com
     *
     * @return Флаг авторизации
     */
    public boolean isAuthorized() {
        return this.isAuthorized;
    }


    /**
     * Объект, который приходит в ответ на /login/getrsakey
     *
     * @author Andrey Marchenkov
     */
    private class CEconRsaKeyResponse {

        @SerializedName("success")
        private boolean success;

        @SerializedName("publickey_mod")
        private String publicKeyMod;

        @SerializedName("publickey_exp")
        private String publicKeyExp;

        @SerializedName("timestamp")
        private String timestamp;

    }

    /**
     * Объект, который приходит в ответ на /login/dologin
     *
     * @author Andrey Marchenkov
     */
    private class CEconLoginResponse {

        @SerializedName("success")
        private boolean success;

        @SerializedName("message")
        private String message;

        @SerializedName("captcha_needed")
        private boolean captchaNeeded;

        @SerializedName("captcha_gid")
        private String captchaGID;

        @SerializedName("emailauth_needed")
        private boolean emailAuthNeeded;

        @SerializedName("emailsteamid")
        private String emailSteamID;

        @SerializedName("transfer_parameters")
        private Map<String, String> transferParameters = new HashMap<>();

        @SerializedName("transfer_url")
        private String transferURL;

    }

}