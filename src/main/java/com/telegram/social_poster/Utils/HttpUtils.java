package com.telegram.social_poster.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpUtils {

    public JsonObject sendGetWithJsonResponse(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
        String jsonContent = EntityUtils.toString(response.getEntity());
        response.close();
        httpClient.close();
        return JsonParser.parseString(jsonContent).getAsJsonObject();
    }

    private HttpPost createPostWithJson(String url, JsonObject jsonObject) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return httpPost;
    }

    public JsonObject sendPostWithJsonResponse(String url, JsonObject jsonObject) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(createPostWithJson(url, jsonObject));
        String jsonContent = EntityUtils.toString(response.getEntity());
        response.close();
        httpClient.close();
        return JsonParser.parseString(jsonContent).getAsJsonObject();
    }
}
