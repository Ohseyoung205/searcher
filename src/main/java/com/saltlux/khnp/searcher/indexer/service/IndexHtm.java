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
public class IndexHtm {
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
	
	public boolean indexHtm(String domain, String fileNm) throws Exception{
		System.out.println("domain ::"+domain+" || fileNm::"+fileNm);
		String title1_pattern1 = ptnTitle1+Consts.TWOSPACE+".*"; // 1.0, 2.0 ,3.0 구분
		String title2_pattern1 = ptnTitle2+Consts.TWOSPACE+".*";// 1.1, 1.2, 1.3 ...2.1
		String title3_pattern1 = ptnTitle3+Consts.TWOSPACE+".*"; //1.1.0,1.1.1
		
		//=======================제외 pattern=================================
		String except_pattern1 = ".*[(][계][속][)]";
		String except_pattern2 = "---------(.*?)---------";
		String except_pattern3 = "────(.*?)────";		
		String except_pattern4 = "Intentionally Blank";
		String except_pattern5 = "════════(.*?)══════";
		String except_pattern6 = ".*[1-9]";	
		String except_pattern7 = "______(.*?)_______";
		String except_pattern8 = "──────(.*?)───────";
		String except_pattern9 = "󰡈󰡈󰡈󰡈󰡈󰡈󰡈󰡈󰡈(.*?)󰡈󰡈󰡈󰡈󰡈󰡈󰡈󰡈󰡈";
		String except_pattern10 = "---------(.*?)---------";
		String except_pattern11 = "[기][술][배][경]([ ][(][계][속][)]){0,1}";

		//=================================================================
		
		Map<String, String> map = null;
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		
		String readLine = null ;
	    int lineNum = 0;			//라인 넘버
	    int startNum = 0;			//시작
	    int startLine = 0;
	    String tagStr = "";
	    String befo1 = "";
	    String befo2 = "";
	    String befo3 = "";
	    Pattern tags = Pattern.compile("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>");
	    Pattern ptns = Pattern.compile("<P CLASS=HStyle(8|16|18|29).*?>(.*?)<SPAN style='HWP-TAB:1;'>");
	    Matcher m;
	    String level = "";
	    String number1 = "";		
		String number2 = "";
		String number3 = "";
		String title1 = "";
		String title2 = "";
		String title3 = "";
		String title4 = "";
		String title4_1 = "";
		String etc = "";
			
		String tmpStr = "";
		String tmpStr1 = "";
		String tmpStr2 = "";
			
		String tmpGb = "";
		String memberGb = "";
		String indexgb = "";
		String bigTitleGb = "1";
		
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
	            	tmpStr = readLine;
	            	
	            	m = ptns.matcher(readLine);
	            	tmpStr1 = "";
	            	while (m.find()) {
	            		tmpStr1 = m.group(0);
	            	}
	            	tmpStr = tmpStr.replace(tmpStr1, "");
	            	tmpStr1 = tags.matcher(tmpStr1).replaceAll("").replaceAll("&nbsp;", ""); //tag 삭제
	            		
	            	m = tags.matcher(tmpStr);
	                tagStr = m.replaceAll("").replaceAll("&nbsp;", " ").replaceAll("&#9552;", "");
	                
                	if(!tagStr.trim().matches(except_pattern1) && !tagStr.trim().matches(except_pattern2) && !tagStr.trim().matches(except_pattern3)
                			&& !tagStr.trim().matches(except_pattern4) && !tagStr.trim().matches(except_pattern5) && !tagStr.trim().matches(except_pattern6)
                			&& !tagStr.trim().matches(except_pattern7) && !tagStr.trim().matches(except_pattern8) && !tagStr.trim().matches(except_pattern9)
                			&& !tagStr.trim().matches(except_pattern10) && !tagStr.trim().matches(except_pattern11)) {
                		
                		if(tagStr.matches(title1_pattern1)) {			//1 단계
                			if(!befo1.equals(tagStr.trim())) {
                   				number1 = tagStr.split(" ")[0];
                   				if("B".equals(number1)) {
                   					memberGb = "Y";
                   					number1 = tagStr.split(" ")[0]+" "+tagStr.split(" ")[1];
                   				}
                   				number2 = "";
                   				number3 = "";
                   				title1 = tagStr.replace(number1, "");
                   				title2 = "";
                   				title3 = "";
                   				level = "1";
                   				indexgb= "index";
                   				startLine = lineNum;
                   				befo1 = tagStr.trim();
                   			}
                   		}else if(tagStr.matches(title2_pattern1)) {		//2 단계
                   			if(!befo2.equals(tagStr.trim())) {
                   				number2 = tagStr.split(" ")[0];
                   				if("B".equals(number2)) {
                   					memberGb = "Y";
                   					number2 = tagStr.split(" ")[0]+" "+tagStr.split(" ")[1];
                   				}
                   				number3 = "";
                    			title2 = tagStr.replace(number2, "");
                    			title3 = "";
                    			level = "2";
                    			indexgb= "index";
                    			startLine = lineNum;
                    			befo2 = tagStr.trim();
                    		}
                   		}else if(tagStr.matches(title3_pattern1)) {		//3 단계
                   			String tmpNumber = tagStr.split(" ")[0];
                   			if(!"2.2.1".equals(tmpNumber)) {
                   				if(!befo3.equals(tagStr.trim())) {
                       				number3 = tagStr.split(" ")[0];
                       				if("B".equals(number3)) {
                       					memberGb = "Y";
                       					number3 = tagStr.split(" ")[0]+" "+tagStr.split(" ")[1];
                       				}
                       				title3 = tagStr.replace(number3, "");
                       				level = "3";
                       				indexgb= "index";
                       				startLine = lineNum;
                       				befo3 = tagStr.trim();
                       			}
                   			}
                   		}
                			
               			if(!tagStr.matches(title1_pattern1) && !tagStr.matches(title2_pattern1)
               					&& !tagStr.matches(title3_pattern1)) {
               				if(!"".equals(tmpStr1) && !"|".equals(tagStr)) {
                				if(!"1.".equals(tmpStr1.trim()) && !"2.".equals(tmpStr1.trim()) && !"3.".equals(tmpStr1.trim()) 
                						&& !"운전제한조건 3.4.10".equals(tmpStr1.trim())) {
                					if(!"1".equals(tmpGb)) {
                    					tmpStr2 = tmpStr1.replace("(계속)", "").trim();
                    					tmpStr2 = number1+number2+number3+tmpStr2;
                    					indexgb= "target";
                    				}
                    				startLine = lineNum;
                    				tmpGb = "1";
                				}
                			}else {
                				tmpGb = "";
                			}
               				
                			String key = "KEY_"+Consts.BIG_TITLE1+number1+title1+number2+title2+number3+title3+tmpStr2;
                			String realStr = tagStr.replace("|", "\r");
                			
                			if("제 1 편".equals(realStr)) {
                				indexgb= "index";
                				bigTitleGb = "1";
                				key = "KEY_"+Consts.BIG_TITLE1+number1+title1+number2+title2+number3+title3+tmpStr2;
               				}else if("제 2 편".equals(realStr)) {
               					indexgb= "index";
               					bigTitleGb = "2";
               					key = "KEY_"+Consts.BIG_TITLE2+number1+title1+number2+title2+number3+title3+tmpStr2;
               				}else if("제 3 편".equals(realStr)) {
               					indexgb= "index";
               					bigTitleGb = "3";
               					key = "KEY_"+Consts.BIG_TITLE3+number1+title1+number2+title2+number3+title3+tmpStr2;
               				}
                			if("".equals(number1)) {
                				indexgb= "index";
                			}
                			if (resultMap.containsKey(key)) {
                				String content = resultMap.get(key).get("CONTENT");
                				String strTitle = resultMap.get(key).get("TITLE4_1");
                				resultMap.get(key).put("CONTENT",content + realStr);
                				tmpStr1 = tmpStr1.replace("(계속)", "");
                				if(!strTitle.trim().equals(tmpStr1.trim())) {
                					if(!"".equals(tmpStr1)) {
                						if(!"1.".equals(tmpStr1.trim()) && !"2.".equals(tmpStr1.trim()) && !"3.".equals(tmpStr1.trim())
                								&& !"운전제한조건 3.4.10".equals(tmpStr1.trim())) {
                							resultMap.get(key).put("TITLE4_1", strTitle+" "+tmpStr1);
                						}
                					}
                				}
               					resultMap.get(key).put("ENDLINE", lineNum+"");
               				}else {
               					if("1".equals(bigTitleGb)) {
               						map.put("TITLE0", Consts.BIG_TITLE1);
               					}else if("2".equals(bigTitleGb)) {
               						map.put("TITLE0", Consts.BIG_TITLE2);
               					}else if("3".equals(bigTitleGb)) {
               						map.put("TITLE0", Consts.BIG_TITLE3);
               					}
               					
               					
               					map.put("TITLE1", title1.trim());
               					map.put("TITLE2", title2.trim());
               					map.put("TITLE3", title3.trim());
               					map.put("TITLE4", "");
               					if(!"1.".equals(tmpStr1.trim()) && !"2.".equals(tmpStr1.trim()) && !"3.".equals(tmpStr1.trim())
               							&& !"운전제한조건 3.4.10".equals(tmpStr1.trim())) {
               						map.put("TITLE4_1", tmpStr1.replace("(계속)", "").trim());
               					}else {
               						map.put("TITLE4_1", "");
               					}
               					if("1".equals(bigTitleGb)) {
               						map.put("NUMBER0", Consts.BIG_NUMBER1);
               					}else if("2".equals(bigTitleGb)) {
               						map.put("NUMBER0", Consts.BIG_NUMBER2);
               					}else if("3".equals(bigTitleGb)) {
               						map.put("NUMBER0", Consts.BIG_NUMBER3);
               					}
								
								map.put("NUMBER1", number1);
								map.put("NUMBER2", number2);
								map.put("NUMBER3", number3);
								map.put("NUMBER4", "");
								map.put("CONTENT", realStr);
								map.put("STARTLINE", startLine+"");
								map.put("ENDLINE", lineNum+"");
								map.put("LEVEL", level);
								map.put("ETC", "");
								map.put("FILENAME", fileNm);
								map.put("INDEXGB", indexgb);
									
								resultMap.put(key, map);
                			}
                		}
                	}
	            }
	        }
	        bufReader.close();
	    }catch ( IOException e ) {
	        return false;
	    }
		
//		System.out.println("resultMap.size() ::"+resultMap.size());
		TreeMap<String, Map<String, String>> tm = new TreeMap<String, Map<String, String>>(resultMap);
		Iterator<String> iteratorKey = tm.keySet().iterator(); // 키값 오름차순 정렬(기본)
		List<Document> voList = new ArrayList<Document>();
		while (iteratorKey.hasNext()) {
			String key = iteratorKey.next();
			Document vo = new Document();
			if("".equals(tm.get(key).get("NUMBER1"))) {
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
		    vo.setIndexgb(tm.get(key).get("INDEXGB"));
		    vo.setDomain(domain);
//			if (tm.get(key).get("CONTENT").getBytes().length > 32000) {
//				System.out.println("==========초과  ====");
//				System.out.println(tm.get(key).get("CONTENT"));
//			}

			voList.add(vo);
		}

		if(voList.size() > 0) {
			if("Y".equals(memberGb)) {
				return htmIndexing.indexing(voList, domain, 3);
			}else {
				return htmIndexing.indexing(voList, domain, 1);
			}
		}else {
			return true;
		}
	}

}
