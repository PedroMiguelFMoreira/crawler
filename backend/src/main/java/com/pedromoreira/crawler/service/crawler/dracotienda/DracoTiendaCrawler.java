package com.pedromoreira.crawler.service.crawler.dracotienda;

import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.service.crawler.PrestaShopCrawler;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
public class DracoTiendaCrawler extends PrestaShopCrawler {

    private static String URL = "https://dracotienda.com/busqueda";

    public DracoTiendaCrawler() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        super(URL, DracoTiendaCrawler.getRestTemplate());
    }

    private static RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected CompletionStage<List<CrawlResult.Entry>> crawlBoardGame(String boardGameName) {
        return CompletableFuture.supplyAsync(() -> {
            HttpEntity<String> response = doSearchRequestFor(boardGameName);

            Document document = Jsoup.parse(response.getBody());
            Element productList = document.getElementById("js-product-list").child(0);

            Elements products = productList.getElementsByClass("laberProduct-container");

            List<CrawlResult.Entry> entries = new ArrayList<>();
            for (Element product : products) {
                CrawlResult.Entry.EntryBuilder entryBuilder = CrawlResult.Entry.builder();

                String name = product.getElementsByClass("productName").get(0).text();
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
