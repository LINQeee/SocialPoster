package com.telegram.social_poster.Services.UploadServices;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.telegram.social_poster.Config.GoogleOAuthConfig;
import com.telegram.social_poster.Entities.VideoEntity;
import com.telegram.social_poster.Entities.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeUploadService {

    private final GoogleOAuthConfig googleOAuthConfig;

    @SneakyThrows
    public Credential generateCredentialFromToken(UserEntity userEntity) {
        TokenResponse response = new TokenResponse().setRefreshToken(userEntity.getRefreshToken());

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(googleOAuthConfig.getClientId(), googleOAuthConfig.getClientSecret())
                .build()
                .setFromTokenResponse(response);

        credential.refreshToken();
        return credential;
    }

    public String uploadVideo(Credential credential, VideoEntity youtubeVideoDTO, String pathToFile) {
        YouTube youtubeService = new YouTube.Builder(
                credential.getTransport(), credential.getJsonFactory(), credential)
                .build();

        Video video = generateYoutubeVideo(youtubeVideoDTO);

        FileContent mediaContent = new FileContent("video/mp4", new File(pathToFile));

        try {
            youtubeService.videos()
                    .insert("snippet,status", video, mediaContent)
                    .execute();
            return "Видео успешно загружено в youtube";
        } catch (Exception e) {
            return "Ошибка загрузки видео в youtube";
        }
    }

    private Video generateYoutubeVideo(VideoEntity youtubeVideoDTO) {
        Video video = new Video();
        VideoStatus status = new VideoStatus();
        VideoSnippet snippet = new VideoSnippet();

        status.setPrivacyStatus(youtubeVideoDTO.getPrivacyStatus());

        snippet.setTitle(youtubeVideoDTO.getVideoTitle());
        snippet.setDescription(youtubeVideoDTO.getVideoDescription());

        List<String> tags = Arrays.asList(youtubeVideoDTO.getTags().split(","));
        snippet.setTags(tags);

        video.setSnippet(snippet);
        video.setStatus(status);

        return video;
    }
}
