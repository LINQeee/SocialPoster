package com.telegram.social_poster.Handlers;

import com.google.api.client.auth.oauth2.Credential;
import com.telegram.social_poster.Entities.UserEntity;
import com.telegram.social_poster.Entities.VideoEntity;
import com.telegram.social_poster.Services.*;
import com.telegram.social_poster.Services.EntityServices.UserEntityService;
import com.telegram.social_poster.Services.EntityServices.VideoEntityService;
import com.telegram.social_poster.Services.UploadServices.FacebookUploadService;
import com.telegram.social_poster.Services.UploadServices.InstagramUploadService;
import com.telegram.social_poster.Services.UploadServices.TelegramUploadService;
import com.telegram.social_poster.Services.UploadServices.YoutubeUploadService;
import com.telegram.social_poster.Utils.UploadVideoState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.util.StringUtils;

@Component
@RequiredArgsConstructor
public class VideoUploadEventsHandler {

    private final FileService fileService;
    private final UserEntityService userEntityService;
    private final VideoEntityService videoEntityService;
    private final YoutubeUploadService youtubeService;
    private final TelegramUploadService telegramUploadService;
    private final InstagramUploadService instagramUploadService;
    private final FacebookUploadService facebookUploadService;

    public void handleUploadCommand(Message message, TelegramService telegramService) {
        UserEntity userEntity = userEntityService.findUserByUserId(message.getFrom().getId());

        switch (UploadVideoState.fromInt(userEntity.getUploadVideoState())) {
            case NOT_STARTED -> downloadVideoEvent(message, telegramService);
            case VIDEO_DOWNLOADED -> askVideoTitle(message, telegramService);
            case TITLE_SET -> askVideoDescription(message, telegramService);
            case DESCRIPTION_SET -> askVideoTags(message, telegramService);
            case TAGS_SET -> askPrivacyStatus(message, telegramService);
            case PRIVACY_STATUS_SET -> publishVideoToConnectedServices(message, telegramService);
        }
    }

    private void publishVideoToConnectedServices(Message message, TelegramService telegramService) {
        VideoEntity videoEntity = videoEntityService.getVideoByUserId(message.getFrom().getId());
        UserEntity userEntity = userEntityService.findUserByUserId(message.getFrom().getId());
        publishToYoutube(videoEntity, userEntity, telegramService, message.getChatId());
        publishToTelegram(videoEntity, userEntity, telegramService, message.getChatId());
        publishToInstagram(videoEntity, userEntity, telegramService, message.getChatId());
        publishToFacebook(videoEntity, userEntity, telegramService, message.getChatId());

        userEntityService.changeUploadStatus(message.getFrom().getId(), UploadVideoState.NOT_STARTED.getNumber());
        videoEntityService.deleteVideoByUserId(message.getFrom().getId());

        telegramService.sendMessage(message.getChatId(), "Видео опубликовано во все подключенные соц-сети!");
    }

    private void publishToYoutube(VideoEntity videoEntity, UserEntity userEntity, TelegramService telegramService, Long chatId) {
        if (userEntity.getRefreshToken() == null) return;
        Credential credential = youtubeService.generateCredentialFromToken(userEntity);
        String videoPath = "videos/" + userEntity.getUserId() + ".mp4";
        String response = youtubeService.uploadVideo(credential, videoEntity, videoPath);
        telegramService.sendMessage(chatId, response);
    }

    private void publishToTelegram(VideoEntity videoEntity, UserEntity userEntity, TelegramService telegramService, Long chatId) {
        if (userEntity.getTelegramChannelId() == null) return;
        String response = telegramUploadService.uploadVideoToChannel(videoEntity, userEntity, telegramService.getTelegramBot());
        telegramService.sendMessage(chatId, response);
    }

    private void publishToInstagram(VideoEntity videoEntity, UserEntity userEntity, TelegramService telegramService, Long chatId) {
        if (userEntity.getInstagramPageId() == null) return;
        String response = instagramUploadService.publishReels(userEntity, videoEntity, null);
        telegramService.sendMessage(chatId, response);
    }

    private void publishToFacebook(VideoEntity videoEntity, UserEntity userEntity, TelegramService telegramService, Long chatId) {
        if (userEntity.getFacebookPageAccessToken() == null) return;
        String response = facebookUploadService.uploadAndPublishReels(userEntity.getFacebookPageAccessToken(), videoEntity);
        telegramService.sendMessage(chatId, response);
    }

