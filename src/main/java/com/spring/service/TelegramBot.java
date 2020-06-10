package com.spring.service;

import com.spring.model.FilterUrl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.spring.service.Constant.Command.*;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "@CarsFilterUaBot";
    private static final String BOT_TOKEN = "1206420147:AAHoIObnM9lHa7g57ncOAbhrYCjKyCmSfLg";
    private FilterBotTelegramManager manager;
    public ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    public InlineKeyboardMarkup callbackKeyboar = new InlineKeyboardMarkup();

    public TelegramBot(FilterBotTelegramManager manager) {
        this.manager = manager;
        addButton();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        if (update.hasMessage()) {
            manager.managedMsg(msg);
        } else if (update.hasCallbackQuery()) {
            manager.managedCallbackMsg(update.getCallbackQuery());
        }

    }


    public String getBotUsername() {
        return BOT_NAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }


    private void addButton() {
        this.replyKeyboardMarkup.setSelective(true);
        this.replyKeyboardMarkup.setResizeKeyboard(true);
        this.replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(ADD_FILTER));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(ALL_FILTER));

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(DELETE_FILTER));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        // и устанваливаем этот список нашей клавиатуре
        this.replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public void addCallbackButton(List<FilterUrl> filterUrls) {
        if (filterUrls == null || filterUrls.isEmpty()) {
            return;
        }
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        for (FilterUrl filterUrl : filterUrls) {
            List<InlineKeyboardButton> btn = new ArrayList<>();
            btn.add(new InlineKeyboardButton().setText(filterUrl.getTitle())
                    .setCallbackData(String.valueOf(filterUrl.getId())));
            rowList.add(btn);
        }
        this.callbackKeyboar.setKeyboard(rowList);
    }
}
