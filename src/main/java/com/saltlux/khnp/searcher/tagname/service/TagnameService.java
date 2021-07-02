package com.saltlux.khnp.searcher.tagname.service;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.query.*;
import com.saltlux.khnp.searcher.common.constant.TagnameField;
import com.saltlux.khnp.searcher.search.vo.PageResultVo;
import com.saltlux.khnp.searcher.tagname.model.ClusteredTagnameVo;
import com.saltlux.khnp.searcher.tagname.model.TagnameVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TagnameService {

    @Autowired
    SynonymDictionary synonymDictionary;

    @Value("${in2.dor.host}")
    private String host;

    @Value("${in2.dor.port}")
    private int searcherPort;

    @Value("${in2.dor.index.tagname}")
    private String indexName;

    public PageResultVo searchByTalkbot(String plant, String query) {
        if(StringUtils.isEmpty(plant) || StringUtils.isEmpty(query))
            return new PageResultVo(new ArrayList(), 0);

        IN2StdSearcher searcher = new IN2StdSearcher();
        searcher.setServer(host, searcherPort);
        searcher.addIndex(indexName);

        IN2BooleanQuery bQuery = new IN2BooleanQuery();
        if(StringUtils.isNotEmpty(plant)){
            bQuery.add(new IN2PrefixQuery(TagnameField.PLANT.name(), plant), IN2Query.AND);
        }

        IN2BooleanQuery subQuery = new IN2BooleanQuery();
        String korFieldName = TagnameField.DESCRIPTION.name() + "_MORPH";
        String korFieldValue = synonymDictionary.getSynonymQuery(query);
        subQuery.add(new IN2ParseQuery(korFieldName, korFieldValue, IN2StdSearcher.TOKENIZER_KOR), IN2Query.OR);

        String bigramFieldName = TagnameField.INTEGRATION.name() + "_BIGRAM";
        subQuery.add(new IN2ParseQuery(bigramFieldName, query, IN2StdSearcher.TOKENIZER_BIGRAM), IN2Query.OR);
        bQuery.add(subQuery, IN2Query.AND);


        searcher.setQuery(bQuery);
        searcher.addReturnField(TagnameField.getAllFields());
        searcher.setReturnPositionCount(0, 100);
        if(!searcher.searchDocument())
            throw new RuntimeException(searcher.getLastErrorMessage());

        LinkedHashMap<Integer, List<TagnameVo>> map = new LinkedHashMap<>();
        for (int i = 0; i < searcher.getDocumentCount(); i++){
            TagnameVo vo = new TagnameVo(i, searcher);

            int clusterId = vo.getCluster() < 0 ? i * vo.getCluster() : vo.getCluster();
            List<TagnameVo> list = map.getOrDefault(clusterId, new ArrayList<>());
            list.add(vo);
            map.put(clusterId, list);
        }

        List<ClusteredTagnameVo> result = new ArrayList();
        for(Map.Entry<Integer, List<TagnameVo>> e : map.entrySet()){
            HashMap<String, String> m = new HashMap<>();
            if(e.getKey() <= 0){
                TagnameVo tag = e.getValue().get(0);
                result.add(new ClusteredTagnameVo(tag.getTagname(), tag.getDescription(), "cluster_0"));
                continue;
            }

            String tagnames = e.getValue().stream().map(t -> t.getTagname()).collect(Collectors.joining(","));
            List<String> descriptions = e.getValue().stream().map(t -> t.getDescription()).collect(Collectors.toList());
            result.add(new ClusteredTagnameVo(tagnames, mergeSentence(descriptions), String.format("cluster_%d", e.getKey())));
        }
        return new PageResultVo(result, result.size());
    }

    public PageResultVo search(String plant, String query, int offset, int limit) {
        IN2StdSearcher searcher = new IN2StdSearcher();
        searcher.setServer(host, searcherPort);
        searcher.addIndex(indexName);

        IN2BooleanQuery bQuery = new IN2BooleanQuery();
        if(StringUtils.isEmpty(query)){
            bQuery.add(IN2Query.MatchingAllDocQuery(), IN2Query.AND);
        }else{
            IN2BooleanQuery subQuery = new IN2BooleanQuery();
            String korFieldName = TagnameField.INTEGRATION.name() + "_MORPH";
            String korFieldValue = synonymDictionary.getSynonymQuery(query);
            subQuery.add(new IN2ParseQuery(korFieldName, korFieldValue, IN2StdSearcher.TOKENIZER_KOR), IN2Query.OR);

            String bigramFieldName = TagnameField.INTEGRATION.name() + "_BIGRAM";
            subQuery.add(new IN2ParseQuery(bigramFieldName, query, IN2StdSearcher.TOKENIZER_BIGRAM), IN2Query.OR);
            bQuery.add(subQuery, IN2Query.AND);
        }

        if(StringUtils.isNotEmpty(plant)){
            bQuery.add(new IN2PrefixQuery(TagnameField.PLANT.name(), plant), IN2Query.AND);
        }

        searcher.setQuery(bQuery);
        searcher.addReturnField(TagnameField.getAllFields());
        searcher.setReturnPositionCount(offset, limit);
        if(!searcher.searchDocument())
            throw new RuntimeException(searcher.getLastErrorMessage());

        List<TagnameVo> list = new ArrayList();
        for (int i = 0; i < searcher.getDocumentCount(); i++)
            list.add(new TagnameVo(i, searcher));
        return new PageResultVo(list, searcher.getTotalDocumentCount());
    }

    private String mergeSentence(List<String> sentences){
        final String regex = "[-\\s]";
        final HashMap<String, Integer> wordMap = new HashMap<>();

        final String rep = sentences.get(0);
        sentences.stream()
                .flatMap(s -> Stream.of(s.split(regex)))
                .forEach(w -> wordMap.put(w, wordMap.getOrDefault(w, 0) + 1));
        final double mean = wordMap.keySet().stream()
                .mapToDouble(w -> wordMap.get(w))
                .average().orElse(1.0);
        final String abs = Stream.of(rep.split(regex))
                .filter(w -> StringUtils.isNotEmpty(w))
                .filter(w -> wordMap.get(w) > 1)
                .collect(Collectors.joining(" "));
        return StringUtils.isNotEmpty(abs) && mean > 1 ? abs : rep;
    }
}
