package com.telegram.social_poster.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Data
public class FacebookOAuthConfig {

    @Value("${facebook.oauth.client.id}")
    private String clientId;

    @Value("${facebook.oauth.client.secret}")
    private String clientSecret;

    @Value("${facebook.oauth.base-url}")
    private String baseOAuthUrl;

    @Value("${facebook.api.base-url}")
    private String baseApiUrl;
}
