package com.telegram.social_poster.Services.EntityServices;

import com.telegram.social_poster.Entities.VideoEntity;
import com.telegram.social_poster.Repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoEntityService {

    private final VideoRepository videoRepository;

    public void createNewVideo(Long userId) {
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setUserId(userId.toString());
        videoRepository.save(videoEntity);
    }

    public void changeVideoTitle(Long userId, String title) {
        VideoEntity videoEntity = videoRepository.findVideoEntityByUserId(userId.toString());
        videoEntity.setVideoTitle(title);
        videoRepository.save(videoEntity);
    }

    public void changeVideoDescription(Long userId, String description) {
        VideoEntity videoEntity = videoRepository.findVideoEntityByUserId(userId.toString());
        videoEntity.setVideoDescription(description);
        videoRepository.save(videoEntity);
    }

    public void changeVideoTags(Long userId, String tags) {
        VideoEntity videoEntity = videoRepository.findVideoEntityByUserId(userId.toString());
        videoEntity.setTags(tags);
        videoRepository.save(videoEntity);
    }

    public void changePrivacyStatus(Long userId, String privacyStatus) {
        VideoEntity videoEntity = videoRepository.findVideoEntityByUserId(userId.toString());
        videoEntity.setPrivacyStatus(privacyStatus);
        videoRepository.save(videoEntity);
    }

    public VideoEntity getVideoByUserId(Long userId) {
        return videoRepository.findVideoEntityByUserId(userId.toString());
    }

    public void deleteVideoByUserId(Long userId) {
        VideoEntity videoEntity = getVideoByUserId(userId);
        videoRepository.delete(videoEntity);
    }

    public void changeDownloadUrl(Long userId, String downloadUrl) {
        VideoEntity videoEntity = videoRepository.findVideoEntityByUserId(userId.toString());
        videoEntity.setDownloadUrl(downloadUrl);
        videoRepository.save(videoEntity);
    }
}
