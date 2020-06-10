package com;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import java.io.IOException;

public class SiteParser {

    public static String getNew(String filterUrl) throws IOException {
        StringBuilder sbInfo = new StringBuilder();

        Document doc = Jsoup.connect(filterUrl)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = doc.select("#rst-page-1");
        Element el = elements.get(0);
        for (Node node : el.childNodes()) {
            String nodeId = "#" + node.attr("id");
            String title = node.childNode(0).childNode(1).childNode(0).childNode(0).outerHtml();
            boolean isToday = el.select(nodeId + " > div.rst-ocb-i-s > strong").text().contains("сегодня");
            if (title.contains("ПРОДАН") /*|| !isToday*/) {
                continue;
            }
            String uri = el.select(nodeId+" > a.rst-ocb-i-a").attr("href");
            System.out.println(uri);
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
            .append("URL: " + "https://rst.ua"+uri);

            break;
        }


        return sbInfo.toString();
    }
}
