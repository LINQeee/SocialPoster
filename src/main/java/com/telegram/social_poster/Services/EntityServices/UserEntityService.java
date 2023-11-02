package com.telegram.social_poster.Services.EntityServices;

import com.telegram.social_poster.Entities.UserEntity;
import com.telegram.social_poster.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserEntityService {

    private final UserRepository userRepository;

    public void changeRefreshToken(Long userId, String accessToken) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId.toString());
        userEntity.setRefreshToken(accessToken);
        userRepository.save(userEntity);
    }

    public void createUserIfNotExist(Long userId) {
        if (Optional.ofNullable(findUserByUserId(userId)).isPresent()) return;
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId.toString());
        userRepository.save(userEntity);
    }

    public UserEntity findUserByUserId(Long userId) {
        return userRepository.findUserEntityByUserId(userId.toString());
    }

    public void changeUploadStatus(Long userId, int newStatus) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId.toString());
        userEntity.setUploadVideoState(newStatus);
        userRepository.save(userEntity);
    }

    public void changeTelegramChannelId(Long userId, Long channelId) {
        UserEntity userEntity = findUserByUserId(userId);
        userEntity.setTelegramChannelId(channelId.toString());
        userRepository.save(userEntity);
    }

    public void changeFacebookAccessToken(String userId, String accessToken) {
        UserEntity userEntity = findUserByUserId(Long.valueOf(userId));
        userEntity.setFacebookAccessToken(accessToken);
        userRepository.save(userEntity);
    }

    public void changeInstagramPageId(String userId, String instagramPageId) {
        UserEntity userEntity = findUserByUserId(Long.valueOf(userId));
        userEntity.setInstagramPageId(instagramPageId);
        userRepository.save(userEntity);
    }

    public void changeFacebookPageId(String userId, String facebookPageId) {
        UserEntity userEntity = findUserByUserId(Long.valueOf(userId));
        userEntity.setFacebookPageId(facebookPageId);
        userRepository.save(userEntity);
    }

    public void changeFacebookPageAccessToken(String userId, String facebookPageAccessToken) {
        UserEntity userEntity = findUserByUserId(Long.valueOf(userId));
        userEntity.setFacebookPageAccessToken(facebookPageAccessToken);
        userRepository.save(userEntity);
    }
}
