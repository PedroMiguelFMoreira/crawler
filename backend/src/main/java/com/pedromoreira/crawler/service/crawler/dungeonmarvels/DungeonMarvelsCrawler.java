package com.pedromoreira.crawler.service.crawler.dungeonmarvels;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.service.crawler.Crawlable;
import com.pedromoreira.crawler.service.crawler.PrestaShopCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public class DungeonMarvelsCrawler extends PrestaShopCrawler {

    private static String URL = "https://dungeonmarvels.com/buscar";

    public DungeonMarvelsCrawler(RestTemplate restTemplate) {
        super(URL, restTemplate);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected CompletionStage<List<CrawlResult.Entry>> crawlBoardGame(String boardGameName) {
        return CompletableFuture.supplyAsync(() -> {
            HttpEntity<String> response = doSearchRequestFor(boardGameName);

            Document document = Jsoup.parse(response.getBody());
            Element productList = document.getElementById("js-product-list").child(0);

            Elements products = productList.getElementsByClass("product-container");

            List<CrawlResult.Entry> entries = new ArrayList<>();
            for (Element product : products) {
                CrawlResult.Entry.EntryBuilder entryBuilder = CrawlResult.Entry.builder();

                String name = product.getElementsByClass("product-title").get(0).text();
                String price = product.getElementsByClass("price").text();

                entryBuilder
                        .name(name)
                        .price(price);

                String oldPrice = product.getElementsByClass("regular-price").text();
                if (oldPrice != null && !oldPrice.trim().isEmpty()) {
                    entryBuilder
                            .hasDiscount(true)
                            .oldPrice(oldPrice);
                }

                entries.add(entryBuilder.build());
            }

            return entries;
        });
    }
}
