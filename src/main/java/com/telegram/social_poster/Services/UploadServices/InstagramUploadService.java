package com.telegram.social_poster.Services.UploadServices;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.telegram.social_poster.Config.FacebookOAuthConfig;
import com.telegram.social_poster.Entities.UserEntity;
import com.telegram.social_poster.Entities.VideoEntity;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class InstagramUploadService {

    private final FacebookOAuthConfig facebookConfig;


    public String publishReels(UserEntity userEntity, VideoEntity videoEntity, String containerId) {
        try {
            String uploadedContainerId = containerId == null ? uploadReels(userEntity, videoEntity) : containerId;

            String url = facebookConfig.getBaseApiUrl() + "/" + userEntity.getInstagramPageId() + "/media_publish";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("access_token", userEntity.getFacebookAccessToken());
            jsonObject.addProperty("creation_id", uploadedContainerId);

            Thread.sleep(100);
            CloseableHttpClient httpClient = HttpClients.createDefault();

            CloseableHttpResponse response = httpClient.execute(createPostWithJson(url, jsonObject));
            String jsonContent = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(jsonContent).getAsJsonObject();

            if (response.getStatusLine().getStatusCode() == 400) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                if (error.get("code").getAsInt() == 9007 && error.get("error_subcode").getAsInt() == 2207027) {
                    return publishReels(userEntity, videoEntity, uploadedContainerId);
                } else return "Ошибка загрузки видео в instagram";
            }
            response.close();
            httpClient.close();

            return "Видео успешно загружено в instagram";
        } catch (Exception e) {
            return "Ошибка загрузки видео в instagram";
        }

    }

    private String uploadReels(UserEntity userEntity, VideoEntity videoEntity) throws IOException {
        String url = facebookConfig.getBaseApiUrl() + "/" + userEntity.getInstagramPageId() + "/media";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("access_token", userEntity.getFacebookAccessToken());
        jsonObject.addProperty("video_url", videoEntity.getDownloadUrl());
        jsonObject.addProperty("caption", videoEntity.getVideoDescription());
        jsonObject.addProperty("media_type", "REELS");

        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = httpClient.execute(createPostWithJson(url, jsonObject));
        String jsonContent = EntityUtils.toString(response.getEntity());
        response.close();
        httpClient.close();

        JsonObject jsonResponse = JsonParser.parseString(jsonContent).getAsJsonObject();

        return jsonResponse.get("id").getAsString();
    }

    private HttpPost createPostWithJson(String url, JsonObject jsonObject) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return httpPost;
    }
}
