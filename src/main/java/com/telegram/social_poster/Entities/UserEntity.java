package com.telegram.social_poster.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserEntity {

    @Id @GeneratedValue
    private long id;
    private String userId;
    private String refreshToken;
    private int uploadVideoState;
    private String telegramChannelId;
    private String facebookAccessToken;
    private String instagramPageId;
    private String facebookPageId;
    private String facebookPageAccessToken;
}
