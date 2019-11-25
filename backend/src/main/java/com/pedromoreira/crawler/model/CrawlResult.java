package com.pedromoreira.crawler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CrawlResult {

    private String crawlerName;
    private List<Entry> result;

    @Getter
    @Builder
    public static class Entry {

        private String name;
        private String price;
        @Builder.Default
        private boolean hasDiscount = false;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String oldPrice;

    }

}
