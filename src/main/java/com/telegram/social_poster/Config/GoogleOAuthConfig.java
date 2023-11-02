package com.telegram.social_poster.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Data
public class GoogleOAuthConfig {

    @Value("${google.oauth.client.id}")
    private String clientId;

    @Value("${google.oauth.client.secret}")
    private String clientSecret;
}
