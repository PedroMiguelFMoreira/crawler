package com.pedromoreira.crawler.service;

import com.pedromoreira.crawler.model.CrawlResult;
import com.pedromoreira.crawler.service.crawler.Crawlable;
import com.pedromoreira.crawler.service.crawler.dracotienda.DracoTiendaCrawler;
import com.pedromoreira.crawler.service.crawler.dungeonmarvels.DungeonMarvelsCrawler;
import com.pedromoreira.crawler.service.crawler.gameplay.GamePlayCrawler;
import com.pedromoreira.crawler.service.crawler.philibert.PhilibertCrawler;
import com.pedromoreira.crawler.util.Futures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Service
public class CrawlService {

    private List<Crawlable> crawlables;

    @Autowired
    public CrawlService(
            DracoTiendaCrawler dracoTiendaCrawler,
            DungeonMarvelsCrawler dungeonMarvelsCrawler,
            GamePlayCrawler gameplayCrawler,
            PhilibertCrawler philibertCrawler
    ) {
        this.crawlables = new ArrayList<>();

        this.crawlables.add(dracoTiendaCrawler);
        //this.crawlables.add(dungeonMarvelsCrawler);
        //this.crawlables.add(gameplayCrawler);
        //this.crawlables.add(philibertCrawler);
    }

    public CompletionStage<List<CrawlResult>> crawl(String boardGameName) {

        List<CompletionStage<CrawlResult>> crawlResultsFuture = crawlables.stream()
                .map(crawlable -> crawlable.crawl(boardGameName))
                .collect(Collectors.toList());

        return Futures.sequence(crawlResultsFuture).thenApply(crawlResults -> crawlResults);
    }

}
