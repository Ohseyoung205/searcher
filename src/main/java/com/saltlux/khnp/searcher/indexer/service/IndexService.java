package com.saltlux.khnp.searcher.indexer.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.saltlux.khnp.searcher.indexer.indexer.HtmIndexing;

@Service
public class IndexService {

	@Autowired
	IndexHtmChange htmChange;
	
	@Autowired
	TargetIndexService targetIndexService;
	
	@Autowired
	IndexHtm indexHtm;
	
	@Autowired
	IndexTarget indexTarget;
	
	@Autowired
	HtmIndexing htmIndexing;
	
	@Value("${htm.file.path}")
    private String htmFilePath;
	
	
	public Map<String, String> indexService(String domain) throws Exception{
		Map<String, String> map = new HashMap<>();
		
		File rw = new File(htmFilePath+File.separator+domain);
		File [] fileList = rw.listFiles();
		String htmFileName = "";
		String fileName = "";
		for(File file : fileList) {
	      if(file.isFile()) {
	    	  fileName = file.getName();
	    	  if(fileName.contains(".htm")) {
	        	 if(!fileName.contains("index_")) {
	        		 if("".equals(htmFileName)) {
	        			 htmFileName = fileName;
	        		 }else {
	        			 htmFileName = htmFileName+";"+fileName;
	        		 }
	        	 }
	         }
	      }
		}
		
		if(!"".equals(htmFileName)) {
			boolean blnErr1 = true;
			boolean blnErr2 = true;
			if(htmChange.indexHtmChange(domain, htmFileName)) {
				String[] fileNm = htmFileName.split(";");
				for(int i=0;i<fileNm.length; i++) {
					System.out.println(fileNm[i]+"파일 색인중....");
					//blnErr1 = indexHtm.indexHtm(domain, fileNm[i]);
					//blnErr2 = indexTarget.indexTarget(domain, fileNm[i]);
					if(!blnErr1 || !blnErr2) {
						map.put("errorYn", "Y");
						map.put("msg", fileNm[i]+" 파일 에서 오류가 발생하였습니다.");
						break;
					}
				}
				if(!blnErr1 || !blnErr2) {
					return map;
				}else {
					//htmIndexing.indexingYn(domain);
				}
				
				Thread.sleep(1000);
				
				
				for(int i=0; i<fileNm.length; i++) {
					targetIndexService.targetIndex(domain, fileNm[i]);
				}
				map.put("errorYn", "N");
				map.put("msg", "색인및 htm 변환을 완료하였습니다.");
//				return map;
			}else {
				map.put("errorYn", "Y");
				map.put("msg", "htm 변환 중 오류가 발생하였습니다.");
				return map;
			}
		}
		System.out.println("완료");
		return map;
	}
	
	
	
}
