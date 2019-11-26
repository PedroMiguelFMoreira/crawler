package com.pedromoreira.crawler.service.crawler;

import com.pedromoreira.crawler.model.CrawlResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor
public abstract class PrestaShopCrawler implements Crawlable {

    private String url;
    protected RestTemplate restTemplate;

    @Override
    public CompletionStage<CrawlResult> crawl(String boardGameName) {
        CrawlResult.CrawlResultBuilder crawlResultBuilder = CrawlResult.builder()
                .crawlerName(this.getClass().getSimpleName());


        return crawlBoardGame(boardGameName).thenApply(entries -> crawlResultBuilder.result(entries).build())
                .exceptionally(throwable -> crawlResultBuilder.result(Collections.emptyList()).build());
    }

    protected HttpEntity<String> doSearchRequestFor(String boardGameName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");
        headers.set("Accept", MediaType.TEXT_HTML_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("controller", "search")
                .queryParam("orderby", "position")
                .queryParam("search_query", boardGameName)
                .queryParam("submit_search", "");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    protected abstract CompletionStage<List<CrawlResult.Entry>> crawlBoardGame(String boardGameName);

}
