package com.saltlux.khnp.searcher.search.service;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.IN2TMSOldOwlimSearch;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.dor.api.common.query.IN2RangeQuery;
import com.saltlux.dor.api.common.sort.IN2FieldSort;
import com.saltlux.khnp.searcher.search.indexer.SearchLogIndexService;
import com.saltlux.khnp.searcher.search.repository.CustomDictRepository;
import com.saltlux.khnp.searcher.search.repository.SearchLogRepository;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;
import com.saltlux.khnp.searcher.search.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.YYYYMMDD;

@Slf4j
@Service
public class SearchLogService {
	
	@Value("${in2.dor.host}")
	private String host;
	@Value("${in2.dor.port}")
	private Integer port;
	@Value("${in2.dor.index.searchlog}")
	private String index;

	@Autowired
	private SearchLogRepository repository;
	
	@Autowired
	private CustomDictRepository customDictRepository;
	
	@Autowired
	private SearchLogIndexService searchLogIndexService;

	private Set<String> stopSet = new HashSet<>(); //불용어관리 포함 제외
	
	
	private SearchObject init(SearchObject searcher){
        searcher.setServer(host, port);
        searcher.addIndex(index);
        return searcher;
    }

	@Scheduled(initialDelay = 0, fixedRate = 60 * 1000)
	public void loadStopDictionary(){
		stopSet = customDictRepository.findByCustomDictList()
				.stream()
				.collect(Collectors.toSet());
	}
	
	@Scheduled(fixedRate= 10 * 60 * 1000)
	public void searchLogIndex() {
		final String createDtField = "CREATEDT";

    	IN2StdSearcher searcher = new IN2StdSearcher();
    	init(searcher);
    	searcher.setQuery(IN2Query.MatchingAllDocQuery());
		IN2FieldSort sort = new IN2FieldSort(createDtField, false);
    	searcher.setSort(sort);
    	searcher.setReturnPositionCount(0, 1);
    	searcher.addReturnField(new String[]{createDtField});
    	
    	if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());
    	
    	String createDt = "2021-06-05 13:51:09";
    	if(searcher.getDocumentCount() > 0) {
    		createDt = searcher.getValueInDocument(0, createDtField);
    	}

		repository.findByLogList(createDt)
				.stream()
				.map(SearchVo::new)
				.forEach(vo -> searchLogIndexService.indexing(vo));
	}
    
    public IntegrationSearchResult wordCloud(SearchVo searchVo) throws Exception {
    	IN2TMSOldOwlimSearch searcher = new IN2TMSOldOwlimSearch();
    	init(searcher);
    	if(StringUtils.isNotEmpty(searchVo.getFirstDt()) && StringUtils.isNotEmpty(searchVo.getLastDt())) {
    		String firstDt = searchVo.getFirstDt().replace("-", "");
    		String lastDt = searchVo.getLastDt().replace("-", "");
    		searcher.setQuery(IN2RangeQuery.newStringRange(YYYYMMDD.fieldName, firstDt, lastDt, true, false));
    	}else {
    		searcher.setQuery(IN2Query.MatchingAllDocQuery());
    	}
    	
        searcher.setSearchCount(10000);
        searcher.setDepth(1);
        searcher.setLanguage("KOR");
        searcher.setContentField("TMS_RAW_STREAM");
        searcher.setKeyField("");

        searcher.setMainToggle("ROOT");
        if (!searcher.analyzeDocument()) throw new RuntimeException(searcher.getLastErrorMessage());
        
        String xmlresult = searcher.getTopicRank();
        InputSource xmldoc = new InputSource(new StringReader(xmlresult));
        SAXBuilder builder = new SAXBuilder();

        Document doc = builder.build(xmldoc);

        Element root = doc.getRootElement();
        List<Element> nodes = root.getChildren("Node");

        List<HashMap<String, String>> documents = new ArrayList<>();
        for (Element node : nodes) {
            String id = node.getAttributeValue("id");
            String name = node.getAttributeValue("name");
            String score = node.getAttributeValue("score");

            if(stopSet.contains(name) || "ROOT".equals(name)){
                continue;
            }

			HashMap<String, String> map = new HashMap<>();
			map.put("id", id);
			map.put("word", name);
			map.put("weight", score);
			documents.add(map);
        }
        return new IntegrationSearchResult(documents, documents.size());
    }
}

