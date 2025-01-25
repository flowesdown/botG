package com.xorealis.botG.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

@Component
public class KeyboardFactory {

    private final KeyboardHelper keyboardHelper;

    public KeyboardFactory() {
        this.keyboardHelper = new KeyboardHelper();
    }

    // Клавиатура "Старт"
    public ReplyKeyboardMarkup getStartKeyboard() {
        List<List<String>> buttons = List.of(
                List.of("Меню", "Помощь")
        );
        return keyboardHelper.getKeyboard(buttons); // Генерация клавиатуры через хелпер
    }

    // Клавиатура "Назад"
    public ReplyKeyboardMarkup getBackKeyboard() {
        List<List<String>> buttons = List.of(
                List.of("Назад")
        );
        return keyboardHelper.getKeyboard(buttons); // Генерация клавиатуры через хелпер
    }
}