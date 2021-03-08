package com.saltlux.khnp.searcher.search.service;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.query.IN2BooleanQuery;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2PrefixQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.saltlux.khnp.searcher.common.config.INDEX_FIELD.*;

@Slf4j
@Service
public class SearchService {

    @Value("${in2.dor.weight.title}")
    private Double titleWeight;
    @Value("${in2.dor.host}")
    private String host;
    @Value("${in2.dor.port}")
    private Integer port;
    @Value("${in2.dor.index.name}")
    private String index;

    private SearchObject init(SearchObject searcher){
        searcher.setServer(host, port);
        searcher.addIndex(index);
        return searcher;
    }
    public IntegrationSearchResult integrationSearch(SearchRequests requests){
        IN2StdSearcher searcher = new IN2StdSearcher();
        init(searcher);
        String query = requests.getQuery();
        if(StringUtils.isNotBlank(query)){
            IN2BooleanQuery bQuery = new IN2BooleanQuery();
            bQuery.add(new IN2PrefixQuery(NUMBER.fieldName, query), IN2Query.OR);
            bQuery.add(new IN2ParseQuery(TITLE.fieldName, String.format("(%s)^%d", query, titleWeight), TITLE.analyzer), IN2Query.OR);
            bQuery.add(new IN2ParseQuery(CONTENTS.fieldName, query, CONTENTS.analyzer), IN2Query.OR);
            searcher.setQuery(bQuery);
        }else{
            searcher.setQuery(IN2Query.MatchingAllDocQuery());
        }

        if(!CollectionUtils.isEmpty(requests.getReturns())){
            requests.getReturns().stream()
                    .forEach(r -> searcher.addReturnField(r.getReturnField().toUpperCase(), r.isHilight(), r.getReturnLength()));
        }
        searcher.setReturnPositionCount(requests.getOffset(), requests.getLimit());
        if(!searcher.searchDocument())
            throw new RuntimeException(searcher.getLastErrorMessage());


        List<HashMap<String, String>> documents = new ArrayList<>();
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            for (SearchRequests.Return field : requests.getReturns())
                map.put(field.getReturnField(), searcher.getValueInDocument(i, field.getReturnField().toUpperCase()));
            documents.add(map);
        }
        log.info("query : {} , return count: {}", requests.getQuery(), searcher.getDocumentCount());
        return new IntegrationSearchResult(documents, searcher.getTotalDocumentCount());
    }
}
