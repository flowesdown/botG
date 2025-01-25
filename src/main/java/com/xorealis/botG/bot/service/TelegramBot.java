package com.xorealis.botG.bot.service;

import com.xorealis.botG.bot.config.BotConfig;
import com.xorealis.botG.bot.keyboard.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final KeyboardFactory keyboardFactory; // Внедрение KeyboardFactory через конструктор

    public TelegramBot(BotConfig config, KeyboardFactory keyboardFactory) {
        this.config = config;
        this.keyboardFactory = keyboardFactory;
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
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start":
                    sendStartMessage(chatId);
                    break;
                case "Меню":
                    sendMenuMessage(chatId);
                    break;
                case "Назад":
                    sendStartMessage(chatId); // Возврат к стартовому сообщению
                    break;
                case "Помощь":
                    sendHelpMessage(chatId);
                    break;
                default:
                    sendTextMessage(chatId, "Неизвестная команда.");
                    break;
            }
        }
    }

    private void sendStartMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Добро пожаловать! Выберите действие из меню ниже:");

        // Устанавливаем клавиатуру "Старт"
        message.setReplyMarkup(keyboardFactory.getStartKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenuMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы находитесь в меню. Выберите действие:");

        // Устанавливаем основное меню
        message.setReplyMarkup(keyboardFactory.getMenuKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendHelpMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Это раздел помощи. Нажмите 'Назад', чтобы вернуться в главное меню.");

        // Устанавливаем клавиатуру с кнопкой "Назад"
        message.setReplyMarkup(keyboardFactory.getBackKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}