package com.pedromoreira.crawler.service.crawler;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.util.Futures;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor
public abstract class PrestaShopCrawler implements Crawlable {

    private String url;
    protected RestTemplate restTemplate;

    @Override
    public CompletionStage<CrawlResult> crawl(List<String> boardGameNames) {
        CrawlResult.CrawlResultBuilder crawlResultBuilder = CrawlResult.builder()
                .crawlerName(this.getClass().getSimpleName());

        List<CompletionStage<CrawlResult.SearchEntry>> searchEntryFutures = new ArrayList<>();
        for (String boardGameName : boardGameNames) {
            CompletionStage<CrawlResult.SearchEntry> searchEntryFuture = crawlBoardGame(boardGameName)
                    .exceptionally(throwable -> CrawlResult.SearchEntry.builder()
                            .name(boardGameName)
                            .result(JsonNodeFactory.instance.arrayNode())
                            .build()
                    );

            searchEntryFutures.add(searchEntryFuture);
        }

        return Futures.sequence(searchEntryFutures).thenApply(result -> crawlResultBuilder.searches(result).build());
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

    protected abstract CompletionStage<CrawlResult.SearchEntry> crawlBoardGame(String boardGameName);

}
