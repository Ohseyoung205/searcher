package com.saltlux.khnp.searcher.search.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class IntegrationSearchResult {
    private List<HashMap<String, String>> documents;

    private long totalCount;

    public IntegrationSearchResult(List<HashMap<String, String>> documents, long totalCount){
        this.documents = documents;
        this.totalCount = totalCount;
    }
}
