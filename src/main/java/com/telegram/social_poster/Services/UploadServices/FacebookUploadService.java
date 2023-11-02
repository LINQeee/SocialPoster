package com.telegram.social_poster.Services.UploadServices;


import com.google.gson.JsonObject;
import com.telegram.social_poster.Config.FacebookOAuthConfig;
import com.telegram.social_poster.Entities.VideoEntity;
import com.telegram.social_poster.Utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FacebookUploadService {

    private final HttpUtils httpUtils;
    private final FacebookOAuthConfig facebookConfig;


    public String uploadAndPublishReels(String facebookPageAccessToken, VideoEntity videoEntity) {
        String videoId = createReels(facebookPageAccessToken);
        try {
            uploadReels(facebookPageAccessToken, videoId, videoEntity.getDownloadUrl());
            return publishReels(facebookPageAccessToken, videoId, videoEntity);
        } catch (Exception e) {
            return "Ошибка загрузки видео в facebook";
        }
    }

    @SneakyThrows
    private String createReels(String facebookAccessToken) {
        String url = facebookConfig.getBaseApiUrl() + "/me/video_reels";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("access_token", facebookAccessToken);
        jsonObject.addProperty("upload_phase", "start");

        JsonObject jsonResponse = httpUtils.sendPostWithJsonResponse(url, jsonObject);

        return jsonResponse.get("video_id").getAsString();
    }

    private void uploadReels(String facebookAccessToken, String videoId, String videoUrl) throws IOException {
        String url = "https://rupload.facebook.com/video-upload/" + videoId;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "OAuth " + facebookAccessToken);
        httpPost.setHeader("file_url", videoUrl);
        httpClient.execute(httpPost);
        httpClient.close();
    }

    @SneakyThrows
    private String publishReels(String facebookAccessToken, String video_id, VideoEntity videoEntity) {
        String url = facebookConfig.getBaseApiUrl() + "/me/video_reels";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("access_token", facebookAccessToken);
        jsonObject.addProperty("upload_phase", "finish");
        jsonObject.addProperty("video_id", video_id);
        jsonObject.addProperty("video_state", "PUBLISHED");
        jsonObject.addProperty("title", videoEntity.getVideoTitle());
        jsonObject.addProperty("description", videoEntity.getVideoDescription());

        Thread.sleep(100);

        try {
            if (isVideoUploaded(facebookAccessToken, video_id)) {
                httpUtils.sendPostWithJsonResponse(url, jsonObject);
                return "Видео успешно загружено в facebook";
            } else return publishReels(facebookAccessToken, video_id, videoEntity);
        } catch (Exception e) {
            return "Ошибка загрузки видео в facebook";
        }
    }

    private boolean isVideoUploaded(String facebookAccessToken, String videoId) throws IOException {
        String url = facebookConfig.getBaseApiUrl() + "/" + videoId + "?fields=status&access_token=" + facebookAccessToken;
        JsonObject jsonResponse = httpUtils.sendGetWithJsonResponse(url);
        return jsonResponse.get("status").getAsJsonObject().get("uploading_phase").getAsJsonObject().get("status").getAsString().equals("complete");
    }
}
