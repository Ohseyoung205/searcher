package com.saltlux.khnp.searcher.search.service;

import com.saltlux.dor.api.IN2FacetSearcher;
import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.IN2FacetResult;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.filter.IN2FieldValueFilter;
import com.saltlux.dor.api.common.filter.IN2TermsFilter;
import com.saltlux.dor.api.common.query.IN2BooleanQuery;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2PrefixQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import com.saltlux.khnp.searcher.search.vo.TermSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Streams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<String> columns = Arrays.asList("ABBR", "GLONAME", "KORTAG", "ENGTAG", "EXPLAIN");

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

        if(StringUtils.isNotEmpty(requests.getSource())){
            searcher.setFilter(new IN2TermsFilter("GLONAME", requests.getSource().split(","), IN2StdSearcher.SOURCE_TYPE_TEXT));
        }

//        searcher.addReturnField(columns.toArray(new String[columns.size()]));
        columns.forEach(c -> searcher.addReturnField(c, true));

        searcher.setReturnPositionCount(requests.getOffset(), requests.getLimit());
        if(!searcher.searchDocument())
            throw new RuntimeException(searcher.getLastErrorMessage());


        List<HashMap<String, String>> documents = new ArrayList<>();
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
//            for (SearchRequests.Return field : requests.getReturns())
//                map.put(field.getReturnField(), searcher.getValueInDocument(i, field.getReturnField().toUpperCase()));
            for(String field : columns){
                map.put(field, searcher.getValueInDocument(i, field));
            }
            documents.add(map);
        }
        log.info("query : {} , return count: {}", requests.getQuery(), searcher.getDocumentCount());
        return new IntegrationSearchResult(documents, searcher.getTotalDocumentCount());
    }

    public List<String> sourceGroupBy(){
        final String fieldName = "GLONAME";

        IN2FacetSearcher searcher = new IN2FacetSearcher();
        searcher.setServer(host, port);
        searcher.addIndex(index);
        searcher.setQuery(IN2Query.MatchingAllDocQuery());
        searcher.addSimpleFacet(fieldName, 1000);
        if (!searcher.searchDocument()) {
            throw new RuntimeException(searcher.getLastErrorMessage());
        }

        List<IN2FacetResult.LabelAndCount> root = searcher.getFacetResult(fieldName).getRoot();
        return root.stream()
                .map(labelAndCount -> labelAndCount.label)
                .sorted()
                .collect(Collectors.toList());

    }
}
