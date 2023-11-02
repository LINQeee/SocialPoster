package com.telegram.social_poster.Listeners;

import com.telegram.social_poster.Config.BotConfig;
import com.telegram.social_poster.Handlers.SocialConnectEventsHandler;
import com.telegram.social_poster.Handlers.VideoUploadEventsHandler;
import com.telegram.social_poster.Services.EntityServices.UserEntityService;
import com.telegram.social_poster.Services.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramBotListener extends TelegramLongPollingBot {

    private final UserEntityService userEntityService;
    private final BotConfig botConfig;
    private final TelegramService telegramService = new TelegramService(this);
    private final VideoUploadEventsHandler uploadEventsHandler;
    private final SocialConnectEventsHandler socialEventsHandler;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();

        if (update.hasMessage() && message.getChat().isUserChat()) {
            userEntityService.createUserIfNotExist(message.getFrom().getId());
        }
        if (update.hasMessage() && message.hasText() && message.getChat().isUserChat()) {
            handleUserCommands(message.getText(), message);
        } else if (update.hasMessage() && message.getCaption() != null && message.getChat().isUserChat()) {
            handleUserCommands(message.getCaption(), message);
        } else if (update.hasCallbackQuery()) {
            handleCallBackQuery(update.getCallbackQuery());
        }

        Optional<ChatMember> newChatMember = Optional.ofNullable(update.getMyChatMember())
                .map(ChatMemberUpdated::getNewChatMember);
        if (newChatMember.isPresent() && "administrator".equals(newChatMember.get().getStatus()) && getBotUsername().equals(newChatMember.get().getUser().getUserName())) {
            socialEventsHandler.telegramAuth(update.getMyChatMember());
        }
    }

    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        switch (callbackQuery.getData()) {
            case "/google-auth" -> socialEventsHandler.googleAuthEvent(callbackQuery, telegramService);
            case "/menu" -> telegramService.backToMenu(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId(),
                    userEntityService.findUserByUserId(callbackQuery.getFrom().getId())
            );
            case "/facebook-auth" -> socialEventsHandler.facebookAuthEvent(callbackQuery, telegramService);
            case "/instagram-auth" -> socialEventsHandler.instagramAuthEvent(callbackQuery, telegramService);
            case "/telegram-auth" -> socialEventsHandler.telegramAuthEvent(callbackQuery, telegramService);
        }
    }

    private void handleUserCommands(String commandText, Message message) {
        switch (commandText) {
            case "/start" -> telegramService.sendMessage(message.getChatId(), """
                    *Добро пожаловать в SocialPosterBot!*
                    Для начала работы вам нужно привязать свои соц-сети к боту, для этого используйте команду "/auth"
                    *Чтобы загрузить видео введите команду "/upload" и следуйте инструкциям*
                    видеофайлы должны быть в формате mp4 и весить не больше 20мб
                    загружать видео через телеграмм можно как в виде файлов так и видео""");
            case "/auth" ->
                    telegramService.sendMenu(message.getChatId(), userEntityService.findUserByUserId(message.getFrom().getId()));
        }
        if (commandText.contains("/upload")) uploadEventsHandler.handleUploadCommand(message, telegramService);
    }
}
