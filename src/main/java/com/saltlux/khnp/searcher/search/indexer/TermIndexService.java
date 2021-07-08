package com.saltlux.khnp.searcher.search.indexer;

import com.saltlux.dor.api.IN2StdDeleter;
import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.khnp.searcher.search.model.CustomDictLog;
import com.saltlux.khnp.searcher.search.model.TermsDict;
import com.saltlux.khnp.searcher.search.model.TermsDictLog;
import com.saltlux.khnp.searcher.search.repository.TermsDictLogRepository;
import com.saltlux.khnp.searcher.search.vo.TermsDictVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TermIndexService {

    @Value("${in2.dor.port}")
    private int SERVER_PORT;

    @Value("${in2.dor.host}")
    private String SERVER_IP;

    @Value("${in2.dor.index.terms}")
    private String INDEX_NAME;

    @Autowired
    private TermsDictLogRepository termsDictLogRepository;

    private Date lastModified = new Date();

    @Scheduled(initialDelay = 0, fixedRate = 60 * 1000)
    public void termIndexScheduler() {
        if(lastModified == null) return;
        List<TermsDictLog> logs = termsDictLogRepository.findByCreateDtGreaterThan(lastModified);
        if(CollectionUtils.isEmpty(logs)) return;
        lastModified = new Date();

        for(TermsDictLog log : logs){
            if(log.getEventTermsDiv() == CustomDictLog.LOG_DIV.C){
                add(log.getTermsDict());
            }else if(log.getEventTermsDiv() == CustomDictLog.LOG_DIV.U){
                update(log.getTermsDict());
            }else if(log.getEventTermsDiv() == CustomDictLog.LOG_DIV.D){
                remove(log.getTermsDict());
            }
        }
    }

    public void indexTerms(TermsDictVo vo) {
        IN2StdIndexer indexer = new IN2StdIndexer();
        indexer.setServer(SERVER_IP, SERVER_PORT);
        indexer.setIndex(INDEX_NAME);
        indexer.addSource("ID", vo.getId(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("GLONAME", vo.getGlossaryName(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("ENGTAG", vo.getEngTerms(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("KORTAG", vo.getKorTerms(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("ABBR", vo.getAbbreviation(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("EXPLAIN", vo.getTermsExplain(), indexer.SOURCE_TYPE_TEXT);

        indexer.addFieldFTR("ID", "ID", indexer.TOKENIZER_TERM, true, true);
        indexer.addFieldFTR("GLONAME", "GLONAME", indexer.TOKENIZER_TERM, true, true);
        indexer.addFieldFTR("ENGTAG", "ENGTAG", indexer.TOKENIZER_KOR_BIGRAM, true, true);
        indexer.addFieldFTR("KORTAG", "KORTAG", indexer.TOKENIZER_KOR_BIGRAM, true, true);
        indexer.addFieldFTR("ABBR", "ABBR", indexer.TOKENIZER_KOR_BIGRAM, true, true);
        indexer.addFieldFTR("EXPLAIN", "EXPLAIN", indexer.TOKENIZER_KOR_BIGRAM, true, true);

        indexer.addFieldFTR("INTEGRATION", "GLONAME/ENGTAG/KORTAG/ABBR/EXPLAIN", indexer.TOKENIZER_KOR_BIGRAM, true, true);
        indexer.addFieldTMS("TMS", "KOR", "ENGTAG/KORTAG/ABBR", true, 100);

        if (!indexer.addDocument()) {
            log.error("TERMS IDX FAIL : id=[{}], {}", vo.getId(), indexer.getLastErrorMessage());
        }
    }

    public void removeIndexTerms(TermsDictVo vo){
        IN2StdDeleter deleter = new IN2StdDeleter();
        deleter.setServer(SERVER_IP, SERVER_PORT);
        deleter.setIndex(INDEX_NAME);
        deleter.setQuery(new IN2ParseQuery("ID", vo.getId(), deleter.TOKENIZER_TERM));
        if(!deleter.DeleteDocument()){
            log.error("TERMS DELETE FAIL : id=[{}], {}", vo.getId(), deleter.getLastErrorMessage());
        }
    }

    private void add(TermsDict entity){
        indexTerms(new TermsDictVo(entity));
    }

    private void remove(TermsDict entity){
        removeIndexTerms(new TermsDictVo(entity));
    }

    private void update(TermsDict entity){
        remove(entity);
        add(entity);
    }
}
