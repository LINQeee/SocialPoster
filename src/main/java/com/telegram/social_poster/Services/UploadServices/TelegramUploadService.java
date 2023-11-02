package com.telegram.social_poster.Services.UploadServices;


import com.telegram.social_poster.Entities.UserEntity;
import com.telegram.social_poster.Entities.VideoEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Service
public class TelegramUploadService {

    public String uploadVideoToChannel(VideoEntity videoEntity, UserEntity userEntity, TelegramLongPollingBot telegramBot) {
        String caption = "*" + videoEntity.getVideoTitle() + "* \n" + videoEntity.getVideoDescription();
        SendVideo sendVideo = new SendVideo(userEntity.getTelegramChannelId(), new InputFile(new File("videos/" + userEntity.getUserId() + ".mp4")));
        sendVideo.setCaption(caption);
        sendVideo.setParseMode(ParseMode.MARKDOWN);

        try {
            telegramBot.execute(sendVideo);
            return "Видео успешно загружено в telegram";
        } catch (Exception e) {
            return "Ошибка загрузки видео в telegram";
        }
    }
}
