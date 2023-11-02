package com.telegram.social_poster.Handlers;

import com.telegram.social_poster.Config.GoogleOAuthInitializer;
import com.telegram.social_poster.Services.AuthServices.FacebookAuthService;
import com.telegram.social_poster.Services.TelegramService;
import com.telegram.social_poster.Services.EntityServices.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialConnectEventsHandler {

    private final UserEntityService userEntityService;
    private final FacebookAuthService facebookAuthService;

    public void googleAuthEvent(CallbackQuery callbackQuery, TelegramService telegramService) {
        String userId = callbackQuery.getFrom().getId().toString();
        String generatedAuthUrl = GoogleOAuthInitializer.googleAuthService.generateOAuthLink(userId);

        telegramService.editMessageWithMarkup(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                "Авторизуйтесь по ссылке для подключения google аккаунта",
                telegramService.createAuthMarkup(generatedAuthUrl)
        );
    }

    public void instagramAuthEvent(CallbackQuery callbackQuery, TelegramService telegramService) {
        Long userId = callbackQuery.getFrom().getId();
        String generatedAuthUrl = facebookAuthService.generateOAuthUrl(userId);

        telegramService.editMessageWithMarkup(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                "Для авторизации instagram вам нужно войти в аккаунт facebook со страницей, к которой *привязан бизнес аккаунт instagram*",
                telegramService.createAuthMarkup(generatedAuthUrl)
        );
    }

    public void facebookAuthEvent(CallbackQuery callbackQuery, TelegramService telegramService) {
        Long userId = callbackQuery.getFrom().getId();
        String generatedAuthUrl = facebookAuthService.generateOAuthUrl(userId);

        telegramService.editMessageWithMarkup(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                "Для авторизации facebook вам нужно перейти и авторизоваться по ссылке",
                telegramService.createAuthMarkup(generatedAuthUrl)
        );
    }

    public void telegramAuthEvent(CallbackQuery callbackQuery, TelegramService telegramService) {

        InlineKeyboardButton cancelButton = telegramService.createCallbackButton("назад", "/menu");
        List<InlineKeyboardButton> raw = new ArrayList<>();
        raw.add(cancelButton);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(raw);

        telegramService.editMessageWithMarkup(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                """
                        Для авторизации telegram вам нужно добавить бота в администраторы вашего канала:
                        1. Перейдите в ваш канал и нажмите на его аватарку
                        2. Нажмите на кнопку редактирования и перейдите в раздел "администраторы"
                        3. Нажмите добавить и введите "@SocialPoster_UploadBot"
                        4. Добавьте бота со всеми разрешениями
                        5. Готово ваш телеграмм канал привязан к боту, для привязки другого канала добавьте бота в администраторы нужного вам канала""",
                new InlineKeyboardMarkup(keyboard)
        );
    }

    public void telegramAuth(ChatMemberUpdated newChatMember) {
        Long userId = newChatMember.getFrom().getId();
        Long channelId = newChatMember.getChat().getId();
        userEntityService.changeTelegramChannelId(userId, channelId);
    }
}
