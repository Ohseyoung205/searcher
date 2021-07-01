package com.saltlux.khnp.searcher.tagname.service;


import com.saltlux.khnp.searcher.deepqa.analyzer.TMSAnalyzer;
import com.saltlux.khnp.searcher.search.model.CustomDict;
import com.saltlux.khnp.searcher.search.model.CustomDictLog;
import com.saltlux.khnp.searcher.search.repository.CustomDictLogRepository;
import com.saltlux.khnp.searcher.search.repository.CustomDictRepository;
import org.ahocorasick.trie.Trie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SynonymDictionary {

    @Autowired
    private TMSAnalyzer analyzer;

    @Autowired
    private CustomDictRepository customDictRepository;

    @Autowired
    private CustomDictLogRepository customDictLogRepository;

    private ConcurrentHashMap<String, CustomDict> synonymMap = new ConcurrentHashMap<>();

    private Trie trie = Trie.builder().build();

    private Date lastModified;

    @PostConstruct
    public void init(){
        List<CustomDict> entities = customDictRepository.findAll();
        lastModified = new Date();
        for(CustomDict entity : entities){
            if(entity.isRecYn())
                updateDictionary(entity);
        }
        buildTrieDictionary();
    }

    public void updateDictionary(CustomDict entity){
        for(String word : entity.getIndexWords(analyzer)){
            synonymMap.put(word, entity);
        }
    }

    public void buildTrieDictionary(){
        synchronized (trie){
            trie = Trie.builder()
                    .ignoreOverlaps()
                    .onlyWholeWords()
                    .ignoreCase()
                    .addKeywords(synonymMap.keySet())
                    .build();
        }
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void update(){
        if(lastModified == null) return;
        Date now = new Date();
        List<CustomDictLog> logs = customDictLogRepository.findByCreateDtGreaterThan(now);
        lastModified = now;
        if(CollectionUtils.isEmpty(logs)) return;

        logs.stream().sorted()
                .forEach(e -> {
                    synonymMap.entrySet().stream()
                            .filter(entry -> e.equals(entry))
                            .forEach(entry -> synonymMap.remove(entry));
                    if(e.getLogDiv() != CustomDictLog.LOG_DIV.D){
                        updateDictionary(e.getWordEntity());
                    }
                });
        buildTrieDictionary();
    }

    public String getSynonymQuery(String query){
        return trie.tokenize(query).stream()
                .map(token -> token.isMatch() ?
                                String.format("(%s)", synonymMap.get(token.getFragment()).toSynonymQuery(analyzer).trim()) :
                                token.getFragment().trim())
                .collect(Collectors.joining(" OR "));
    }

}
