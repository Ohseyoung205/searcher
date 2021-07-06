package com.saltlux.khnp.searcher.search.service;

import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.CONTENTS;
import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.NUMBER;
import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.TITLE;
import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.INDEXGB;
import static com.saltlux.khnp.searcher.common.constant.INDEX_FIELD.DOMAIN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.filter.IN2TermsFilter;
import com.saltlux.dor.api.common.query.IN2BooleanQuery;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2PrefixQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.dor.api.common.sort.IN2FieldSort;
import com.saltlux.dor.api.common.sort.IN2MultiSort;
import com.saltlux.khnp.searcher.search.model.DomainTable;
import com.saltlux.khnp.searcher.search.repository.DomainRepository;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchService {

    @Value("${in2.dor.weight.title}")
    private Double titleWeight;
    @Value("${in2.dor.host}")
    private String host;
    @Value("${in2.dor.port}")
    private Integer port;
    
    @Autowired
	private DomainRepository repository;

    private SearchObject init(SearchObject searcher, String indexName){
        searcher.setServer(host, port);
        searcher.addIndex(indexName);
        return searcher;
    }
    
    
    public IntegrationSearchResult integrationSearch(SearchRequests requests){
    	List<DomainTable>  doaminList = null;
    	
        IN2StdSearcher searcher = new IN2StdSearcher();
        if("".equals(requests.getFilter().get(0).getFilterTerm())) {
        	searcher.setServer(host, port);
    		doaminList = repository.findAll();
    		for(int i=0;i<doaminList.size(); i++) {
    			searcher.addIndex(doaminList.get(i).getIndexName());
    		}
    	}else {
    		doaminList = repository.findByDomainList(requests.getFilter().get(0).getFilterTerm());
    		String indexName = doaminList.get(0).getIndexName();
    		init(searcher, indexName);
    	}

        String query = requests.getQuery();
     
        if(StringUtils.isNotBlank(query)){									//검색할 문자가 존재할 경우
            IN2BooleanQuery bQuery = new IN2BooleanQuery();
            for(String strFld : requests.getField()) {						//검색필드 매핑
            	if("number".equals(strFld)) {
            		bQuery.add(new IN2PrefixQuery(NUMBER.fieldName, query), IN2Query.OR);
            	}else if("title".equals(strFld)) {
            		bQuery.add(new IN2ParseQuery(TITLE.fieldName, String.format("(%s)^%f", query, titleWeight), TITLE.analyzer), IN2Query.OR);
            	}else if("contents".equals(strFld)) {
            		bQuery.add(new IN2ParseQuery(CONTENTS.fieldName, query, CONTENTS.analyzer), IN2Query.OR);
            	}
            }
            searcher.setQuery(bQuery);
        }else{
            searcher.setQuery(IN2Query.MatchingAllDocQuery());
        }
        
//        if(!"".equals(requests.getFilter().get(0).getFilterTerm())) {
//        	searcher.setFilter(new IN2TermsFilter(DOMAIN.fieldName, requests.getFilter().get(0).getFilterTerm().split(";"), IN2StdSearcher.SOURCE_TYPE_TEXT));
//        }
        
        if(!CollectionUtils.isEmpty(requests.getReturns())){				//넘겨줄 값 매핑
            requests.getReturns().stream()
                    .forEach(r -> searcher.addReturnField(r.getReturnField().toUpperCase(), r.isHilight(), r.getReturnLength()));
        }
        
        if(!CollectionUtils.isEmpty(requests.getSort())){					//정렬을 설정한다.
        	IN2MultiSort multiSort = new IN2MultiSort();
        	requests.getSort().stream()
                    .forEach(r -> multiSort.add(new IN2FieldSort(r.getSortField().toUpperCase(), r.isAsc())));
            
        	searcher.setSort(multiSort);    	
        }
        
        
        if(requests.getLimit()== 0){
        	requests.setLimit(10);
        }        
        searcher.setReturnPositionCount(requests.getOffset(), requests.getLimit());
        
        if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());
        
        List<HashMap<String, String>> documents = new ArrayList<>();
        String strSplit = "";
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            for (SearchRequests.Return field : requests.getReturns()) {
            	if("number".equals(field.getReturnField())) {
            		strSplit = searcher.getValueInDocument(i, "FILENM").replaceAll("htm", "");
            		if("4.".equals(strSplit) || "5.".equals(strSplit) || "6.".equals(strSplit)) {
            			strSplit = "";
            		}
            		if("".equals(searcher.getValueInDocument(i, field.getReturnField().toUpperCase()))) {
            			map.put("rnumber", searcher.getValueInDocument(i, field.getReturnField().toUpperCase()).replaceAll("B 2", "20").replaceAll("B 3", "30"));
            			map.put(field.getReturnField(), strSplit+"0");
            		}else {
            			map.put("rnumber", searcher.getValueInDocument(i, field.getReturnField().toUpperCase()).replaceAll("B 2", "20").replaceAll("B 3", "30"));
            			map.put(field.getReturnField(), strSplit+searcher.getValueInDocument(i, field.getReturnField().toUpperCase()).replaceAll("B 2", "20").replaceAll("B 3", "30"));
            		}
            	}else {
            		map.put(field.getReturnField(), searcher.getValueInDocument(i, field.getReturnField().toUpperCase()));
            	}
            	
            }
            documents.add(map);
        }
        log.info("query : {} , return count: {}", requests.getQuery(), searcher.getDocumentCount());
        return new IntegrationSearchResult(documents, searcher.getTotalDocumentCount());
    }
    
    /**
     * 
     * @param plant : 원전 호기선택 ex)고리,월성
     * @param query : 운전 기술지침서의 목차 번호. 비어있을 경우 최상위 (root)로 인식한다.
     * @param inferred : 대상의 하위만 출력할 경우 false, 대상 하위의 전체를 출력할 경우 true로 입력. 톡봇에서 운전기술지침서의 해당 depth 하위의 결과를 출력하기 위해 사용.
     * @return
     */
    public IntegrationSearchResult hierarchySearch(String plant, String query, boolean inferred ){
    	List<DomainTable>  doaminList = repository.findByDomainList(plant);
    	String indexName = doaminList.get(0).getIndexName();

    	IN2StdSearcher searcher = new IN2StdSearcher();
    	init(searcher, indexName);
    	
    	if(StringUtils.isNotBlank(query)){
    		IN2BooleanQuery bQuery = new IN2BooleanQuery();
    		bQuery.add(new IN2PrefixQuery(NUMBER.fieldName, query), IN2Query.OR);
    		bQuery.add(new IN2ParseQuery(INDEXGB.fieldName, "index", CONTENTS.analyzer), IN2Query.AND);
    		searcher.setQuery(bQuery);
    	}else {
    		IN2BooleanQuery bQuery = new IN2BooleanQuery();
    		bQuery.add(new IN2ParseQuery(INDEXGB.fieldName, "index", CONTENTS.analyzer), IN2Query.AND);
    		
    		searcher.setQuery(bQuery);
    	}
    	
    	if(!"".equals(plant)) {
			searcher.setFilter(new IN2TermsFilter(DOMAIN.fieldName, plant.split(";"), IN2StdSearcher.SOURCE_TYPE_TEXT));
		}
    	

    	searcher.setSort(new IN2FieldSort("ORDERNUM", true, "long"));    	
        searcher.setReturnPositionCount(0, 10000);
        
        searcher.addReturnField(new String[]{"TITLE0","TITLE1","TITLE2","TITLE3", "NUMBER0", "NUMBER1","NUMBER2","NUMBER3","POSITION","FILENM","LEVEL", "ORDERNUM"});
        
        if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());

        
    	List<HashMap<String, String>> documents = new ArrayList<>();
    	String tmpStr1 = "";
    	String tmpStr2 = "";
    	String tmpStr3 = "";
    	String tmpStr4 = "";
    	
    	int cnt = 0;
    	int gb1 = 0;
    	int gb2 = 0;
    	int gb3 = 0;
    	int gb4 = 0;
    	int gb5 = 0;
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
        	HashMap<String, String> map = new HashMap<>();
        	tmpStr1 = searcher.getValueInDocument(i, "NUMBER1");
        	tmpStr2 = searcher.getValueInDocument(i, "NUMBER2");
        	tmpStr3 = searcher.getValueInDocument(i, "NUMBER3");
        	
    		map.put("level", searcher.getValueInDocument(i, "LEVEL"));
    		String  number0 = searcher.getValueInDocument(i, "NUMBER0");
    		String tmpLev = searcher.getValueInDocument(i, "LEVEL");
    		if("1".equals(tmpLev) && "제 1편".equals(number0) && "1.0".equals(tmpStr1)) {
    			tmpStr4 = "1.";
    		}else if("1".equals(tmpLev) && "제 2편".equals(number0) && "1.0".equals(tmpStr1)) {
    			tmpStr4 = "2.";
    		}else if("1".equals(tmpLev) && "제 3편".equals(number0) && "1.0".equals(tmpStr1)) {
    			tmpStr4 = "3.";
    		}else if("1".equals(tmpLev) && "제 1편".equals(number0) && "B 2.0".equals(tmpStr1)){
    			tmpStr4 = "8.";
    		}else if("1".equals(tmpLev) && "제 1편".equals(number0) && "B 3.0".equals(tmpStr1)){
    			tmpStr4 = "9.";
    		}
    		
    		map.put("position", searcher.getValueInDocument(i, "POSITION"));
    		map.put("fileNm", searcher.getValueInDocument(i, "FILENM"));
    		map.put("level", searcher.getValueInDocument(i, "LEVEL"));
    		
			if(!"".equals(tmpStr1) && "".equals(tmpStr2) && "".equals(tmpStr3)) {		// 1단계
				
        		if("1".equals(tmpLev) && "제 1편".equals(number0) && "1.0".equals(tmpStr1) && gb1 == 0) {
        			HashMap<String, String> map1 = new HashMap<>();
        			map1.put("vid", "");
            		map1.put("name", searcher.getValueInDocument(i, "TITLE0"));
            		map1.put("pid", tmpStr4+"0");
            		map1.put("id", tmpStr4+"0");
            		map1.put("order", "");
            		gb1 = 1;
            		documents.add(map1);
        			cnt++;
        		}else if("1".equals(tmpLev) && "제 2편".equals(number0) && "1.0".equals(tmpStr1) && gb2 == 0) {
        			HashMap<String, String> map2 = new HashMap<>();
        			map2.put("vid", "");
            		map2.put("name", searcher.getValueInDocument(i, "TITLE0"));
            		map2.put("pid", tmpStr4+"0");
            		map2.put("id", tmpStr4+"0");
            		map2.put("order", "");
            		gb2 = 1;
            		documents.add(map2);
        			cnt++;
        		}else if("1".equals(tmpLev) && "제 3편".equals(number0) && "1.0".equals(tmpStr1) && gb3 == 0) {
        			HashMap<String, String> map3 = new HashMap<>();
        			map3.put("vid", "");
            		map3.put("name", searcher.getValueInDocument(i, "TITLE0"));
            		map3.put("pid", tmpStr4+"0");
            		map3.put("id", tmpStr4+"0");
            		map3.put("order", "");
            		gb3 = 1;
            		documents.add(map3);
        			cnt++;
        		}else if("1".equals(tmpLev) && "제 1편".equals(number0) && "B 2.0".equals(tmpStr1) && gb4 == 0) {
        			HashMap<String, String> map4 = new HashMap<>();
        			map4.put("vid", "");
            		map4.put("name", searcher.getValueInDocument(i, "TITLE0"));
            		map4.put("pid", tmpStr4+"0");
            		map4.put("id", tmpStr4+"0");
            		map4.put("order", "");
            		gb4 = 1;
            		documents.add(map4);
        			cnt++;
        		}else if("1".equals(tmpLev) && "제 1편".equals(number0) && "B 3.0".equals(tmpStr1) && gb5 == 0) {
        			HashMap<String, String> map4 = new HashMap<>();
        			map4.put("vid", "");
            		map4.put("name", searcher.getValueInDocument(i, "TITLE0"));
            		map4.put("pid", tmpStr4+"0");
            		map4.put("id", tmpStr4+"0");
            		map4.put("order", "");
            		gb5 = 1;
            		documents.add(map4);
        			cnt++;
        		}
				map.put("vid", tmpStr1);
        		map.put("name", searcher.getValueInDocument(i, "TITLE1"));
        		map.put("pid", tmpStr4+"0");
        		map.put("id", tmpStr4+tmpStr1);
        		map.put("order", searcher.getValueInDocument(i, "ORDERNUM"));
        	}else if(!"".equals(tmpStr1) && !"".equals(tmpStr2) && "".equals(tmpStr3)) {	// 2단계
        		map.put("vid", tmpStr2);
        		map.put("name", searcher.getValueInDocument(i, "TITLE2"));
        		map.put("pid", tmpStr4+tmpStr1);
        		map.put("id", tmpStr4+tmpStr2);
        		map.put("order", searcher.getValueInDocument(i, "ORDERNUM"));
        	}else {																			// 3단계
        		map.put("vid", tmpStr3);
        		map.put("name", searcher.getValueInDocument(i, "TITLE3"));
        		map.put("pid", tmpStr4+tmpStr2);
        		map.put("id", tmpStr4+tmpStr3);
        		map.put("order", searcher.getValueInDocument(i, "ORDERNUM"));
        	}
        	
        	if(i > 0) {
        		if(!map.get("name").toString().equals(searcher.getValueInDocument(i-1, "TITLE"+map.get("level").toString()))) {
        			
        			documents.add(map);
        			cnt++;
        		}
        	}else {
        		documents.add(map);
        		cnt++;
        	}
        }
        log.info("query : {} , return count: {}",  searcher.getDocumentCount());
    	return new IntegrationSearchResult(documents, cnt);
    }
    
