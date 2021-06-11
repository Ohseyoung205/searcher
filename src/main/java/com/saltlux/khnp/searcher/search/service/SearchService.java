package com.saltlux.khnp.searcher.search.service;

import static com.saltlux.khnp.searcher.common.config.INDEX_FIELD.CONTENTS;
import static com.saltlux.khnp.searcher.common.config.INDEX_FIELD.NUMBER;
import static com.saltlux.khnp.searcher.common.config.INDEX_FIELD.TITLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.query.IN2BooleanQuery;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2PrefixQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.dor.api.common.sort.IN2FieldSort;
import com.saltlux.dor.api.common.sort.IN2MultiSort;
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
    @Value("${in2.dor.index.name6}")
    private String index6;
    @Value("${in2.dor.index.name7}")
    private String index7;

    private SearchObject init(SearchObject searcher, String gb){
        searcher.setServer(host, port);
        if("target".equals(gb)) {
        	searcher.addIndex(index6);
        }else if("table".equals(gb)){
        	searcher.addIndex(index7);
        }else {
        	searcher.addIndex(index6);
        	searcher.addIndex(index7);
        }
        return searcher;
    }
    
    
    public IntegrationSearchResult integrationSearch(SearchRequests requests){
        IN2StdSearcher searcher = new IN2StdSearcher();
        init(searcher, "");
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
            if("".equals(requests.getFilter().get(0).getFilterTerm())) {
            	bQuery.add(new IN2ParseQuery(CONTENTS.fieldName, requests.getFilter().get(0).getFilterTerm(), CONTENTS.analyzer), IN2Query.OR);
            }
            
            searcher.setQuery(bQuery);
        }else{
            searcher.setQuery(IN2Query.MatchingAllDocQuery());
        }
        
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
            		if("".equals(searcher.getValueInDocument(i, field.getReturnField().toUpperCase()))) {
            			map.put("rnumber", searcher.getValueInDocument(i, field.getReturnField().toUpperCase()));
            			map.put(field.getReturnField(), strSplit+"0");
            		}else {
            			map.put("rnumber", searcher.getValueInDocument(i, field.getReturnField().toUpperCase()));
            			map.put(field.getReturnField(), strSplit+searcher.getValueInDocument(i, field.getReturnField().toUpperCase()));
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
    	IN2StdSearcher searcher = new IN2StdSearcher();
    	init(searcher, "table");
    	
    	if(StringUtils.isNotBlank(query)){
    		IN2BooleanQuery bQuery = new IN2BooleanQuery();
    		bQuery.add(new IN2PrefixQuery(NUMBER.fieldName, query), IN2Query.OR);
    		searcher.setQuery(bQuery);
    	}else {
    		searcher.setQuery(IN2Query.MatchingAllDocQuery()); //쿼리가 존재하지 않음
    	}
    	
    	IN2MultiSort multiSort = new IN2MultiSort();
    	multiSort.add(new IN2FieldSort("FILENM", true));
    	multiSort.add(new IN2FieldSort("NUMBER1", true));
    	multiSort.add(new IN2FieldSort("NUMBER2", true));
    	multiSort.add(new IN2FieldSort("NUMBER3", true));
    	
    	searcher.setSort(multiSort);    	
        searcher.setReturnPositionCount(0, 10000);
        
        searcher.addReturnField(new String[]{"TITLE0","TITLE1","TITLE2","TITLE3", "NUMBER1","NUMBER2","NUMBER3","POSITION","FILENM","LEVEL"});
        
        if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());

        
    	List<HashMap<String, String>> documents = new ArrayList<>();
    	String tmpStr1 = "";
    	String tmpStr2 = "";
    	String tmpStr3 = "";
    	String tmpStr4 = "";
    	int cnt = 0;
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
        	HashMap<String, String> map = new HashMap<>();
        	
        	tmpStr1 = searcher.getValueInDocument(i, "NUMBER1");
        	tmpStr2 = searcher.getValueInDocument(i, "NUMBER2");
        	tmpStr3 = searcher.getValueInDocument(i, "NUMBER3");
        	tmpStr4 = searcher.getValueInDocument(i, "FILENM");
        	tmpStr4 = tmpStr4.substring(0, 2);
        	
        	map.put("position", searcher.getValueInDocument(i, "POSITION"));
    		map.put("fileNm", searcher.getValueInDocument(i, "FILENM"));
    		map.put("level", searcher.getValueInDocument(i, "LEVEL"));
        	if("".equals(tmpStr1)) {														// 0단계
        		map.put("id", tmpStr4+"0");
        		map.put("name", searcher.getValueInDocument(i, "TITLE0"));
        		map.put("pid", "");
        		map.put("rid", "");
        	}else if(!"".equals(tmpStr1) && "".equals(tmpStr2)) {							// 1단계
        		map.put("id", tmpStr4+tmpStr1);
        		map.put("name", searcher.getValueInDocument(i, "TITLE1"));
        		map.put("pid", tmpStr4+"0");
        		map.put("rid", tmpStr1);
        	}else if(!"".equals(tmpStr1) && !"".equals(tmpStr2) && "".equals(tmpStr3)) {	// 2단계
        		map.put("id", tmpStr4+tmpStr2);
        		map.put("name", searcher.getValueInDocument(i, "TITLE2"));
        		map.put("pid", tmpStr4+tmpStr1);
        		map.put("rid", tmpStr2);
        	}else {																			// 3단계
        		map.put("id", tmpStr4+tmpStr3);
        		map.put("name", searcher.getValueInDocument(i, "TITLE3"));
        		map.put("pid", tmpStr4+tmpStr2);
        		map.put("rid", tmpStr2);
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
    
    public IntegrationSearchResult targetSearch(){
    	IN2StdSearcher searcher = new IN2StdSearcher();
    	init(searcher, "target");
    	
    	searcher.setQuery(IN2Query.MatchingAllDocQuery()); //쿼리가 존재하지 않음
    	
    	IN2MultiSort multiSort = new IN2MultiSort();
    	multiSort.add(new IN2FieldSort("FILENM", true));
    	multiSort.add(new IN2FieldSort("SORT", true));
    	
    	searcher.setSort(multiSort);    	
        searcher.setReturnPositionCount(0, 10000);
        
        searcher.addReturnField(new String[]{"TITLE1","POSITION","FILENM"});
        
        if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());
    	
        List<HashMap<String, String>> documents = new ArrayList<>();
    	int cnt = 0;
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
        	HashMap<String, String> map = new HashMap<>();
        	
        	map.put("position", searcher.getValueInDocument(i, "POSITION"));
    		map.put("fileNm", searcher.getValueInDocument(i, "FILENM"));
    		map.put("title", searcher.getValueInDocument(i, "TITLE1"));
        	
        	
    		documents.add(map);
    		cnt++;
        }
        log.info("query : {} , return count: {}",  searcher.getDocumentCount());
    	return new IntegrationSearchResult(documents, cnt);
    }
}
