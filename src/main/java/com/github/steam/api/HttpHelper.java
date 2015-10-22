package com.github.steam.api;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHelper {

    private static final Log LOG = LogFactory.getLog(HttpHelper.class);

    /**
     * GET - запрос к API
     * @param uri Идентификатор ресурса
     * @return Текст ответа
     * @throws IOException
     */
    public String sendGet(URI uri) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpRequest = new HttpGet(uri);
            try (CloseableHttpResponse response = httpclient.execute(httpRequest)) {
                HttpEntity entity = response.getEntity();
                String result = IOUtils.toString(entity.getContent());
                EntityUtils.consume(entity);
                return result;
            }
        }
    }

    /**
     * POST - запрос к API
     * @param uri Идентификатор ресурса
     * @param params Параметры тела запроса
     * @return Текст ответа
     * @throws IOException
     */
    public String sendPost(URI uri, Map<String, String> params) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpRequest = new HttpPost(uri);
            List<NameValuePair> nvps = new ArrayList<>();
            for (Map.Entry<String, String> param : params.entrySet()) {
                nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            httpRequest.setEntity(new UrlEncodedFormEntity(nvps));

            try (CloseableHttpResponse response = httpclient.execute(httpRequest)) {
                int code = response.getStatusLine().getStatusCode();
                if (!(code >= 200 && code < 300)) {
                    throw new IOException(MessageFormat.format("Error calling steam web service. Unexpected status [{}]", code));
                }
                HttpEntity entity = response.getEntity();
                String result = IOUtils.toString(entity.getContent());
                EntityUtils.consume(entity);
                return result;
            }
        }
    }
}