//    public IntegrationSearchResult targetSearch(){
//    	IN2StdSearcher searcher = new IN2StdSearcher();
//    	init(searcher, "target");
//    	
//    	searcher.setQuery(IN2Query.MatchingAllDocQuery()); //쿼리가 존재하지 않음
//    	
//    	IN2MultiSort multiSort = new IN2MultiSort();
//    	multiSort.add(new IN2FieldSort("FILENM", true));
//    	multiSort.add(new IN2FieldSort("SORT", true));
//    	
//    	searcher.setSort(multiSort);    	
//        searcher.setReturnPositionCount(0, 10000);
//        
//        searcher.addReturnField(new String[]{"TITLE1","POSITION","FILENM"});
//        
//        if(!searcher.searchDocument())	//검색 요청
//            throw new RuntimeException(searcher.getLastErrorMessage());
//    	
//        List<HashMap<String, String>> documents = new ArrayList<>();
//    	int cnt = 0;
//        for (int i = 0; i < searcher.getDocumentCount(); i++) {
//        	HashMap<String, String> map = new HashMap<>();
//        	
//        	map.put("position", searcher.getValueInDocument(i, "POSITION"));
//    		map.put("fileNm", searcher.getValueInDocument(i, "FILENM"));
//    		map.put("title", searcher.getValueInDocument(i, "TITLE1"));
//        	
//        	
//    		documents.add(map);
//    		cnt++;
//        }
//        log.info("query : {} , return count: {}",  searcher.getDocumentCount());
//    	return new IntegrationSearchResult(documents, cnt);
//    }
}
