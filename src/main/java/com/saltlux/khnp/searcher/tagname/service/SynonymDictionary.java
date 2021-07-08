package com.saltlux.khnp.searcher.tagname.service;


import com.saltlux.khnp.searcher.deepqa.analyzer.TMSAnalyzer;
import com.saltlux.khnp.searcher.search.model.CustomDict;
import com.saltlux.khnp.searcher.search.model.CustomDictLog;
import com.saltlux.khnp.searcher.search.repository.CustomDictLogRepository;
import com.saltlux.khnp.searcher.search.repository.CustomDictRepository;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Trie;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SynonymDictionary {

    @Autowired
    private TMSAnalyzer analyzer;

    @Autowired
    private CustomDictRepository customDictRepository;

    @Autowired
    private CustomDictLogRepository customDictLogRepository;

    private ConcurrentHashMap<String, CustomDict> synonymMap = new ConcurrentHashMap<>();

    private Trie trie = Trie.builder().build();

    private Date lastModified = new Date();

    @PostConstruct
    public void init(){
        List<CustomDict> entities = customDictRepository.findAll();
        entities.stream()
                .filter(CustomDict::isRecYn)
                .forEach(this::addMap);
        buildTrieDictionary();
    }

    public void buildTrieDictionary(){
        Trie searcher = Trie.builder()
                .ignoreOverlaps()
                .onlyWholeWords()
                .ignoreCase()
                .addKeywords(synonymMap.keySet())
                .build();
        trie = searcher;
    }

    @Scheduled(initialDelay = 0, fixedRate = 60 * 1000)
    public void updateSchedule(){
        if(lastModified == null) return;
        List<CustomDictLog> logs = customDictLogRepository.findByCreateDtGreaterThan(lastModified);
        if(CollectionUtils.isEmpty(logs)) return;
        lastModified = new Date();

        log.info("synonym update {} recoreds", logs.size());
        for(CustomDictLog log : logs){
            CustomDict e = log.getWordEntity();
            if(log.getLogDiv() == CustomDictLog.LOG_DIV.C){
                addMap(e);
            } else if(log.getLogDiv() == CustomDictLog.LOG_DIV.U){
                updateMap(e);
            } else if(log.getLogDiv() == CustomDictLog.LOG_DIV.D){
                removeMap(e);
            }
        }
        buildTrieDictionary();
    }

    public String getSynonymQuery(String query){
        return trie.tokenize(analyzer.getIndexWords(query))
                .stream()
                .map(token -> token.isMatch() ?
                        String.format("(%s)", synonymMap.get(token.getFragment()).toSynonymQuery(analyzer)) :
                        token.getFragment().trim())
                .collect(Collectors.joining(" "));
    }

    public void addMap(CustomDict entity){
        entity.getAllIndexWords(analyzer).stream()
                .filter(StringUtils::isNotEmpty)
                .forEach(w -> synonymMap.put(w, entity));
    }

    public void removeMap(CustomDict entity){
        Long id = entity.getWordId();
        synonymMap.entrySet().removeIf(entry -> entry.getValue().getWordId().equals(id));
    }

    public void updateMap(CustomDict entity){
        removeMap(entity);
        addMap(entity);
    }

}
