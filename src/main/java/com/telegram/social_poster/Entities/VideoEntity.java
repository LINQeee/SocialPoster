package com.telegram.social_poster.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class VideoEntity {

    @Id
    private String userId;
    private String privacyStatus;
    private String videoTitle;
    private String videoDescription;
    private String tags;
    private String downloadUrl;
}
