package com.saltlux.khnp.searcher.indexer.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.saltlux.dor.api.IN2FacetSearcher;
import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.IN2FacetResult;
import com.saltlux.dor.api.common.SearchObject;
import com.saltlux.dor.api.common.filter.IN2TermsFilter;
import com.saltlux.dor.api.common.query.IN2ParseQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import com.saltlux.dor.api.common.sort.IN2FieldSort;
import com.saltlux.dor.api.common.sort.IN2MultiSort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TargetIndexService {
	
	@Value("${in2.dor.host}")
	private String host;
	@Value("${in2.dor.port}")
	private Integer port;
	    
	
	@Value("${htm.file.path}")
	private String htmPath;
	
	private SearchObject init(SearchObject searcher, String index_name){
	    searcher.setServer(host, port);
	    searcher.addIndex(index_name);
	    return searcher;
	}
	
	public void targetIndex(String domain, String fileNm, String indexName, String name) throws Exception{
		//############### 타겟 색인 불러오기 ###################
		IN2StdSearcher searcher = new IN2StdSearcher();
		init(searcher, indexName);
		
		if(StringUtils.isNotBlank(fileNm)){	
			searcher.setQuery(new IN2ParseQuery("FILENM", fileNm.trim(), IN2StdSearcher.TOKENIZER_KOR_BIGRAM));
		}else {
			searcher.setQuery(IN2Query.MatchingAllDocQuery()); //쿼리가 존재하지 않음
		}
		searcher.setFilter(new IN2TermsFilter("DOMAIN", name.split(";"), IN2StdSearcher.SOURCE_TYPE_TEXT));
		
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
        
        final String fieldName = "TOKENTITLE";
        
        Map<String, Map<String, String>> facetMap = new LinkedHashMap<String, Map<String, String>>();
        IN2FacetSearcher facetSearcher = new IN2FacetSearcher();
        facetSearcher.setServer(host, port);
        facetSearcher.setFilter(new IN2TermsFilter("DOMAIN", name.split(";"), IN2StdSearcher.SOURCE_TYPE_TEXT));
        facetSearcher.newQuery();
        facetSearcher.addIndex(indexName);

        if(StringUtils.isNotBlank(fileNm)){	
        	facetSearcher.setQuery(new IN2ParseQuery("INDEXGB", "target", IN2StdSearcher.TOKENIZER_KOR_BIGRAM));
		}else {
			facetSearcher.setQuery(IN2Query.MatchingAllDocQuery()); //쿼리가 존재하지 않음
		}

        facetSearcher.addSimpleFacet(fieldName, 1000);
        if (!facetSearcher.searchDocument()) {
            throw new RuntimeException(facetSearcher.getLastErrorMessage());
        }

        List<IN2FacetResult.LabelAndCount> root = facetSearcher.getFacetResult(fieldName).getRoot();
        
        for(int i=0; i<root.size();i++) {
        	HashMap<String, String> fMap = new HashMap<>();
        	fMap.put("tokentitle", root.get(i).label);
        	facetMap.put(i+"",fMap);
        }
        
        Matcher m;
	    Pattern ptns1 = Pattern.compile("[가-힣]</SPAN> <SPAN STYLE=''>[0-9.]{2,8}");
	    
	    String regExp = "[0-9]";
	    String checkStr = "";
	    
		HashMap<String, String> htmlMap = new LinkedHashMap<>();
		String readLine = null ;
		int lineNum = 0;

		String oriFilePath = htmPath+File.separator+domain+File.separator+fileNm;
		String copyFilePath = htmPath+File.separator+domain+File.separator+"index_"+fileNm;
		
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
						if(tEm.getValue().get("position").trim() != null && !"".equals(tEm.getValue().get("position").trim())) {
							if(!htmlMap.get(tEm.getValue().get("position").split("-")[0]).contains("<div class='hilight")) {
								htmlMap.put(tEm.getValue().get("position").split("-")[0], "<div class='hilight-"+tEm.getValue().get("position").split("-")[0]+"'>"+htmlMap.get(tEm.getValue().get("position").split("-")[0]));
								htmlMap.put(tEm.getValue().get("position").split("-")[1], htmlMap.get(tEm.getValue().get("position").split("-")[1])+"</div>");
							}
						}
					}
				}else{
					if(tEm.getValue().get("position").trim() != null && !"".equals(tEm.getValue().get("position").trim())) {
						if(htmlMap.get(tEm.getValue().get("position").split("-")[0]) != null) {
							if(!htmlMap.get(tEm.getValue().get("position").split("-")[0]).contains("<div class='hilight")) {
								htmlMap.put(tEm.getValue().get("position").split("-")[0], "<div class='hilight-"+tEm.getValue().get("position").split("-")[0]+"'>"+htmlMap.get(tEm.getValue().get("position").split("-")[0]));
								htmlMap.put(tEm.getValue().get("position").split("-")[1], htmlMap.get(tEm.getValue().get("position").split("-")[1])+"</div>");
							}
						}
					}
				}
		    }
			//</SPAN> <SPAN STYLE=''>을 ""로 치환
			for( Map.Entry<String, String> hElem : htmlMap.entrySet() ){
				m = ptns1.matcher(hElem.getValue());
				if(m.find()) {
					htmlMap.put(hElem.getKey(), hElem.getValue().replaceAll("</SPAN> <SPAN STYLE=''>", ""));
				}
			}
			
			for( Map.Entry<String, Map<String, String>> tEl : facetMap.entrySet() ){
				String tmpTitle = tEl.getValue().get("tokentitle");
				for( Map.Entry<String, String> hElem : htmlMap.entrySet() ){
					String tmpHtml = "<a href=\"javascript:void(0);\" onclick=\"openCall('"+tmpTitle+"')\">"+tmpTitle+"</a>";
					if(hElem.getValue().indexOf(tmpTitle) != -1) {
						checkStr = hElem.getValue().substring(hElem.getValue().indexOf(tmpTitle)+tmpTitle.length(), hElem.getValue().indexOf(tmpTitle)+tmpTitle.length()+1);
						if(!checkStr.matches(regExp)) {
							if(!"원자로건물".equals(tmpTitle) && !"가압기".equals(tmpTitle) && !"사용후연료저장조".equals(tmpTitle) && !"붕소농도".equals(tmpTitle)
									&& !"비방사능".equals(tmpTitle)) {
								htmlMap.put(hElem.getKey(), hElem.getValue().replaceAll(tmpTitle, tmpHtml));
							}
						}
					}
					if("운전제한조건 3.1.4".equals(tmpTitle)) {
						htmlMap.put(hElem.getKey(), hElem.getValue().replaceAll("운전제한조건3.1.4", tmpHtml));
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
			
			Path filePath = Paths.get(copyFilePath);
			//############### 파일 소유자 변경 ###############
			UserPrincipal hostUid = filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("saltlux");
			Files.setOwner(filePath, hostUid);
			//############### 파일 그룹 변경 ################
			GroupPrincipal group =filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("saltlux");
			Files.getFileAttributeView(filePath, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setGroup(group);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
