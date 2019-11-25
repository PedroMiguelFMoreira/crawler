package com.pedromoreira.crawler.service.crawler.gameplay;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.service.crawler.PrestaShopCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public class GamePlayCrawler extends PrestaShopCrawler {

    private static String URL = "http://www.gameplay.pt/search";

    public GamePlayCrawler(RestTemplate restTemplate) {
        super(URL, restTemplate);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public CompletionStage<List<CrawlResult.Entry>> crawlBoardGame(String boardGameName) {
        return CompletableFuture.supplyAsync(() -> {
            HttpEntity<String> response = doSearchRequestFor(boardGameName);

            Document document = Jsoup.parse(response.getBody());
            Element productList = document.getElementsByClass("product_list").get(0);

            Elements products = productList.getElementsByClass("ajax_block_product");

            List<CrawlResult.Entry> entries = new ArrayList<>();
            for (Element product : products) {
                CrawlResult.Entry.EntryBuilder entryBuilder = CrawlResult.Entry.builder();

                String name = product.getElementsByClass("product-name").text();
                String price = product.getElementsByClass("price product-price").text().split(" ")[0] + "€";

                entryBuilder
                        .name(name)
                        .price(price);

                String oldPrice = product.getElementsByClass("old-price product-price").text();
                if (oldPrice != null && !oldPrice.trim().isEmpty()) {
                    entryBuilder
                            .hasDiscount(true)
                            .oldPrice(oldPrice.split(" ")[0] + "€");
                }

                entries.add(entryBuilder.build());
            }

            return entries;
        });
    }

}
