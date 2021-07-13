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

import com.saltlux.khnp.searcher.indexer.config.Consts;
import com.saltlux.khnp.searcher.indexer.indexer.HtmIndexing;
import com.saltlux.khnp.searcher.indexer.vo.Document;
import com.saltlux.khnp.searcher.indexer.vo.IndexVo;

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
	
public IndexVo indexTarget(String domain, String fileNm, String indexName, IndexVo indexVo, int fileNum, String path, int documentId) throws Exception{
		
		String title1_pattern1 = ptnTitle1+Consts.TWOSPACE+".*"; // 1.0, 2.0 ,3.0 구분
		String title2_pattern1 = ptnTitle2+Consts.TWOSPACE+".*"; // 1.1, 1.2, 1.3 ...2.1
		String title3_pattern1 = ptnTitle3+Consts.TWOSPACE+".*"; // 1.1.0,1.1.1
		
		Map<String, String> map = null;
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
			
		String readLine = null ;
	    int lineNum = 0;			//라인 넘버
	    
	    Pattern tags = Pattern.compile("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>");
	    Pattern ptns1 = Pattern.compile("<IMG src=.*?>(.*?)"); //이미지 패턴
	    Pattern ptns2 = Pattern.compile("^그림 [1-9.-]{2,8}");
	    Pattern ptns3 = Pattern.compile(ptnPtns1);
	    Pattern ptns4 = Pattern.compile(ptnPtns2);
	    
	    Matcher m;
		boolean saveBln = false;
		int numberChk = 0;
		String orderNum = "";
		
		indexVo.setStartLine(0);
		indexVo.setImgLineNum(0);
		indexVo.setNumber1("");
	    indexVo.setNumber2("");
	    indexVo.setNumber3("");
	    indexVo.setNumber4("");
	    indexVo.setLevel("");
		
		try{
			File file = new File(htmFilePath+File.separator+path+File.separator+fileNm);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

	        while((readLine =  bufReader.readLine()) != null ){
	            map = new HashMap<String, String>();
	            lineNum++;
	            if("<BODY>".equals(readLine)) {
	            	indexVo.setStartNum(lineNum);
	            }
	            	
	            orderNum= fileNum+String.format("%06d", lineNum);
	            if(indexVo.getStartNum() > 0) {
	            	
	            	saveBln = false;
	            	//######## 이미지 #############
	            	m = tags.matcher(readLine); //태그삭제
	            	indexVo.setTmpStr(m.replaceAll(""));
	            	
	            	indexVo.setTagStr(m.replaceAll("").replaceAll("&nbsp;", " ").replaceAll("&#9552;", ""));
	            	
	          
	            	if(indexVo.getTagStr().matches(title1_pattern1)) {			//1 단계
	            		indexVo.setNumber1(indexVo.getTagStr().split(" ")[0]);
	            		if("B".equals(indexVo.getNumber1())) {
	            			indexVo.setNumber1(indexVo.getTagStr().split(" ")[0]+" "+indexVo.getTagStr().split(" ")[1]);
	            		}
           				indexVo.setNumber2("");
           				indexVo.setNumber3("");
           				indexVo.setLevel("1");
           				indexVo.setStartLine(lineNum);
               		}else if(indexVo.getTagStr().matches(title2_pattern1)) {		//2 단계
               			indexVo.setNumber2(indexVo.getTagStr().split(" ")[0]);
               			if("B".equals(indexVo.getNumber2())) {
               				indexVo.setNumber2(indexVo.getTagStr().split(" ")[0]+" "+indexVo.getTagStr().split(" ")[1]);
               			}
           				indexVo.setNumber3("");
            			indexVo.setLevel("2");
            			indexVo.setStartLine(lineNum);
               		}else if(indexVo.getTagStr().matches(title3_pattern1)) {		//3 단계
               			indexVo.setNumber3(indexVo.getTagStr().split(" ")[0]);
               			if("B".equals(indexVo.getNumber3())) {
               				indexVo.setNumber3(indexVo.getTagStr().split(" ")[0]+" "+indexVo.getTagStr().split(" ")[1]);
               			}
           				indexVo.setLevel("3");
           				indexVo.setStartLine(lineNum);
               		}
	            	
	            	m = ptns1.matcher(readLine);
	            	while (m.find()) {
	            		indexVo.setImgLineNum(lineNum);
	            		indexVo.setImgChk("chk");
	            	}
	            	if("chk".equals(indexVo.getImgChk())) {
	            		m = ptns2.matcher(indexVo.getTmpStr());
		            	while (m.find()){
		            		indexVo.setTmpStr1(m.group(0));
		            		indexVo.setTmpStr(indexVo.getTmpStr().replaceAll("&nbsp;", ""));
		            		if(indexVo.getTmpStr().contains(indexVo.getTmpStr1())) {
		            			indexVo.setImgChk("");
		            			saveBln = true;
		            			indexVo.setTitle1(indexVo.getTmpStr1());
			            		indexVo.setStartLine(indexVo.getImgLineNum());
		            		}
		            	}
	            	}
	            	//##########  표, 운전제한조건 ###############
	            	m = ptns3.matcher(indexVo.getTmpStr());
	            	while (m.find()) {
	            		indexVo.setTmpStr1(m.group(0));
	            		indexVo.setTmpStr(indexVo.getTmpStr().replaceAll("&nbsp;", ""));
	            		if(indexVo.getTmpStr().contains(indexVo.getTmpStr1())) {
	            			saveBln = true;
		            		indexVo.setTitle1(indexVo.getTmpStr1());
		            		indexVo.setStartLine(lineNum);
	            		}
	            	}
	            	
	            	//########## 점검요구사항  #############
	            	m = ptns4.matcher(indexVo.getTmpStr().trim());
	            	while (m.find()) {
	            		if(!indexVo.getTmpStr().contains("(계속)")) {
	            			indexVo.setTmpStr1(m.group(0));
	            			if(indexVo.getTmpStr().contains("&nbsp;&nbsp;")) {
			            		saveBln = true;
			            		indexVo.setTitle1(indexVo.getTmpStr1());
			            		indexVo.setStartLine(lineNum);
	            			}else if(!indexVo.getTmpStr().contains("&nbsp;")){
	            				if(indexVo.getTmpStr().length() < 20) {
				            		saveBln = true;
				            		indexVo.setTitle1(indexVo.getTmpStr1());
				            		indexVo.setStartLine(lineNum);
	            				}
	            			}
	            		}
	            	}
	            	
	            	if("제 1 편".equals(indexVo.getTagStr())) {
        				indexVo.setIndexgb("index");
        				indexVo.setBigTitleGb("1");
       				}else if("제 2 편".equals(indexVo.getTagStr())) {
       					indexVo.setIndexgb("index");
       					indexVo.setBigTitleGb("2");
       				}else if("제 3 편".equals(indexVo.getTagStr())) {
       					indexVo.setIndexgb("index");
       					indexVo.setBigTitleGb("3");
       				}else if("B 2.0".equals(indexVo.getNumber1())) {
       					indexVo.setIndexgb("index");
        				indexVo.setBigTitleGb("1");
       				}else if("B 3.0".equals(indexVo.getNumber2())) {
       					indexVo.setIndexgb("index");
        				indexVo.setBigTitleGb("1");
       				}
    				
    				if("".equals(indexVo.getBigTitleGb())) {
    					indexVo.setIndexgb("index");
        				indexVo.setBigTitleGb("1");
        			}      
	            	
	            	
	            	
	            	if("".equals(indexVo.getNumber1())) {
        				if(!"".equals(indexVo.getNumber2())) {
        					indexVo.setNumber1(indexVo.getNumber2().substring(0, indexVo.getNumber2().length()-1)+"0");
        				}
        			}

	            	if(saveBln) {
	            		numberChk++;
	            		String key = "";
		            	if("1".equals(indexVo.getBigTitleGb())) {
	        				key = "KEY_"+Consts.BIG_TITLE1+indexVo.getTitle1()+indexVo.getStartLine();
	       				}else if("2".equals(indexVo.getBigTitleGb())) {
	       					key = "KEY_"+Consts.BIG_TITLE2+indexVo.getTitle1()+indexVo.getStartLine();
	       				}else if("3".equals(indexVo.getBigTitleGb())) {
	       					key = "KEY_"+Consts.BIG_TITLE2+indexVo.getTitle1()+indexVo.getStartLine();
	       				}
		            	
	            		
	            		if("1".equals(indexVo.getBigTitleGb())) {
       						map.put("TITLE0", Consts.BIG_TITLE1);
       					}else if("2".equals(indexVo.getBigTitleGb())) {
       						map.put("TITLE0", Consts.BIG_TITLE2);
       					}else if("3".equals(indexVo.getBigTitleGb())) {
       						map.put("TITLE0", Consts.BIG_TITLE3);
       					}
	            		
       					map.put("TITLE1", indexVo.getTitle1().trim());
       					map.put("TITLE2", "");
       					map.put("TITLE3", "");
       					map.put("TITLE4", "");
       					map.put("TITLE4_1", "");
       					
       					if("1".equals(indexVo.getBigTitleGb())) {
       						map.put("NUMBER0", Consts.BIG_NUMBER1);
       					}else if("2".equals(indexVo.getBigTitleGb())) {
       						map.put("NUMBER0", Consts.BIG_NUMBER2);
       					}else if("3".equals(indexVo.getBigTitleGb())) {
       						map.put("NUMBER0", Consts.BIG_NUMBER3);
       					}
				
						map.put("NUMBER1", indexVo.getNumber1());
						map.put("NUMBER2", indexVo.getNumber2());
						map.put("NUMBER3", indexVo.getNumber3());
						map.put("NUMBER4", "");
						map.put("CONTENT", indexVo.getTitle1().trim());
						map.put("STARTLINE", indexVo.getStartLine()+"");
						map.put("ENDLINE", lineNum+"");
						map.put("SORT", indexVo.getStartLine()+"");
						map.put("LEVEL", indexVo.getLevel());
						map.put("ETC", "");
						map.put("FILENAME", fileNm);
						map.put("ORDERNUM", orderNum);
							
						resultMap.put(key, map);
	            	}
	            }
	        }
	        bufReader.close();
	    }catch ( IOException e ) {
	    	indexVo.setBln(false);
	        return indexVo;
	    }
		
		TreeMap<String, Map<String, String>> tm = new TreeMap<String, Map<String, String>>(resultMap);
		Iterator<String> iteratorKey = tm.keySet().iterator(); 
		List<Document> voList = new ArrayList<Document>();
		while (iteratorKey.hasNext()) {
			String key = iteratorKey.next();
			Document vo = new Document();
			if(!"".equals(tm.get(key).get("NUMBER1").trim())) {
				vo.setContent(tm.get(key).get("CONTENT"));
				vo.setEtc(tm.get(key).get("ETC"));
				vo.setPosition(tm.get(key).get("STARTLINE")+"-"+tm.get(key).get("ENDLINE"));
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
			    vo.setOrderNum(Long.parseLong(tm.get(key).get("ORDERNUM")));
			    vo.setUuid(path);
				vo.setDocumentId(String.valueOf(documentId));	
				voList.add(vo);
			}
			
		}
		
		if(voList.size() > 0) {
			boolean bln = false;
			bln = htmIndexing.indexing(voList, indexName, 2);
			indexVo.setBln(bln);
			return indexVo;
		}else {
			indexVo.setBln(true);
			return indexVo;
		}
	}
}
