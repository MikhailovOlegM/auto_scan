package com;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SiteContainer {
    private final Map<Long, String> container = new HashMap<>();

    public void saveSiteUrl(Long chatID, String url) {
        if (url != null && !url.isEmpty()) {
            this.container.put(chatID, url);
        }
    }

    public String getUrl(Long chatID) {
        return this.container.get(chatID);
    }

    public String getNewLink(Long chatID) {
        String result = null;
        this.container.get(chatID);


        return result;
    }

}
