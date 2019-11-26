package com.pedromoreira.crawler.controller;

import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@RestController
@RequestMapping("crawl")
public class CrawlController {

    private CrawlService crawlService;

    @Autowired
    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @GetMapping
    public CompletionStage<List<CrawlResult>> getBoardGamePrice(
            @RequestParam String boardGameName
    ) {
        CompletionStage<List<CrawlResult>> crawlResultsFuture = crawlService.crawl(boardGameName);

        return crawlResultsFuture.thenApply(crawlResults -> crawlResults.stream()
                .map(crawlResult -> {
                    List<CrawlResult.Entry> validEntries = crawlResult.getResult()
                            .stream()
                            .filter(entry -> entry.getName().toLowerCase().contains(boardGameName.toLowerCase()))
                            .collect(Collectors.toList());

                    return CrawlResult.builder().crawlerName(crawlResult.getCrawlerName()).result(validEntries).build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

}
