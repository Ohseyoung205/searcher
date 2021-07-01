package com.saltlux.khnp.searcher.indexer.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.saltlux.khnp.searcher.indexer.common.Consts;
import com.saltlux.khnp.searcher.indexer.indexer.HtmIndexing;
import com.saltlux.khnp.searcher.indexer.vo.Document;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IndexTarget {

	@Value("${htm.file.path}")
    private String htmFilePath;
	
	@Autowired
	HtmIndexing htmIndexing;
	
	@Value("${pattern1.title1}")
    private String ptnTitle1;
	@Value("${pattern1.title2}")
    private String ptnTitle2;
	@Value("${pattern1.title3}")
    private String ptnTitle3;
	
	@Value("${pattern3.ptns1}")
    private String ptnPtns1;
	@Value("${pattern3.ptns2}")
    private String ptnPtns2;
	
public boolean indexTarget(String domain, String fileNm) throws Exception{
		
		String title1_pattern1 = ptnTitle1+Consts.TWOSPACE+".*"; // 1.0, 2.0 ,3.0 구분
		String title2_pattern1 = ptnTitle2+Consts.TWOSPACE+".*"; // 1.1, 1.2, 1.3 ...2.1
		String title3_pattern1 = ptnTitle3+Consts.TWOSPACE+".*"; // 1.1.0,1.1.1
		
		Map<String, String> map = null;
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
			
		String readLine = null ;
	    int lineNum = 0;			//라인 넘버
	    int startNum = 0;			//시작
	    int startLine = 0;
	    String tagStr = "";
	    Pattern tags = Pattern.compile("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>");
	    Pattern ptns1 = Pattern.compile("<IMG src=.*?>(.*?)"); //이미지 패턴
	    Pattern ptns2 = Pattern.compile("^그림 [1-9.-]{2,8}");
	    Pattern ptns3 = Pattern.compile(ptnPtns1);
	    Pattern ptns4 = Pattern.compile(ptnPtns2);
	    
	    Matcher m;
		String title1 = "";
		String tmpStr = "";
		String tmpStr1 = "";
		boolean saveBln = false;
		
		String imgChk = "";
		int imgLineNum = 0;
		int numberChk = 0;
		
		String level = "";
		String number1 = "";		
		String number2 = "";
		String number3 = "";
		
		try{
			File file = new File(htmFilePath+File.separator+domain+File.separator+fileNm);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

	        while((readLine =  bufReader.readLine()) != null ){
	            map = new HashMap<String, String>();
	            lineNum++;
	            if("<BODY>".equals(readLine)) {
	            	startNum = lineNum;
	            }
	            	
	            if(startNum > 0) {
	            	
	            	saveBln = false;
	            	//######## 이미지 #############
	            	m = tags.matcher(readLine); //태그삭제
	            	tmpStr = m.replaceAll("");
	            	
	            	tagStr = m.replaceAll("").replaceAll("&nbsp;", " ").replaceAll("&#9552;", "");
	            	if(tagStr.matches(title1_pattern1)) {			//1 단계
	            		number1 = tagStr.split(" ")[0];
	            		if("B".equals(number1)) {
	            			number1 = tagStr.split(" ")[0]+" "+tagStr.split(" ")[1];
	            		}
           				number2 = "";
           				number3 = "";
           				level = "1";
           				startLine = lineNum;
               		}else if(tagStr.matches(title2_pattern1)) {		//2 단계
               			number2 = tagStr.split(" ")[0];
               			if("B".equals(number2)) {
               				number1 = tagStr.split(" ")[0]+" "+tagStr.split(" ")[1];
               			}
           				number3 = "";
            			level = "2";
            			startLine = lineNum;
               		}else if(tagStr.matches(title3_pattern1)) {		//3 단계
               			number3 = tagStr.split(" ")[0];
               			if("B".equals(number3)) {
               				number1 = tagStr.split(" ")[0]+" "+tagStr.split(" ")[1];
               			}
           				level = "3";
           				startLine = lineNum;
               		}
	            	
	            	m = ptns1.matcher(readLine);
	            	while (m.find()) {
	            		imgLineNum = lineNum;
	            		imgChk = "chk";
	            	}
	            	if("chk".equals(imgChk)) {
	            		m = ptns2.matcher(tmpStr);
		            	while (m.find()){
		            		tmpStr1 = m.group(0);
		            		tmpStr = tmpStr.replaceAll("&nbsp;", "");
		            		if(tmpStr.contains(tmpStr1)) {
		            			imgChk = "";
		            			saveBln = true;
		            			title1 = tmpStr1;
			            		startLine = imgLineNum;
		            		}
		            	}
	            	}
	            	//##########  표, 운전제한조건 ###############
	            	m = ptns3.matcher(tmpStr);
	            	while (m.find()) {
	            		tmpStr1 = m.group(0);
	            		tmpStr = tmpStr.replaceAll("&nbsp;", "");
	            		if(tmpStr.contains(tmpStr1)) {
	            			saveBln = true;
		            		title1 = tmpStr1;
		            		startLine = lineNum;
	            		}
	            	}
	            	
	            	//########## 점검요구사항  #############
	            	m = ptns4.matcher(tmpStr.trim());
	            	while (m.find()) {
	            		if(!tmpStr.contains("(계속)")) {
	            			tmpStr1 = m.group(0);
	            			if(tmpStr.contains("&nbsp;&nbsp;")) {
			            		saveBln = true;
			            		title1 = tmpStr1;
			            		startLine = lineNum;
	            			}else if(!tmpStr.contains("&nbsp;")){
	            				if(tmpStr.length() < 20) {
				            		saveBln = true;
				            		title1 = tmpStr1;
				            		startLine = lineNum;
	            				}
	            			}
	            		}
	            	}

	            	if(saveBln) {
	            		numberChk++;
	            		String key = "KEY_"+Consts.BIG_TITLE1+title1+startLine;
	            		
	            		map.put("TITLE0", Consts.BIG_TITLE1);
       					map.put("TITLE1", title1.trim());
       					map.put("TITLE2", "");
       					map.put("TITLE3", "");
       					map.put("TITLE4", "");
       					map.put("TITLE4_1", "");
						map.put("NUMBER0", Consts.BIG_NUMBER1);
						map.put("NUMBER1", number1);
						map.put("NUMBER2", number2);
						map.put("NUMBER3", number3);
						map.put("NUMBER4", "");
						map.put("CONTENT", title1.trim());
						map.put("STARTLINE", startLine+"");
						map.put("ENDLINE", startLine+"");
						map.put("SORT", startLine+"");
						map.put("LEVEL", level);
						map.put("ETC", "");
						map.put("FILENAME", "1.htm");
							
						resultMap.put(key, map);
	            	}
	            }
	        }
	        bufReader.close();
	    }catch ( IOException e ) {
	        return false;
	    }
		
		TreeMap<String, Map<String, String>> tm = new TreeMap<String, Map<String, String>>(resultMap);
		Iterator<String> iteratorKey = tm.keySet().iterator(); 
		List<Document> voList = new ArrayList<Document>();
		while (iteratorKey.hasNext()) {
			String key = iteratorKey.next();
			Document vo = new Document();
			if("".equals(tm.get(key).get("TITLE0")) || "".equals(tm.get(key).get("NUMBER0"))) {
				vo.setContent("");
				vo.setEtc("");
				vo.setPosition("");
			}else {
				vo.setContent(tm.get(key).get("CONTENT"));
				vo.setEtc(tm.get(key).get("ETC"));
				vo.setPosition(tm.get(key).get("STARTLINE")+"-"+tm.get(key).get("ENDLINE"));
			}
			vo.setNumber0(tm.get(key).get("NUMBER0"));
			vo.setTitle0(tm.get(key).get("TITLE0"));
			vo.setTitle1(tm.get(key).get("TITLE1"));
			vo.setTitle2(tm.get(key).get("TITLE2"));
			vo.setTitle3(tm.get(key).get("TITLE3"));
			vo.setTitle4(tm.get(key).get("TITLE4"));
			vo.setTitle4_1(tm.get(key).get("TITLE4_1"));
			vo.setNumber1(tm.get(key).get("NUMBER1"));
			vo.setNumber2(tm.get(key).get("NUMBER2"));
			vo.setNumber3(tm.get(key).get("NUMBER3"));
			vo.setNumber4(tm.get(key).get("NUMBER4"));
		    vo.setEtc(tm.get(key).get("ETC"));
		    vo.setLevel(tm.get(key).get("LEVEL"));
		    vo.setFileNm(tm.get(key).get("FILENAME"));
		    vo.setIndexgb("target");
		    vo.setDomain(domain);
//			if (tm.get(key).get("CONTENT").getBytes().length > 32000) {
//				System.out.println("==========초과  ====");
//				System.out.println(tm.get(key).get("CONTENT"));
//			}
				
			voList.add(vo);
		}
		
		if(voList.size() > 0) {
			return htmIndexing.indexing(voList, domain, 2);
		}else {
			return true;
		}
		
	}
}
