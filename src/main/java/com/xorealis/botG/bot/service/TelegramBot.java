package com.xorealis.botG.bot.service;

import com.xorealis.botG.bot.config.BotConfig;
import com.xorealis.botG.bot.keyboard.KeyboardFactory;
import com.xorealis.botG.bot.keyboard.KeyboardHelper;
import com.xorealis.botG.bot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final KeyboardFactory keyboardFactory; // Внедрение KeyboardFactory через конструктор
    private UserStorage userStorage;

    public TelegramBot(BotConfig config, KeyboardFactory keyboardFactory,UserStorage userStorage) throws IOException {
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
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start":
                    sendStartMessage(chatId);
                    break;
                case "Назад":
                    sendStartMessage(chatId); // Возврат к стартовому сообщению
                    break;
                case "Меню":
                    if(userStorage.getUsers().isEmpty()){
                        sendTextMessage(chatId,"Нет зарегистрированых пользователей");
                    }else{
                        sendTextMessage(chatId, userStorage.toString());
                    }
                    break;
                case "Помощь":
                    userStorage.addUser(new User("Михаил",12));
                    sendHelpMessage(chatId);
                    break;
                default:
                    sendTextMessage(chatId, "Неизвестная команда.");
                    break;
            }
        }
    }

    private void sendStartMessage(long chatId) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));

            message.setText("Добро пожаловать! Ваш аккаунт был зарегистрирован. Ниже информация.");


            // Устанавливаем клавиатуру "Старт"
            message.setReplyMarkup(keyboardFactory.getStartKeyboard());

            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e, "Ошибка при отправке стартового сообщения");
        }
    }

    private void sendHelpMessage(long chatId) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Это раздел помощи. Нажмите 'Назад', чтобы вернуться в главное меню.");

            // Устанавливаем клавиатуру с кнопкой "Назад"
            message.setReplyMarkup(keyboardFactory.getBackKeyboard());

            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e, "Ошибка при отправке сообщения помощи");
        }
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

    private void handleTelegramApiException(TelegramApiException e, String errorMessage) {
        System.err.println(errorMessage);
        e.printStackTrace();
    }
}