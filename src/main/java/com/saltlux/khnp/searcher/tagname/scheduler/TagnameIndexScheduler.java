package com.saltlux.khnp.searcher.tagname.scheduler;

import com.saltlux.dor.api.IN2StdFieldUpdater;
import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.query.IN2TermQuery;
import com.saltlux.khnp.searcher.common.constant.TAGNAME_FIELD;
import com.saltlux.khnp.searcher.tagname.analysis.Cluster;
import com.saltlux.khnp.searcher.tagname.analysis.DBScanCluster;
import com.saltlux.khnp.searcher.tagname.analysis.LevenshteinDistance;
import com.saltlux.khnp.searcher.tagname.model.TagnameEntity;
import com.saltlux.khnp.searcher.tagname.model.TagnameVo;
import com.saltlux.khnp.searcher.tagname.repository.TagnameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TagnameIndexScheduler {

    @Autowired
    TagnameRepository tagnameRepository;

    @Value("${in2.dor.host}")
    private String host;

    @Value("${in2.dor.port1}")
    private int indexerPort;

    @Value("${in2.dor.port}")
    private int searcherPort;

    @Value("${in2.dor.index.tagname}")
    private String indexName;

    @Value("${tagname.cluster.distance.max-distance}")
    private int maxDistance;

    @Value("${tagname.cluster.eps}")
    private double eps;

    @Value("${tagname.cluster.minpts}")
    private int minPts;

    @Scheduled(cron = "${tagname.clustering.schedule.cron:0 10 0 * * *}")
    public void clusterSchedule(){
        List<TagnameVo> list = tagnameRepository
                .findAll()
                .stream()
                .distinct()
                .map(e -> new TagnameVo(e))
                .sorted()
                .collect(Collectors.toList());
        LevenshteinDistance measure = new LevenshteinDistance(maxDistance + 1);
        DBScanCluster<TagnameVo> clusterer = new DBScanCluster(measure, eps, minPts);
        clusterer.cluster(list);
        for(Cluster<TagnameVo> c : clusterer.getClusters()){
            List<TagnameVo> members = c.getMember();
            for (TagnameVo m : members){
                updateCluster(m.setCluster(c.getId()));
            }
        }
    }

    private void updateCluster(TagnameVo vo) {
        IN2StdFieldUpdater updater = new IN2StdFieldUpdater();
        updater.setServer(host, indexerPort);
        updater.setIndex(indexName);
        updater.setKey(TAGNAME_FIELD.TAGID.getFieldName(), vo.getTagid());
        updater.setUpdateData(TAGNAME_FIELD.CLUSTER.getFieldName(), vo.getCluster());
        updater.update();
        log.debug("update cluster id : id={}, cluster={}", vo.getTagid(), vo.getCluster());
    }

    @Scheduled(cron="${tagname.indexing.schedule.cron:0 0 0 * * *}")
    public void tagnameIndexing(){
        for(TagnameEntity e : tagnameRepository.findAll()){
            TagnameVo vo = new TagnameVo(e);
            if(needUpdate(vo)){
                index(vo);
                log.info("{} : add to index [{}]", indexName, vo.toString());
            }
        }
    }

    private boolean needUpdate(TagnameVo vo) {
        IN2StdSearcher searcher = new IN2StdSearcher();
        searcher.setServer(host, searcherPort);
        searcher.addIndex(indexName);
        searcher.setQuery(new IN2TermQuery(TAGNAME_FIELD.TAGID.getFieldName(), vo.getTagid()));
        searcher.addReturnField(TAGNAME_FIELD.getAllFields());
        if(!searcher.searchDocument()){
            log.warn("DOR Searcher error : {}", searcher.getLastErrorMessage());
            return false;
        }

        for (int i = 0; i < searcher.getDocumentCount(); i++) {
            boolean equals = vo.equals(new TagnameVo(i, searcher));
            if(equals)
                return false;
        }
        return true;
    }

    private void index(TagnameVo vo){
        IN2StdIndexer indexer = new IN2StdIndexer();
        indexer.setServer(host, indexerPort);
        indexer.setIndex(indexName);

        addField(TAGNAME_FIELD.TAGID, vo.getTagid(), indexer);
        addField(TAGNAME_FIELD.TAGNAME, vo.getTagname(), indexer);
        addField(TAGNAME_FIELD.DESCRIPTION, vo.getDescription(), indexer);
        addField(TAGNAME_FIELD.PLANT, vo.getPlant(), indexer);
        indexer.addFieldFTR(TAGNAME_FIELD.INTEGRATION.name(), TAGNAME_FIELD.INTEGRATION.getFieldName(), TAGNAME_FIELD.INTEGRATION.getAnalyzer(), TAGNAME_FIELD.INTEGRATION.isIndexed(), TAGNAME_FIELD.INTEGRATION.isStored());

        indexer.addUpdateableField(TAGNAME_FIELD.CLUSTER.getFieldName(), vo.getCluster());
        if(!indexer.addDocument())
            throw new RuntimeException(indexer.getLastErrorMessage());
    }
    
    private void addField(TAGNAME_FIELD field, String value, IN2StdIndexer indexer){
        indexer.addSource(field.name(), value, IN2StdSearcher.SOURCE_TYPE_TEXT);
        indexer.addFieldFTR(field.name(), field.getFieldName(), field.getAnalyzer(), field.isIndexed(), field.isStored());
    }
}
