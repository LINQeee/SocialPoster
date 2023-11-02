package com.telegram.social_poster.Services.AuthServices;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.telegram.social_poster.Config.FacebookOAuthConfig;
import com.telegram.social_poster.Utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class FacebookAuthService {

    private final FacebookOAuthConfig facebookConfig;
    private final HttpUtils httpUtils;
    @Value("${server.base-url}")
    private String serverBaseUrl;

    public String generateOAuthUrl(Long userId) {
        return facebookConfig.getBaseOAuthUrl() +
                "/dialog/oauth?client_id=" + facebookConfig.getClientId() +
                "&redirect_uri=" + serverBaseUrl + "/facebook-auth&scope=instagram_basic,instagram_content_publish,pages_show_list,pages_manage_posts&state=" + userId;
    }

    public String generateAccessToken(String code) {
        String url = facebookConfig.getBaseApiUrl() +
                "/oauth/access_token?client_id=" + facebookConfig.getClientId() +
                "&client_secret=" + facebookConfig.getClientSecret() +
                "&grant_type=authorization_code&redirect_uri=" + serverBaseUrl + "/facebook-auth&code=" + code;

        return getTokenRequest(url);
    }

    public String generateLongLiveAccessToken(String accessToken) {
        String url = facebookConfig.getBaseApiUrl() +
                "/oauth/access_token?grant_type=fb_exchange_token&client_id=" + facebookConfig.getClientId() +
                "&client_secret=" + facebookConfig.getClientSecret() +
                "&fb_exchange_token=" + accessToken;

        return getTokenRequest(url);
    }

    @SneakyThrows
    private String getTokenRequest(String url) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            return jsonObject.get("access_token").getAsString();
        }
        response.close();
        httpClient.close();
        return null;
    }

    @SneakyThrows
    public String getFacebookPageId(String accessToken) {

        String url = facebookConfig.getBaseApiUrl() +
                "/me/accounts?access_token=" + accessToken;

        JsonObject jsonObject = httpUtils.sendGetWithJsonResponse(url);
        return jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().get("id").getAsString();
    }

    public String getFacebookPageAccessToken(String accessToken, String facebookPageId) {
        String url = facebookConfig.getBaseApiUrl() +
                "/" + facebookPageId + "?fields=access_token&access_token=" + accessToken;
        return getTokenRequest(url);
    }
}
