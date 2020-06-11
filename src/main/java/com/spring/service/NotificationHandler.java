package com.spring.service;

import com.SiteParser;
import com.spring.model.FilterUrl;
import com.spring.model.User;
import com.spring.model.ViewedHistory;
import com.spring.repository.UserRepository;
import com.spring.repository.ViewedHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class NotificationHandler {

    private static final Logger LOGGER = Logger.getLogger(NotificationHandler.class.getName());

    @Autowired
    private FilterBotTelegramManager telegramManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ViewedHistoryRepository viewedHistoryRepository;

    @Autowired
    private SiteParser siteParser;

    /*
    1.Seconds
    2.Minutes
    3.Hours
    4.Day-of-Month
    5.Month
    6.Day-of-Week
    7.Year (optional field)
     */

    @Scheduled(cron = "0 0/10 * * * ?")
    public void sendNotification() {
        List<User> allUsers = (List<User>) repository.findAll();
        allUsers.parallelStream().forEach(user -> {
            for (FilterUrl filter : user.getUserFilterUrl()) {

                try {
                    String response = siteParser.getNew(filter.getUrl(), user);
                    if (!response.isEmpty()) {
                        telegramManager.sendMsg(String.valueOf(user.getChat_id()), response, null);
                    }

                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }


//            telegramManager.sendMsg(String.valueOf(user.getChat_id()), "Test", null);
        });

    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void clearUserHistory() {
        List<User> allUsers = (List<User>) repository.findAll();

        for (User user : allUsers) {
            Set<ViewedHistory> needToDelete = user.getUserViewedHistory()
                    .stream()
                    .filter(h -> h.getViewedDate().toEpochDay() < LocalDate.now().toEpochDay())
                    .collect(Collectors.toSet());

            if (!needToDelete.isEmpty()) {
                viewedHistoryRepository.deleteAll(needToDelete);
            }
        }

    }

}
