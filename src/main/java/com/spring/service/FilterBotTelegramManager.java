package com.spring.service;

import com.SiteContainer;
import com.spring.model.FilterUrl;
import com.spring.model.User;
import com.spring.repository.FilterRespository;
import com.spring.repository.UserRepository;
import com.spring.service.Constant.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
public class FilterBotTelegramManager {

    @Autowired
    private SiteContainer container;
    @Autowired
    private FilterRespository filterRep;
    @Autowired
    private UserRepository userRep;
    private final Stack<String> operation = new Stack<>();

    private static final Logger LOGGER = Logger.getLogger(TelegramBot.class.getName());
    private static final String REGULAR = "(\\w+:\\/\\/)(rst.ua|m.rst.ua)\\/[-a-zA-Z0-9:@;?&=\\/%\\+\\.\\*!'\\(\\),\\$_\\{\\}\\^~\\[\\]`#|]+";

    private TelegramBot bot;
    private Map<Long, Boolean> isAddFilterMap = new HashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.log(Level.INFO, "Telegram bot start");

        ApiContextInitializer.init(); // Инициализируем апи
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            this.bot = new TelegramBot(this);
            botapi.registerBot(this.bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "Telegram bot finish");
    }


    public void managedMsg(Message msg) {
        Long chatId = msg.getChatId();
        String text = msg.getText();
        User user = getUser(msg, chatId);

        //начало работы
        if (text.equals("/start")) {
            sendMsg(String.valueOf(chatId), "Приветствую. Выбери одно из действий.", bot.replyKeyboardMarkup);
            return;
        }

        //добавление фильтров
        if (!text.equals(Command.ADD_FILTER) && !text.equals(Command.ALL_FILTER) && !text.equals(Command.DELETE_FILTER)
                && this.isAddFilterMap.containsKey(chatId) && this.isAddFilterMap.get(chatId)) {
            this.isAddFilterMap.put(chatId, false);
            boolean success = this.saveFilter(chatId, text, user);
            if (success) {
                sendMsg(String.valueOf(chatId), "Список фильтров обновлён.", null);
            }
            return;
        }

        //обработка кнопок
        switch (text) {
            case Command.ADD_FILTER: {
                this.isAddFilterMap.put(chatId, true);
                sendMsg(String.valueOf(chatId), "Отправьте данные в формате- **краткое_название:url**. \n " +
                        "Например: Одесса, Ауди:http://rst.ua/region", null);
                break;
            }
            case Command.ALL_FILTER: {
                this.isAddFilterMap.put(chatId, false);
                List<FilterUrl> filterUrls = filterRep.getFilterByUserId(user.getId());
                StringBuilder sb = new StringBuilder();
                int number = 1;
                for (FilterUrl v : filterUrls) {
                    sb.append(number++).append(". ")
                            .append(v.getTitle())
                            .append("-")
                            .append(v.getUrl())
                            .append("\n");
                }

                sendMsg(String.valueOf(chatId), sb.toString(), null);
                break;
            }
            case Command.DELETE_FILTER: {
                this.isAddFilterMap.put(chatId, false);
                List<FilterUrl> filterUrls = filterRep.getFilterByUserId(user.getId());
                this.bot.addCallbackButton(filterUrls);
                sendMsg(String.valueOf(chatId), "Выберите фильтр:", this.bot.callbackKeyboar);
                break;
            }
            default: {
                sendMsg(String.valueOf(chatId), "Выбери одно из действий.", this.bot.replyKeyboardMarkup);
            }

        }


    }


    /**
     * Добавить фильтр
     * Удадлить фильтр
     * Показать все фильтра
     */
    public synchronized void sendMsg(String chatId, String s, ReplyKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        if (s == null || s.isEmpty()) {
            s = "Фильтры не добавлены";
        }
        sendMessage.setText(s);


        if (keyboard != null) {
            sendMessage.setReplyMarkup(keyboard);
        }

        try {
            this.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * NotificationHandler
     */
    public synchronized void sendMsg(String chatId, List<String> arrayMsg) {
        for (String msg : arrayMsg) {

            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(chatId);
            sendMessage.enableHtml(true);
            sendMessage.setText(msg);

            try {
                this.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }


    public void managedCallbackMsg(CallbackQuery msg) {
        SendMessage sendMessage = new SendMessage();
        String id = msg.getData();
        try {
            filterRep.deleteById(Integer.valueOf(id));
        } catch (EmptyResultDataAccessException ex) {
            return;
        }
        sendMessage.setText("Фильрт удалён.")
                .setChatId(msg.getMessage().getChatId());

        try {

            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    private boolean saveFilter(Long chatId, String text, User user) {

        String[] data = text.split(":", 2);

        if (data.length != 2) {
            LOGGER.log(Level.WARNING, "Not valid format: " + text + "\n chat_id: " + chatId);
            sendMsg(String.valueOf(chatId), "Проверьте правильность введённых параметров или формат ввода согласно примеру.", null);
            return false;
        }

        String title = data[0].trim();
        String url = data[1].trim();

        if (!Pattern.matches(REGULAR, url)) {
            LOGGER.log(Level.WARNING, "Unvalid url for filter: " + text + " from user: " + chatId);
            sendMsg(String.valueOf(chatId), "Неверно указана ссылка. Повторите ввод.", null);
            return false;
        }

        FilterUrl filter = filterRep.getUserFilterById(url, user.getId());
        if (filter == null) {
            filter = new FilterUrl();
            filter.setUrl(url);
            filter.setTitle(title);
            filter.setUserFilter(user);
        }
        filterRep.save(filter);

        return true;
    }

    private User getUser(Message msg, Long chatId) {
        User user = userRep.getUserByChatId(chatId);
        if (user == null) {
            user = new User();
            String name = msg.getFrom().getUserName();
            String firstName = msg.getFrom().getFirstName();
            user.setUser_name(name == null ? firstName : name);
            user.setFirst_name(firstName);
            user.setChatIt(chatId);
            userRep.save(user);
        }
        return user;
    }
}
