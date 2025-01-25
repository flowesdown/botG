package com.xorealis.botG.bot.service;

import com.xorealis.botG.bot.config.BotConfig;
import com.xorealis.botG.bot.keyboard.KeyboardFactory;
import com.xorealis.botG.bot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final KeyboardFactory keyboardFactory;
    private final UserStorage userStorage;

    public TelegramBot(BotConfig config, KeyboardFactory keyboardFactory, UserStorage userStorage) {
        this.config = config;
        this.keyboardFactory = keyboardFactory;
        this.userStorage = userStorage;
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();

            switch (messageText) {
                case "/start":
                    handleStartCommand(chatId, username);
                    break;
                case "Профиль":
                    handleProfileCommand(chatId, username);
                    break;
                case "Помочь":
                    handleHelpCommand(chatId);
                    break;
                default:
                    sendTextMessage(chatId, "Неизвестная команда. Используйте кнопки.");
                    break;
            }
        }
    }

    private void handleStartCommand(long chatId, String username) {
        if (!userStorage.isUserRegistered(chatId)) {
            userStorage.addUser(new User(username, chatId));
        }
        sendStartMessage(chatId, username);
    }

    private void handleProfileCommand(long chatId, String username) {
        User user = userStorage.getUser(chatId);
        if (user != null) {
            sendTextMessage(chatId, String.format("Ваш профиль:\nИмя: %s\nID: %d", user.getName(), user.getId()));
        } else {
            sendTextMessage(chatId, "Пользователь не найден. Нажмите /start для регистрации.");
        }
    }

    private void handleHelpCommand(long chatId) {
        sendTextMessage(chatId, "Если вам нужна помощь, обратитесь к администратору. Нажмите /start, чтобы вернуться в меню.");
    }

    private void sendStartMessage(long chatId, String username) {
        String welcomeMessage = String.format("Добро пожаловать, %s! Используйте кнопки ниже для работы с ботом.", username);
        sendTextMessageWithKeyboard(chatId, welcomeMessage, keyboardFactory.getMainKeyboard());
    }

    private void sendTextMessage(long chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e, "Ошибка при отправке текстового сообщения");
        }
    }

    private void sendTextMessageWithKeyboard(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            message.setReplyMarkup(keyboard);
            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e, "Ошибка при отправке сообщения с клавиатурой");
        }
    }

    private void handleTelegramApiException(TelegramApiException e, String errorMessage) {
        System.err.println(errorMessage);
        e.printStackTrace();
    }
}