    private void askPrivacyStatus(Message message, TelegramService telegramService) {
        String status = telegramService.getCommandPlainParameter(message);
        if (isCommandParameterInvalid(status, message.getChatId(), telegramService, "Параметр доступа")) return;
        if (status.equalsIgnoreCase("публичный")) {
            videoEntityService.changePrivacyStatus(message.getFrom().getId(), "public");
        } else if (status.equalsIgnoreCase("приватный")) {
            videoEntityService.changePrivacyStatus(message.getFrom().getId(), "private");
        } else {
            telegramService.sendMessage(message.getChatId(), "Не удалось распознать вид доступа, попробуйте ещё раз");
            return;
        }
        userEntityService.changeUploadStatus(message.getFrom().getId(), UploadVideoState.PRIVACY_STATUS_SET.getNumber());
        telegramService.sendMessage(message.getChatId(), "Отлично, все данные установлены. Вы уверены что хотите выложить ролик? (\"/upload да\" или \"/upload нет\")");
    }

    private void askVideoTitle(Message message, TelegramService telegramService) {
        String videoTitle = telegramService.getCommandPlainParameter(message);
        if (isCommandParameterInvalid(videoTitle, message.getChatId(), telegramService, "Название видео")) return;
        videoEntityService.changeVideoTitle(message.getFrom().getId(), videoTitle);
        userEntityService.changeUploadStatus(message.getFrom().getId(), UploadVideoState.TITLE_SET.getNumber());
        telegramService.sendMessage(message.getChatId(), "Введите описание видео, используя команду \"/upload описание видео\"");
    }

    private void askVideoDescription(Message message, TelegramService telegramService) {
        String videoDescription = telegramService.getCommandPlainParameter(message);
        if (isCommandParameterInvalid(videoDescription, message.getChatId(), telegramService, "Описание видео")) return;
        videoEntityService.changeVideoDescription(message.getFrom().getId(), videoDescription);
        userEntityService.changeUploadStatus(message.getFrom().getId(), UploadVideoState.DESCRIPTION_SET.getNumber());
        telegramService.sendMessage(message.getChatId(), "Введите теги к видео, разделяя их запятой, используя команду \"/upload тег 1,тег 2\"");
    }

    private void askVideoTags(Message message, TelegramService telegramService) {
        String tags = telegramService.getCommandPlainParameter(message);
        if (isCommandParameterInvalid(tags, message.getChatId(), telegramService, "Теги видео")) return;
        videoEntityService.changeVideoTags(message.getFrom().getId(), tags);
        userEntityService.changeUploadStatus(message.getFrom().getId(), UploadVideoState.TAGS_SET.getNumber());
        telegramService.sendMessage(message.getChatId(), "Выберите доступ к видео, используя команду \"/upload публичный\" или \"/upload приватный\"");
    }

    private void downloadVideoEvent(Message message, TelegramService telegramService) {
        if (!message.hasDocument() && !message.hasVideo()) {
            telegramService.sendMessage(message.getChatId(), "Приложите видео к команде!");
            return;
        }
        Message statusMessage = telegramService.sendMessage(message.getChatId(), "Загружаем видео...");
        videoEntityService.createNewVideo(message.getFrom().getId());
        try {
            downloadVideo(message, telegramService);
            telegramService.editTelegramMessage(statusMessage.getChatId(), statusMessage.getMessageId(), "Видео успешно загружено ✔");
            userEntityService.changeUploadStatus(message.getFrom().getId(), UploadVideoState.VIDEO_DOWNLOADED.getNumber());
            telegramService.sendMessage(statusMessage.getChatId(), "Введите название видео, используя команду \"/upload название видео\"");
        } catch (TelegramApiException e) {
            telegramService.editTelegramMessage(statusMessage.getChatId(), statusMessage.getMessageId(), "Видео не должно превышать вес в 20мб!");
            videoEntityService.deleteVideoByUserId(message.getFrom().getId());
        }
    }

    private void downloadVideo(Message message, TelegramService telegramService) throws TelegramApiException {
        String fileUrl = telegramService.getFileUrl(message);
        videoEntityService.changeDownloadUrl(message.getFrom().getId(), fileUrl);

        fileService.downloadAndSaveVideo(
                fileUrl,
                "videos/" + message.getFrom().getId() + ".mp4"
        );
    }

    private boolean isCommandParameterInvalid(String parameter, Long chatId, TelegramService telegramService, String parameterName) {
        if (StringUtils.isEmptyOrWhitespace(parameter)) {
            telegramService.sendMessage(chatId, parameterName + " не может быть пустым");
            return true;
        }
        return false;
    }
}
