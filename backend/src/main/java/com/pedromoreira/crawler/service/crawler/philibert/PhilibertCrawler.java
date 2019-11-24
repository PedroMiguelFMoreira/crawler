package com.pedromoreira.crawler.service.crawler.philibert;

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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public class PhilibertCrawler extends PrestaShopCrawler {

    private static String URL = "https://www.philibertnet.com/en/search";

    public PhilibertCrawler(RestTemplate restTemplate) {
        super(URL, restTemplate);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public CompletionStage<CrawlResult.SearchEntry> crawlBoardGame(String boardGameName) {
        return CompletableFuture.supplyAsync(() -> {
            HttpEntity<String> response = doSearchRequestFor(boardGameName);

            Document document = Jsoup.parse(response.getBody());
            Element productList = document.getElementsByClass("product_list").get(0);

            Elements products = productList.getElementsByClass("ajax_block_product");

            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

            for (Element product : products) {
                ObjectNode objectNode = arrayNode.addObject();

                objectNode.put("name", product.getElementsByClass("s_title_block").get(0).child(0).text());

                String price = product.getElementsByClass("price").text().split(" ")[0] + "€";
                objectNode.put("price", price);
                objectNode.put("has-discount", false);

                String oldPrice = product.getElementsByClass("old-price product-price").text();
                if (oldPrice != null && !oldPrice.trim().isEmpty()) {
                    objectNode.put("has-discount", true);
                    objectNode.put("old-price", oldPrice.split(" ")[0] + "€");
                }

            }

            return CrawlResult.SearchEntry.builder()
                    .name(boardGameName)
                    .result(arrayNode)
                    .build();
        });
    }

}
