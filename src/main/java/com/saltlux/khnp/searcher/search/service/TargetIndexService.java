package com.saltlux.khnp.searcher.search.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.dor.api.common.sort.IN2FieldSort;
import com.saltlux.dor.api.common.sort.IN2MultiSort;

@Service
public class TargetIndexService {
	
	@Value("${in2.dor.host}")
	private String host;
	@Value("${in2.dor.port}")
	private Integer port;
	    
	@Value("${in2.dor.index.name6}")
	private String index6;
	@Value("${in2.dor.index.name7}")
	private String index7;
	
	private SearchObject init(SearchObject searcher){
	    searcher.setServer(host, port);
        searcher.addIndex(index6);
        searcher.addIndex(index7);
	        
	    return searcher;
	}
	
	public void targetIndex(String query) throws Exception{
		//############### 타겟 색인 불러오기 ###################
		IN2StdSearcher searcher = new IN2StdSearcher();
		init(searcher);
		
		System.out.println("query ::"+query);
		if(StringUtils.isNotBlank(query)){	
			searcher.setQuery(new IN2ParseQuery("FILENM", query.trim(), IN2StdSearcher.TOKENIZER_KOR_BIGRAM));
		}else {
			searcher.setQuery(IN2Query.MatchingAllDocQuery()); //쿼리가 존재하지 않음
		}
		
		
		IN2MultiSort multiTargetSort = new IN2MultiSort();
		multiTargetSort.add(new IN2FieldSort("FILENM", true));
		multiTargetSort.add(new IN2FieldSort("NUMBER1", true));
		multiTargetSort.add(new IN2FieldSort("NUMBER2", true));
		multiTargetSort.add(new IN2FieldSort("NUMBER3", true));
    	
		searcher.setSort(multiTargetSort);    	
		searcher.setReturnPositionCount(0, 10000);
        
		searcher.addReturnField(new String[]{"INDEXGB","TITLE1","TITLE2","TITLE3","TITLE4","TITLE4_1","NUMBER","LEVEL","POSITION","FILENM"});
        
        if(!searcher.searchDocument())	//검색 요청
            throw new RuntimeException(searcher.getLastErrorMessage());
    	
        Map<String, Map<String, String>> targetMap = new LinkedHashMap<String, Map<String, String>>();
        for (int i = 0; i < searcher.getDocumentCount(); i++) {
        	HashMap<String, String> map = new HashMap<>();
        	
        	map.put("indexgb", searcher.getValueInDocument(i, "INDEXGB"));
        	map.put("title1", searcher.getValueInDocument(i, "TITLE1"));
        	map.put("title2", searcher.getValueInDocument(i, "TITLE2"));
        	map.put("title3", searcher.getValueInDocument(i, "TITLE3"));
        	map.put("title4", searcher.getValueInDocument(i, "TITLE4"));
        	map.put("title4_1", searcher.getValueInDocument(i, "TITLE4_1"));
        	map.put("number", searcher.getValueInDocument(i, "NUMBER"));
        	map.put("level", searcher.getValueInDocument(i, "LEVEL"));
        	map.put("position", searcher.getValueInDocument(i, "POSITION"));
    		map.put("fileNm", searcher.getValueInDocument(i, "FILENM"));

    		targetMap.put(i+"",map);
        }
        
		HashMap<String, String> htmlMap = new LinkedHashMap<>();
		String readLine = null ;
		int lineNum = 0;
		
		String oriFilePath = "C:\\work\\convert" + File.separator + query;
		String copyFilePath = "C:\\work\\convert" + File.separator + "index_"+query;
		try{
			File file = new File(oriFilePath);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			while((readLine =  bufReader.readLine()) != null ){
				lineNum++;
				htmlMap.put(lineNum+"", readLine+"\r\n");
			}
			bufReader.close();

			//############## htm indexing 작성 시작 #####################
			for( Map.Entry<String, Map<String, String>> tEm : targetMap.entrySet() ){
				if("index".equals(tEm.getValue().get("indexgb"))) {
					if(!"".equals(tEm.getValue().get("level"))) {
						if(!htmlMap.get(tEm.getValue().get("position").split("-")[0]).contains("<div class='#hilight")) {
							htmlMap.put(tEm.getValue().get("position").split("-")[0], "<div class='#hilight-"+tEm.getValue().get("position").split("-")[0]+"'>"+htmlMap.get(tEm.getValue().get("position").split("-")[0]));
							htmlMap.put(tEm.getValue().get("position").split("-")[1], htmlMap.get(tEm.getValue().get("position").split("-")[1])+"</div>");
						}
					}
				}else{
					if(!htmlMap.get(tEm.getValue().get("position").split("-")[0]).contains("<div class='#hilight")) {
						htmlMap.put(tEm.getValue().get("position").split("-")[0], "<div class='#hilight-"+tEm.getValue().get("position").split("-")[0]+"'>"+htmlMap.get(tEm.getValue().get("position").split("-")[0]));
						htmlMap.put(tEm.getValue().get("position").split("-")[1], htmlMap.get(tEm.getValue().get("position").split("-")[1])+"</div>");
					}
				}
		    }
			for( Map.Entry<String, Map<String, String>> tEl : targetMap.entrySet() ){
				if("target".equals(tEl.getValue().get("indexgb"))) {
					String tmpTitle = tEl.getValue().get("title1");
					for( Map.Entry<String, String> hElem : htmlMap.entrySet() ){
						if(!hElem.getValue().contains("<div class='#hilight")) {
							if(hElem.getValue().contains(tmpTitle)) {
								String tmpHtml = "<a href=\"javascript:void(0);\" onclick=\"openCall('"+tmpTitle+"')\">"+tmpTitle+"</a>";
								htmlMap.put(hElem.getKey(), hElem.getValue().replaceAll(tmpTitle, tmpHtml));
							}
						}
					}
				}
			}
			
			//############### htm indexing 작업 끝 ###################
			File copyFile = new File(copyFilePath);
			FileOutputStream fos = new FileOutputStream(copyFile); //복사할파일
			for( Map.Entry<String, String> elem : htmlMap.entrySet() ){
				fos.write(elem.getValue().getBytes());
		    }
			fos.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
