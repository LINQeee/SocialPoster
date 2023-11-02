package com.telegram.social_poster.Config;

import com.telegram.social_poster.Listeners.TelegramBotListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class BotInitializer {

    private final TelegramBotListener telegramBotService;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBotService);
            System.out.println("Bot " + telegramBotService.getBotUsername() + " started!");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
