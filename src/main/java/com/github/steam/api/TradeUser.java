package com.github.steam.api;

import com.github.steam.api.adapter.GetOfferAdapter;
import com.github.steam.api.adapter.TradeOfferStateAdapter;
import com.github.steam.api.enumeration.ETradeOfferState;
import com.github.steam.api.enumeration.HttpMethod;
import com.github.steam.api.exception.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
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

    protected static final String apiUrl = "https://api.steampowered.com/IEconService/";
//    protected static final String apiUrl = "http://localhost/";

    private static CookieStore cookieStore = new BasicCookieStore();
    private CloseableHttpClient httpClient;
    private Gson gson;
    private String webApiKey;
    private CEconLoginResponse loginJson;

    private TradeUser(String webApiKey) {
        this.webApiKey = webApiKey;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ETradeOfferState.class, new TradeOfferStateAdapter())
                .registerTypeAdapter(CEconTradeOffer.class, new GetOfferAdapter())
                .create();
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public TradeUser(String webApiKey, String username, String password) throws GeneralSecurityException, GettingRsaException, IOException, LoginFailedException, GuardRequiredException, CaptchaRequiredException {
        this(webApiKey);
        this.login(username, password);
    }

    public TradeUser(String webApiKey, String username, String password, String steamGuardText, String capText) throws GeneralSecurityException, GettingRsaException, IOException, LoginFailedException, GuardRequiredException, CaptchaRequiredException {
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
    public List<TradeOffer> getTradeOffers(Map<String, String> params) throws IEconServiceException, IOException {
        if (!params.containsKey("get_sent_offers") && !params.containsKey("get_received_offers")) {
            params.put("get_received_offers", "true");
            params.put("get_sent_offers", "true");
        }
        String result = this.doAPICall("GetTradeOffers/v1", HttpMethod.GET, params);
        Type listType = new TypeToken<ArrayList<TradeOffer>>() {
        }.getType();
        List<CEconTradeOffer> tradeOffersData = gson.fromJson(result, listType);
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
    public TradeOffer getTradeOffer(long tradeOfferID, String language) throws IEconServiceException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(tradeOfferID));
        params.put("language", language);
        String result = this.doAPICall("GetTradeOffer/v1", HttpMethod.GET, params);
        CEconTradeOffer tradeOfferData = gson.fromJson(result, CEconTradeOffer.class);
        return new TradeOffer(this, tradeOfferData);
    }

    /**
     * Отменить исходящее предложение обмена
     *
     * @param tradeOfferID Идентификатор предложения обмена
     * @throws IEconServiceException
     */
    public void cancelTradeOffer(long tradeOfferID) throws IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("tradeofferid", String.valueOf(tradeOfferID));
        this.doAPICall("CancelTradeOffer/v1", HttpMethod.POST, params);
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
     * @throws IOException
     */
    public List<TradeOffer> getOutcomingTradeOffers() throws IOException, IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("get_sent_offers", "true");
        return this.getTradeOffers(params);
    }

    /**
     * Получить список исходящих предложений обмена
     *
     * @return Список CEconTradeOffer
     * @throws IOException
     */
    public List<TradeOffer> getIncomingTradeOffers() throws IOException, IEconServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("get_received_offers", "true");
        return this.getTradeOffers(params);
    }

    private void login(String username, String password) throws GeneralSecurityException, IOException, GettingRsaException, CaptchaRequiredException, GuardRequiredException, LoginFailedException {
        this.login(username, password, null, null);
    }

    /**
     * Выполнить залогинивание на сайте steamcommunity
     *
     * @param username Имя пользователя
     * @param password Пароль пользователя
     */
    private void login(String username, String password, String steamGuardText, String capText)
            throws GeneralSecurityException, IOException, GettingRsaException, CaptchaRequiredException, GuardRequiredException, LoginFailedException {
        CEconRsaKeyResponse rsaKeyResponse = this.getRSAKey(username);
        String encryptedBase64Password = this.getEncryptedPassword(password, rsaKeyResponse);

        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("password", encryptedBase64Password));
        data.add(new BasicNameValuePair("username", username));
        data.add(new BasicNameValuePair("captchagid", loginJson == null ? "-1" : loginJson.getCaptchaGid()));
        data.add(new BasicNameValuePair("captcha_text", capText));
        data.add(new BasicNameValuePair("emailauth", steamGuardText));
        data.add(new BasicNameValuePair("emailsteamid", loginJson == null ? "" : loginJson.getEmailSteamID()));

        data.add(new BasicNameValuePair("rsatimestamp", rsaKeyResponse.getTimestamp()));
        String webResponse = this.doCommunityCall("https://steamcommunity.com/login/dologin/", HttpMethod.POST, data, false);
        this.loginJson = gson.fromJson(webResponse, CEconLoginResponse.class);

        if (loginJson.isCaptchaNeeded()) {
            LOG.info("SteamWeb: Captcha is needed. https://steamcommunity.com/public/captcha.php?gid=" + loginJson.getCaptchaGid());
            throw new CaptchaRequiredException();
        }

        if (loginJson.isEmailAuthNeeded()) {
            LOG.info("SteamWeb: SteamGuard is needed.");
            throw new GuardRequiredException();
        }

        if (loginJson.isSuccess()) {
            data = new ArrayList<>();
            for (Map.Entry<String, String> stringStringEntry : loginJson.getTransferParameters().entrySet()) {
                data.add(new BasicNameValuePair(stringStringEntry.getKey(), stringStringEntry.getValue()));
            }
            this.doCommunityCall(loginJson.getTransferUrl(), HttpMethod.POST, data, false);
        } else {
            throw new LoginFailedException();
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
        BigInteger mod = new BigInteger(rsaKeyResponse.getPublickeyMod(), 16);
        BigInteger exp = new BigInteger(rsaKeyResponse.getPublickeyExp(), 16);
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
     * @throws IOException
     * @throws GettingRsaException
     */
    private CEconRsaKeyResponse getRSAKey(String username) throws IOException, GettingRsaException {
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("username", username));
        String response = this.doCommunityCall("https://steamcommunity.com/login/getrsakey", HttpMethod.POST, data, false);
        CEconRsaKeyResponse rsaKeyResponse = gson.fromJson(response, CEconRsaKeyResponse.class);

        if (!rsaKeyResponse.isSuccess()) {
            throw new GettingRsaException();
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
            URIBuilder builder = new URIBuilder(apiUrl + method);
            builder.setParameter("key", this.webApiKey);
            builder.setParameter("format", "json");
            switch (httpMethod) {
                case GET:
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        builder.setParameter(param.getKey(), param.getValue());
                    }
                    return IOUtils.toString(request(builder.build().toString(), HttpMethod.GET, null, null).getEntity().getContent());
                case POST:
                    List<NameValuePair> data = params.entrySet().stream().map(param -> new BasicNameValuePair(param.getKey(), param.getValue())).collect(Collectors.toList());
                    return IOUtils.toString(request(builder.build().toString(), HttpMethod.POST, data, null).getEntity().getContent());
                default:
                    throw new IllegalStateException("Undefined http method");
            }
        } catch (URISyntaxException e) {
            throw new IEconServiceException("Invalid API URI", e);
        } catch (IOException e) {
            throw new IEconServiceException("HTTP Requset exception", e);
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
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Host", "steamcommunity.com");
        headers.put("Referer", "http://steamcommunity.com/tradeoffer/1");

        if (isAjax) {
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("X-Prototype-Version", "1.7");
        }

        HttpResponse response = request(url, httpMethod, data, headers);
        return IOUtils.toString(response.getEntity().getContent());
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
        cookie.setDomain("steamcommunity.com");
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

}