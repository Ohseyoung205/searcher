package com.saltlux.khnp.searcher.search.service;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.query.IN2BooleanQuery;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2PrefixQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import com.saltlux.khnp.searcher.search.vo.TermSearchRequest;
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
public class TermSearchService {

    @Value("${in2.dor.weight.title}")
    private Double titleWeight;
    @Value("${in2.dor.host}")
    private String host;
    @Value("${in2.dor.port}")
    private Integer port;
    @Value("${in2.dor.index.terms}")
    private String index;

    private SearchObject init(SearchObject searcher){
        searcher.setServer(host, port);
        searcher.addIndex(index);
        return searcher;
    }
    public IntegrationSearchResult termSearch(TermSearchRequest requests){
        IN2StdSearcher searcher = new IN2StdSearcher();
        init(searcher);
        String query = requests.getQuery();
        if(StringUtils.isNotBlank(query)){
            searcher.setQuery(new IN2ParseQuery("INTEGRATION", query.trim(), IN2StdSearcher.TOKENIZER_KOR_BIGRAM));
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
