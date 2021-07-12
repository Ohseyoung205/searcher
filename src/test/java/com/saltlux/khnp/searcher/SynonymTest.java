package com.saltlux.khnp.searcher;

import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.query.IN2ParseQuery;

public class SynonymTest {
    public static void main(String[] args) {
        IN2StdIndexer indexer = new IN2StdIndexer();
        indexer.setServer("192.168.219.2", 10200);
        indexer.setIndex("synonym");

        indexer.addSource("LG", "휴대폰", "text");
        indexer.addFieldFTR("TITLE", "LG", "KOR_BIGRAM", true, true);
        indexer.addDocument();

        IN2StdSearcher searcher = new IN2StdSearcher();
        searcher.setServer("192.168.219.2", 10000);
        searcher.addIndex("synonym");
        searcher.setQuery(new IN2ParseQuery("TITLE", "휴대전화", "KOR_BIGRAM"));
        searcher.setReturnPositionCount(0, 10);
        searcher.searchDocument();

        for (int i = 0; i < searcher.getDocumentCount(); i++) {
            String title = searcher.getValueInDocument(i, "TITLE");
            System.out.println("TITLE :: " + title);
        }
        System.exit(0);
    }
}
