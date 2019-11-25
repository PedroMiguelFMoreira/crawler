package com.pedromoreira.crawler.service.crawler;

import com.pedromoreira.crawler.model.CrawlResult;

import java.util.concurrent.CompletionStage;

public interface Crawlable {

    CompletionStage<CrawlResult> crawl(String boardGameName);

}
