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
	
	@Value("${pattern1.tags0}")
	private String tags0;
	@Value("${pattern1.ptns0}")
	private String ptns0;
	
	@Value("${pattern4.ptns1}")
	private String ptnPtns5;
	
	public IndexVo indexHtm(String domain, String fileNm, String indexName, IndexVo indexVo, int fileNum, String path, int documentId) throws Exception{
	
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
	    Pattern tags = Pattern.compile(tags0);
	    Pattern ptns = Pattern.compile(ptns0);
	    Pattern ptns5 = Pattern.compile(ptnPtns5); //색인 시작시점 찾는 패턴
	    Matcher m;
	    
	    indexVo.setStartLine(0);
	    indexVo.setNumber1("");
	    indexVo.setNumber2("");
	    indexVo.setNumber3("");
	    indexVo.setNumber4("");
	    indexVo.setLevel("");
	    String orderNum = "";
		
		try{
			File file = new File(htmFilePath+File.separator+path+File.separator+fileNm);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

	        while((readLine =  bufReader.readLine()) != null ){
	            map = new HashMap<String, String>();
	            lineNum++;
	            
	            m = ptns5.matcher(readLine);
	            while (m.find()) {
	            	indexVo.setStartNum(lineNum);
	            }
	            
	            if(indexVo.getStartNum() > 0) {
	            	indexVo.setTmpStr(readLine);
	            	orderNum= fileNum+String.format("%06d", lineNum);
	            	m = ptns.matcher(readLine);
	            	indexVo.setTmpStr1("");
	            	while (m.find()) {
	            		indexVo.setTmpStr1(m.group(0));
	            	}
	            	indexVo.setTmpStr(indexVo.getTmpStr().replace(indexVo.getTmpStr1(), ""));
	            	indexVo.setTmpStr1(tags.matcher(indexVo.getTmpStr1()).replaceAll("").replace("&nbsp;", ""));
	            		
	            	m = tags.matcher(indexVo.getTmpStr());
	                indexVo.setTagStr(m.replaceAll("").replace("&nbsp;", " ").replace("&#9552;", ""));
                	if(!indexVo.getTagStr().trim().matches(except_pattern1) && !indexVo.getTagStr().trim().matches(except_pattern2) && !indexVo.getTagStr().trim().matches(except_pattern3)
                			&& !indexVo.getTagStr().trim().matches(except_pattern4) && !indexVo.getTagStr().trim().matches(except_pattern5) && !indexVo.getTagStr().trim().matches(except_pattern6)
                			&& !indexVo.getTagStr().trim().matches(except_pattern7) && !indexVo.getTagStr().trim().matches(except_pattern8) && !indexVo.getTagStr().trim().matches(except_pattern9)
                			&& !indexVo.getTagStr().trim().matches(except_pattern10) && !indexVo.getTagStr().trim().matches(except_pattern11)) {
                		
                		if(indexVo.getTagStr().matches(title1_pattern1)) {			//1 단계
                			if(!indexVo.getBefo1().equals(indexVo.getTagStr().trim())) {
                   				indexVo.setNumber1(indexVo.getTagStr().split(" ")[0]);
                   				if("B".equals(indexVo.getNumber1())) {
                   					indexVo.setMemberGb("Y");
                   					indexVo.setNumber1(indexVo.getTagStr().split(" ")[0]+" "+indexVo.getTagStr().split(" ")[1]);
                   				}
                   				indexVo.setNumber2("");
                   				indexVo.setNumber3("");
                   				indexVo.setTitle1(indexVo.getTagStr().replace(indexVo.getNumber1(), ""));
                   				indexVo.setTitle2("");
                   				indexVo.setTitle3("");
                   				indexVo.setLevel("1");
                   				indexVo.setIndexgb("index");
                   				indexVo.setStartLine(lineNum);
                   				indexVo.setOrderNum(orderNum);
                   				indexVo.setBefo1(indexVo.getTagStr().trim());
                   			}
                   		}else if(indexVo.getTagStr().matches(title2_pattern1)) {		//2 단계
                   			if(!indexVo.getBefo2().equals(indexVo.getTagStr().trim())) {
                   				indexVo.setNumber2(indexVo.getTagStr().split(" ")[0]);
                   				if("B".equals(indexVo.getNumber2())) {
                   					indexVo.setMemberGb("Y");
                   					indexVo.setNumber2(indexVo.getTagStr().split(" ")[0]+" "+indexVo.getTagStr().split(" ")[1]);
                   				}
                   				indexVo.setNumber3("");
                    			indexVo.setTitle2(indexVo.getTagStr().replace(indexVo.getNumber2(), ""));
                    			indexVo.setTitle3("");
                    			indexVo.setLevel("2");
                    			indexVo.setIndexgb("index");
                    			indexVo.setStartLine(lineNum);
                    			indexVo.setOrderNum(orderNum);
                    			indexVo.setBefo2(indexVo.getTagStr().trim());
                    		}
                   		}else if(indexVo.getTagStr().matches(title3_pattern1)) {		//3 단계
                   			indexVo.setTmpNumber(indexVo.getTagStr().split(" ")[0]);
                   			if(!"2.2.1".equals(indexVo.getTmpNumber())) {
                   				if(!indexVo.getBefo3().equals(indexVo.getTagStr().trim())) {
                       				indexVo.setNumber3(indexVo.getTagStr().split(" ")[0]);
                       				if("B".equals(indexVo.getNumber3())) {
                       					indexVo.setMemberGb("Y");
                       					indexVo.setNumber3(indexVo.getTagStr().split(" ")[0]+" "+indexVo.getTagStr().split(" ")[1]);
                       				}
                       				indexVo.setTitle3(indexVo.getTagStr().replace(indexVo.getNumber3(), ""));
                       				indexVo.setLevel("3");
                       				indexVo.setIndexgb("index");
                       				indexVo.setStartLine(lineNum);
                       				indexVo.setOrderNum(orderNum);
                       				indexVo.setBefo3(indexVo.getTagStr().trim());
                       			}
                   			}
                   		}
                			
               			if(!indexVo.getTagStr().matches(title1_pattern1) && !indexVo.getTagStr().matches(title2_pattern1)
               					&& !indexVo.getTagStr().matches(title3_pattern1)) {
               				if(!"".equals(indexVo.getTmpStr1()) && !"|".equals(indexVo.getTagStr())) {
                				if(!"1.".equals(indexVo.getTmpStr1().trim()) && !"2.".equals(indexVo.getTmpStr1().trim()) && !"3.".equals(indexVo.getTmpStr1().trim()) 
                						&& !"운전제한조건 3.4.10".equals(indexVo.getTmpStr1().trim())) {
                					if(!"1".equals(indexVo.getTmpGb())) {
                    					indexVo.setTmpStr2(indexVo.getTmpStr1().replace("(계속)", "").trim());
                    					indexVo.setTmpStr2(indexVo.getNumber1()+indexVo.getNumber2()+indexVo.getNumber3()+indexVo.getTmpStr2());
                    				}
                					indexVo.setIndexgb("target");
                    				indexVo.setStartLine(lineNum);
                    				indexVo.setTmpGb("1");
                				}
                			}else {
                				indexVo.setIndexgb("index");
                				indexVo.setTmpGb("");
                			}
               				
                			indexVo.setRealStr(indexVo.getTagStr().replace("|", "\r"));
                			
                			if("".equals(indexVo.getNumber1())) {
                				indexVo.setIndexgb("index");
                				if(!"".equals(indexVo.getNumber2())) {
                					indexVo.setNumber1(indexVo.getNumber2().substring(0, indexVo.getNumber2().length()-1)+"0");
                				}
                			}
            
                			if("".equals(indexVo.getIndexgb())) {
                				if("B".equals(indexVo.getNumber1().split(" ")[0])) {
                					indexVo.setIndexgb("target");
                					indexVo.setMemberGb("Y");
                				}
                			}
                			
                			if(!"".equals(indexVo.getRealStr().trim())) {
               					indexVo.setStrStart("start");
               				}
                			
                			if(!"".equals(indexVo.getStrStart())) {
                				if("제 1 편".equals(indexVo.getRealStr())) {
                    				indexVo.setIndexgb("index");
                    				indexVo.setBigTitleGb("1");
                   				}else if("제 2 편".equals(indexVo.getRealStr())) {
                   					indexVo.setIndexgb("index");
                   					indexVo.setBigTitleGb("2");
                   				}else if("제 3 편".equals(indexVo.getRealStr())) {
                   					indexVo.setIndexgb("index");
                   					indexVo.setBigTitleGb("3");
                   				}else if("B 2.0".equals(indexVo.getNumber1())) {
                   					indexVo.setIndexgb("index");
                    				indexVo.setBigTitleGb("1");
                   				}else if("B 3.0".equals(indexVo.getNumber1())) {
                   					indexVo.setIndexgb("index");
                    				indexVo.setBigTitleGb("1");
                   				}
                				
                				
                				if("".equals(indexVo.getBigTitleGb())) {
                					indexVo.setIndexgb("index");
                    				indexVo.setBigTitleGb("1");
                    			} 
                	

                    			String key = "";
                				if("1".equals(indexVo.getBigTitleGb())) {
                					key = "KEY_"+Consts.BIG_TITLE1+indexVo.getNumber1()+indexVo.getTitle1()+indexVo.getNumber2()+indexVo.getTitle2()+indexVo.getNumber3()+indexVo.getTitle3()+indexVo.getTmpStr2();
                							;
               					}else if("2".equals(indexVo.getBigTitleGb())) {
               						key = "KEY_"+Consts.BIG_TITLE2+indexVo.getNumber1()+indexVo.getTitle1()+indexVo.getNumber2()+indexVo.getTitle2()+indexVo.getNumber3()+indexVo.getTitle3()+indexVo.getTmpStr2();
               					}else if("3".equals(indexVo.getBigTitleGb())) {
               						key = "KEY_"+Consts.BIG_TITLE3+indexVo.getNumber1()+indexVo.getTitle1()+indexVo.getNumber2()+indexVo.getTitle2()+indexVo.getNumber3()+indexVo.getTitle3()+indexVo.getTmpStr2();
               					}
                				if (resultMap.containsKey(key)) {
                    				indexVo.setContent(resultMap.get(key).get("CONTENT"));
                    				indexVo.setStrTitle(resultMap.get(key).get("TITLE4_1"));
                    				resultMap.get(key).put("CONTENT",indexVo.getContent() + indexVo.getRealStr());
                    				indexVo.setTmpStr1(indexVo.getTmpStr1().replace("(계속)", ""));
                    				if(!indexVo.getStrTitle().trim().equals(indexVo.getTmpStr1().trim())) {
                    					if(!"".equals(indexVo.getTmpStr1())) {
                    						if(!"1.".equals(indexVo.getTmpStr1().trim()) && !"2.".equals(indexVo.getTmpStr1().trim()) && !"3.".equals(indexVo.getTmpStr1().trim())
                    								&& !"운전제한조건 3.4.10".equals(indexVo.getTmpStr1().trim())) {
                    							resultMap.get(key).put("TITLE4_1", indexVo.getStrTitle()+" "+indexVo.getTmpStr1());
                    						}
                    					}
                    				}
                   					resultMap.get(key).put("ENDLINE", lineNum+"");
                   				}else {
                   					if("1".equals(indexVo.getBigTitleGb())) {
                   						map.put("TITLE0", Consts.BIG_TITLE1);
                   					}else if("2".equals(indexVo.getBigTitleGb())) {
                   						map.put("TITLE0", Consts.BIG_TITLE2);
                   					}else if("3".equals(indexVo.getBigTitleGb())) {
                   						map.put("TITLE0", Consts.BIG_TITLE3);
                   					}
                   					
                   					map.put("TITLE1", indexVo.getTitle1().trim());
                   					map.put("TITLE2", indexVo.getTitle2().trim());
                   					map.put("TITLE3", indexVo.getTitle3().trim());
                   					map.put("TITLE4", "");
                   					if(!"1.".equals(indexVo.getTmpStr1().trim()) && !"2.".equals(indexVo.getTmpStr1().trim()) && !"3.".equals(indexVo.getTmpStr1().trim())
                   							&& !"운전제한조건 3.4.10".equals(indexVo.getTmpStr1().trim())) {
                   						map.put("TITLE4_1", indexVo.getTmpStr1().replace("(계속)", "").trim());
                   						
                   					}else {
                   						map.put("TITLE4_1", "");
                   					}
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
    								map.put("CONTENT", indexVo.getRealStr());
    								map.put("STARTLINE", indexVo.getStartLine()+"");
    								map.put("ENDLINE", lineNum+"");
    								map.put("LEVEL", indexVo.getLevel());
    								map.put("ETC", "");
    								map.put("FILENAME", fileNm);
    								map.put("INDEXGB", indexVo.getIndexgb());
    								map.put("ORDERNUM", indexVo.getOrderNum());
    								map.put("MENUBERGB", indexVo.getMemberGb());
    									
    								resultMap.put(key, map);
                    			}
                    		}
                		}
                			
                	}
	            }
	        }
	        bufReader.close();
	    }catch ( IOException e ) {
	    	indexVo.setBln(false);
	        return indexVo;
	    }
		
		TreeMap<String, Map<String, String>> tm = new TreeMap<String, Map<String, String>>(resultMap);
		Iterator<String> iteratorKey = tm.keySet().iterator(); // 키값 오름차순 정렬(기본)
		List<Document> voList = new ArrayList<Document>();
		String[] uuid = path.split("/");

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
			    if("".equals(vo.getTitle4_1())) {
			    	vo.setIndexgb(tm.get(key).get("INDEXGB"));
			    }else {
			    	vo.setIndexgb("target");
			    }
			    
			    vo.setDomain(domain);
			    vo.setOrderNum(Long.parseLong(tm.get(key).get("ORDERNUM")));
			    vo.setUuid(uuid[uuid.length-1]);
				vo.setDocumentId(String.valueOf(documentId));	
				vo.setMemberGb(tm.get(key).get("MENUBERGB"));
				voList.add(vo);
			}
		}

		if(voList.size() > 0) {
			boolean bln = false;
			bln = htmIndexing.indexing(voList, indexName, 3);
			indexVo.setBln(bln);
			return indexVo;
		}else {
			indexVo.setBln(false);
			return indexVo;
		}
	}

}
