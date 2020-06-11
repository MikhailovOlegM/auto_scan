package com;

import com.spring.model.User;
import com.spring.model.ViewedHistory;
import com.spring.repository.ViewedHistoryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SiteParser {

    @Autowired
    private ViewedHistoryRepository viewedRepository;
    private static final Logger LOGGER = Logger.getLogger(SiteParser.class.getSimpleName());

    public List<String> getNew(String filterUrl, User user) throws IOException {
        List<String> result = new ArrayList<>();
        List<ViewedHistory> viewedHistory = new ArrayList<>();

        Document doc = Jsoup.connect(filterUrl)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = doc.select("#rst-page-1");
        if (elements.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Url: " + filterUrl + " User: " + user.getId());
            return new ArrayList<>();
        }
        Element el = elements.get(0);
        for (Node node : el.childNodes()) {
            String nodeId = "#" + node.attr("id");
            if (!nodeId.contains("rst-ocid")) {
                continue;
            }
            StringBuilder sbInfo = new StringBuilder();

            String title = node.childNode(0).childNode(1).childNode(0).childNode(0).outerHtml();
            boolean isToday = el.select(nodeId + " > div.rst-ocb-i-s > strong").text().contains("сегодня");
            if (title.contains("ПРОДАН") || !isToday || user.getUserViewedHistoryId().contains(nodeId)) {
                continue;
            }
            String uri = el.select(nodeId + " > a.rst-ocb-i-a").attr("href");

            Elements info = el.select(nodeId + " > div.rst-ocb-i-d > ul");
            String cost = info.get(0).select("ul > li:nth-child(1) > span").text();
            String city = info.get(0).select("ul > li:nth-child(2) > span").text();
            String year = info.get(0).select("ul > li:nth-child(3) > span").text();
            String status = info.get(0).select("ul > li:nth-child(4) > span").text();
            String engine = info.get(0).select("ul > li:nth-child(5) > span:nth-child(1)").text();
            String kpp = info.get(0).select("ul > li:nth-child(5) > span:nth-child(2)").text();

            sbInfo.append("Цена: ").append(cost).append("\n")
                    .append("Год: ").append(year).append("\n")
                    .append("Двигатель: ").append(engine).append("\n")
                    .append("КПП: ").append(kpp).append("\n")
                    .append("Состояние: ").append(status).append("\n")
                    .append("Город: ").append(city).append("\n")
                    .append("URL: " + "https://rst.ua" + uri)
                    .append("\n").append("\n");

            result.add(sbInfo.toString());
            viewedHistory.add(new ViewedHistory(user, nodeId, LocalDate.now()));
        }
        if (!viewedHistory.isEmpty()) {
            viewedRepository.saveAll(viewedHistory);
        }


        return result;
    }
}
