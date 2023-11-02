package com.telegram.social_poster.Services.AuthServices;


import com.google.gson.JsonObject;
import com.telegram.social_poster.Config.FacebookOAuthConfig;
import com.telegram.social_poster.Utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstagramAuthService {

    private final FacebookOAuthConfig facebookConfig;
    private final HttpUtils httpUtils;

    @SneakyThrows
    public String getInstagramPageId(String accessToken, String facebookPageId) {
        String url = facebookConfig.getBaseApiUrl() +
                "/" + facebookPageId + "?fields=instagram_business_account&access_token=" + accessToken;

        JsonObject jsonObject = httpUtils.sendGetWithJsonResponse(url);
        return jsonObject.getAsJsonObject("instagram_business_account").get("id").getAsString();
    }
}
