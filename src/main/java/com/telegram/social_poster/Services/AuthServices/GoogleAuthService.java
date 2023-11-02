package com.telegram.social_poster.Services.AuthServices;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class GoogleAuthService {

    private GoogleAuthorizationCodeFlow flow;
    private String serverBaseUrl;

    public String generateOAuthLink(String stateValue) {
        String redirectUri = serverBaseUrl + "/google-auth";
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri).setState(stateValue).setAccessType("offline");
        return authorizationUrl.build();
    }

    @SneakyThrows
    public String generateRefreshToken(String code) {
        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(serverBaseUrl + "/google-auth").execute();
        return tokenResponse.getRefreshToken();
    }
}
