package com.pedromoreira.crawler.controller;

import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletionStage;

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
            @RequestParam List<String> boardGameNames
    ) {
        return crawlService.crawl(boardGameNames);
    }

}
