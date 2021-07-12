package com.saltlux.khnp.searcher;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.query.IN2FormularQuery;

public class QueryTest {
    public static void main(String[] args) {
        IN2StdSearcher searcher = new IN2StdSearcher();
        searcher.setServer("192.168.219.2", 10000);
        searcher.addIndex("KHNP_TAGNAME");
//        IN2FormularQuery q = new IN2FormularQuery("Synonym(integration:cndr integration:cndsr integration:condenser integration:복수기) +plant:2*");
//        IN2FormularQuery q = new IN2FormularQuery("Synonym(integration_MORPH:cndr OR integration_MORPH:cndsr OR integration_MORPH:condenser OR integration_MORPH:복수기)");
//        IN2FormularQuery q = new IN2FormularQuery("INTEGRATION_MORPH:(cndr OR cndsr OR condenser OR 복수기)");
        IN2FormularQuery q = new IN2FormularQuery("INTEGRATION_MORPH:((1차기기냉각수) OR (1차측 기기냉각수) OR (ccw) OR (component cooling water) OR (기기냉각수))");
//        IN2FormularQuery q = new IN2FormularQuery("Synonym(integration_MORPH:cndr OR integration_MORPH:cndsr OR integration_MORPH:condenser OR integration_MORPH:복수기) +plant:21*");

        searcher.setQuery(q);
        searcher.setReturnPositionCount(0, 10);
        searcher.addReturnField(new String[]{"_id"});
        searcher.searchDocument();
        System.out.println(searcher.getTotalDocumentCount());
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
            String id = searcher.getValueInDocument(i, "_id");
            System.out.println(id);
        }
        System.exit(0);

    }
}
