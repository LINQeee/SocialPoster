package com.telegram.social_poster.Services;

import com.telegram.social_poster.Entities.UserEntity;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Getter
public class TelegramService {

    private final TelegramLongPollingBot telegramBot;

    public TelegramService(TelegramLongPollingBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public InlineKeyboardButton createUrlButton(String url) {

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("авторизоваться");
        button.setUrl(url);

        return button;
    }

    public InlineKeyboardButton createCallbackButton(String buttonText, String callbackData) {

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData(callbackData);

        return button;
    }

    public String getCommandPlainParameter(Message message) {
        List<String> content;
        if (message.hasText()) {
            content = new ArrayList<>(Arrays.asList(message.getText().split(" ")));
        } else {
            content = new ArrayList<>(Arrays.asList(message.getCaption().split(" ")));
        }
        content.remove(0);
        return String.join(" ", content);
    }

    @SneakyThrows
    public Message sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        return telegramBot.execute(sendMessage);
    }

    @SneakyThrows
    public void editTelegramMessage(Long chatId, Integer messageId, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);

        telegramBot.execute(editMessageText);
    }

    @SneakyThrows
    public void sendMessageWithMarkup(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);
        telegramBot.execute(sendMessage);
    }

    public String getFileUrl(Message message) throws TelegramApiException {

        String fileId = message.hasVideo() ? message.getVideo().getFileId() : message.getDocument().getFileId();

        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = telegramBot.execute(getFile);
        return "https://api.telegram.org/file/bot" + telegramBot.getBotToken() + "/" + file.getFilePath();
    }

    @SneakyThrows
    public void editMessageWithMarkup(Long chatId, Integer messageId, String newText, InlineKeyboardMarkup markup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        editMessageText.setReplyMarkup(markup);

        telegramBot.execute(editMessageText);
    }

    public void sendMenu(Long chatId, UserEntity userEntity) {
        sendMessageWithMarkup(
                chatId,
                "Эмодзи ✔ означает, что соц-сеть привязана к боту, ❌ - соц-сеть не привязана к боту\nДля перепривязки соц-сети пройдите процесс авторизации повторно",
                getMenuMarkup(userEntity)
        );
    }

    public void backToMenu(Long chatId, int messageId, UserEntity userEntity) {
        editMessageWithMarkup(
                chatId,
                messageId,
                "Эмодзи ✔ означает, что соц-сеть привязана к боту, ❌ - соц-сеть не привязана к боту\nДля перепривязки соц-сети пройдите процесс авторизации повторно",
                getMenuMarkup(userEntity)
        );
    }

    private InlineKeyboardMarkup getMenuMarkup(UserEntity userEntity) {
        InlineKeyboardButton googleButton = createCallbackButton("google " + (userEntity.getRefreshToken() == null ? "❌" : "✔"), "/google-auth");
        InlineKeyboardButton telegramButton = createCallbackButton("telegram " + (userEntity.getTelegramChannelId() == null ? "❌" : "✔"), "/telegram-auth");
        InlineKeyboardButton facebookButton = createCallbackButton("facebook " + (userEntity.getFacebookPageId() == null ? "❌" : "✔"), "/facebook-auth");
        InlineKeyboardButton instagramButton = createCallbackButton("instagram " + (userEntity.getInstagramPageId() == null ? "❌" : "✔"), "/instagram-auth");

        return createTwoRawsMarkup(googleButton, telegramButton, facebookButton, instagramButton);
    }

    public InlineKeyboardMarkup createTwoRawsMarkup(InlineKeyboardButton... buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>(Arrays.asList(buttons).subList(0, buttons.length / 2));

        List<InlineKeyboardButton> secondRow = new ArrayList<>(Arrays.asList(buttons).subList(buttons.length / 2, buttons.length));

        keyboard.add(firstRow);
        keyboard.add(secondRow);
        markup.setKeyboard(keyboard);
        return markup;
    }

    public InlineKeyboardMarkup createAuthMarkup(String url) {
        InlineKeyboardButton urlButton = createUrlButton(url);
        InlineKeyboardButton cancelButton = createCallbackButton("назад", "/menu");

        return createTwoRawsMarkup(urlButton, cancelButton);
    }
}
