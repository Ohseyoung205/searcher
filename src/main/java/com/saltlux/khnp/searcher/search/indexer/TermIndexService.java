package com.saltlux.khnp.searcher.search.indexer;

import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.khnp.searcher.search.repository.TermsDictLogRepository;
import com.saltlux.khnp.searcher.search.vo.TermsDictVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

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

    /* TODO [assign : 최지호 주임]
    *  텀사전 Create, Update, Delete 각각에 해당하는 작업이 이루어 졌을때
    *  index에서도 Create, Update, Delete 하는 로직이 만들어져야 합니다.
    * */

    @Scheduled(cron = "0 * * * * *")
    public void termIndexScheduler() {
        termsDictLogRepository.findByCreateDtGreaterThan(lastModified)
                .stream()
                .map(log -> log.getTermsDict())
                .filter(entity -> entity.isRecYn())
                .map(entity -> new TermsDictVo(entity))
                .forEach(this::termIndex);
        lastModified = new Date();
    }

    public void termIndex(TermsDictVo vo) {
        IN2StdIndexer indexer = new IN2StdIndexer();
        indexer.setServer(SERVER_IP, SERVER_PORT);
        indexer.setIndex(INDEX_NAME);
        indexer.addSource("GLONAME", vo.getGlossaryName(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("ENGTAG", vo.getEngTerms(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("KORTAG", vo.getKorTerms(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("ABBR", vo.getAbbreviation(), indexer.SOURCE_TYPE_TEXT);
        indexer.addSource("EXPLAIN", vo.getTermsExplain(), indexer.SOURCE_TYPE_TEXT);

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
}
