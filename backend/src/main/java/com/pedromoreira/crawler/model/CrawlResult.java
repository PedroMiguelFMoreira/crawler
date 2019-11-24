package com.pedromoreira.crawler.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class CrawlResult {

    private String crawlerName;

    @Singular
    private List<SearchEntry> searches;

    @Getter
    @Builder
    public static class SearchEntry {

        private String name;
        private JsonNode result;

    }

}
