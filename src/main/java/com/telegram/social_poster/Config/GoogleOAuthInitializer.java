package com.telegram.social_poster.Config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.telegram.social_poster.Services.AuthServices.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class GoogleOAuthInitializer {

    public static GoogleAuthService googleAuthService;
    private final GoogleOAuthConfig googleOAuthConfig;
    @Value("${server.base-url}")
    private String serverBaseUrl;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        List<String> scopes = List.of("https://www.googleapis.com/auth/youtube");

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(
                new GoogleClientSecrets.Details()
                        .setClientId(googleOAuthConfig.getClientId())
                        .setClientSecret(googleOAuthConfig.getClientSecret()));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
                .build();

        googleAuthService = new GoogleAuthService(flow, serverBaseUrl);
    }
}
