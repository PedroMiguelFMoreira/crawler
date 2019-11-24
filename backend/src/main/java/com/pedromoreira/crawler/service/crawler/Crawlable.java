package com.pedromoreira.crawler.service.crawler;

import com.pedromoreira.crawler.model.CrawlResult;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface Crawlable {

    CompletionStage<CrawlResult> crawl(List<String> boardGameName);

}
