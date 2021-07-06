package com.saltlux.khnp.searcher.search.service;

import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.YYYYMMDD;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.IN2TMSOldOwlimSearch;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.dor.api.common.query.IN2RangeQuery;
import com.saltlux.dor.api.common.sort.IN2FieldSort;
import com.saltlux.khnp.searcher.search.indexer.SearchLogIndexing;
import com.saltlux.khnp.searcher.search.model.SearchLog;
import com.saltlux.khnp.searcher.search.repository.CustomDictRepository;
import com.saltlux.khnp.searcher.search.repository.SearchLogRepository;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;
import com.saltlux.khnp.searcher.search.vo.SearchVo;

import lombok.extern.slf4j.Slf4j;;


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
	private SearchLogIndexing indexing;
	
	
	private SearchObject init(SearchObject searcher){
        searcher.setServer(host, port);
        searcher.addIndex(index);
        return searcher;
    }
	
	
	@Scheduled(fixedRate=10*60*1000)
    public  void searchLogIndex() {
    	IN2StdSearcher searcher = new IN2StdSearcher();
    	init(searcher);
    	
    	searcher.setQuery(IN2Query.MatchingAllDocQuery());
    	IN2FieldSort sort = new IN2FieldSort("CREATEDT", false);
    	searcher.setSort(sort);
    	searcher.setReturnPositionCount(0, 1);
    	searcher.addReturnField(new String[]{"LOGID","LOGKEYWORD","CREATEDT","CLIENTIP"});
    	
    	if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());
    	
    	String createDt = "";
    	if(searcher.getDocumentCount() > 0) {
    		createDt = searcher.getValueInDocument(0, "CREATEDT");
    	}else {
    		createDt = "2021-06-05 13:51:09";
    	}

		List<SearchLog>  searchLog = repository.findByLogList(createDt);
		List<SearchVo> voList = new ArrayList<SearchVo>();

		for(int i=0; i<searchLog.size(); i++) {
			SearchVo vo = new SearchVo();
			vo.setLogId(String.valueOf(searchLog.get(i).getLogId()));
			vo.setLogKeyword(searchLog.get(i).getLogKeyword());
			vo.setCreateDt(searchLog.get(i).getCreateDt());
			vo.setClientIp(searchLog.get(i).getClientIp());
			voList.add(vo);
		}
		
		if(voList.size() > 0) {
			indexing.indexing(voList);
		}
	}
    
    public IntegrationSearchResult wordCloud(SearchVo searchVo) throws Exception{
    	IN2TMSOldOwlimSearch searcher = new IN2TMSOldOwlimSearch();
    	init(searcher);
    	if(searchVo.getFirstDt() != null && !"".equals(searchVo.getFirstDt()) 
    			&& searchVo.getLastDt() != null && !"".equals(searchVo.getLastDt())) {
    		String firstDt = searchVo.getFirstDt().replaceAll("-", "");
    		String lastDt = searchVo.getLastDt().replaceAll("-", "");
    		searcher.setQuery(IN2RangeQuery.newStringRange(YYYYMMDD.fieldName, firstDt, lastDt, false, false));
    		
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
        List<Element> edges = root.getChildren("Edge");
        
        List<String>  customDictList = customDictRepository.findByCustomDictList();
        
        Set<String> stop = new HashSet<>(); //불용어관리 포함 제외
        
        
        List<HashMap<String, String>> documents = new ArrayList<>();
    	int cnt = 0;
    	boolean flag = false;
        for (Element node : nodes) {
        	flag = true;
            String id = node.getAttributeValue("id");
            String name = node.getAttributeValue("name");
            String score = node.getAttributeValue("score");

            if(stop.contains(name) || "ROOT".equals(name)){
                continue;
            }
            
            
            for(String mainWord : customDictList){
    			if(name.equals(mainWord)) {
    				flag = false;
    			}
    		} 
            if(flag) {
            	HashMap<String, String> map = new HashMap<>();
                map.put("id", id);
        		map.put("word", name);
        		map.put("weight", score);
        	
        		documents.add(map);
        		cnt++;
            }
        }
        return new IntegrationSearchResult(documents, cnt);

//        for (Element child : edges) {
//            String fromID = child.getAttributeValue("fromID");
//            String toID = child.getAttributeValue("toID");
//
//            if(stop.contains(fromID) || stop.contains(toID))
//                continue;
//            System.out.println(String.format("from[%s] -> to[%s]", fromID, toID));
//        }


    
    }
}

