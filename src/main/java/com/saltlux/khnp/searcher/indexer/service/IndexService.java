package com.saltlux.khnp.searcher.indexer.service;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.saltlux.khnp.searcher.indexer.indexer.HtmIndexing;
import com.saltlux.khnp.searcher.indexer.vo.IndexVo;
import com.saltlux.khnp.searcher.search.model.DomainTable;
import com.saltlux.khnp.searcher.search.repository.DomainRepository;

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
	
	@Autowired
	private DomainRepository repository;
	
	@Value("${htm.file.path}")
    private String htmFilePath;
	
	
	public Map<String, String> indexService(String domain) throws Exception{
		Map<String, String> map = new HashMap<>();

		List<DomainTable>  doaminList = repository.findByDomainList(domain);
		DomainTable domainTable = new DomainTable();
		String indexDomain = "KHNP_DOCUMENT";
		String indexgb = "";
		if(domain.contains("1,2")) {
			indexgb="1";
		}else if(domain.contains("3,4")) {
			indexgb="2";
		}else if(domain.contains("5,6")) {
			indexgb="3";
		}
		
		if(domain.contains("고리")) {
			indexDomain = indexDomain+"1"+indexgb;
		}else if(domain.contains("월성")) {
			indexDomain = indexDomain+"2"+indexgb;
		}else if(domain.contains("한빛")) {
			indexDomain = indexDomain+"3"+indexgb;
		}else if(domain.contains("한울")) {
			indexDomain = indexDomain+"4"+indexgb;
		}else if(domain.contains("신한울")) {
			indexDomain = indexDomain+"7"+indexgb;
		}else if(domain.contains("새울")) {
			indexDomain = indexDomain+"8"+indexgb;
		}
		

		if(doaminList.size() == 0) {
			domainTable.setDomainId(0);
			domainTable.setName(domain);
			domainTable.setIndexName(indexDomain+String.format("%05d", 0));
			domainTable = repository.save(domainTable);
		}else {
			domainTable.setDomainId(0);
			domainTable.setName(domain);
			domainTable.setIndexName(indexDomain+String.format("%05d", doaminList.get(0).getDomainId()));
			domainTable = repository.save(domainTable);
		}
		
		if(domainTable.getIndexName() != null && !"".equals(domainTable.getIndexName())) {
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
				IndexVo htmVo = new IndexVo();
				IndexVo tarVo = new IndexVo();
				if(htmChange.indexHtmChange(domain, htmFileName)) {
					String[] fileNm = htmFileName.split(";");
					Arrays.sort(fileNm);
					for(int i=0;i<fileNm.length; i++) {
						System.out.println(fileNm[i]+"파일 색인중....");
						htmVo = indexHtm.indexHtm(domain, fileNm[i], domainTable.getIndexName(), htmVo, (i+1));
						tarVo = indexTarget.indexTarget(domain, fileNm[i], domainTable.getIndexName(), tarVo, (i+1));
						System.out.println("htmVo.isBln() ::"+htmVo.isBln());
						if(!htmVo.isBln() || !tarVo.isBln()) {
							map.put("errorYn", "Y");
							map.put("msg", fileNm[i]+" 파일 에서 오류가 발생하였습니다.");
							break;
						}
					}
					if(!htmVo.isBln() || !tarVo.isBln()) {
						return map;
					}else {
						List<DomainTable>  doaminList1 = repository.findByDomainList(domain);
						if(doaminList1.size() > 1) {
							htmIndexing.dropIndex(doaminList1.get(1).getIndexName());
							DomainTable delDomain = new DomainTable();
							delDomain = doaminList1.get(1);
							
//							System.out.println("delDomain.getDomainId() ::"+delDomain.getDomainId()+" || delDomain.getIndexName() ::"+delDomain.getIndexName());
							repository.delete(delDomain);
						}
					}
					
					
					Thread.sleep(1000);
					
					
					for(int i=0; i<fileNm.length; i++) {
						targetIndexService.targetIndex(domain, fileNm[i], domainTable.getIndexName());
					}
					map.put("errorYn", "N");
					map.put("msg", "색인및 htm 변환을 완료하였습니다.");
				}else {
					map.put("errorYn", "Y");
					map.put("msg", "htm 변환 중 오류가 발생하였습니다.");
					return map;
				}
			}
		}else {
			map.put("errorYn", "Y");
			map.put("msg", "색인 테이블 저장중 오류가 발생하였습니다.");
		}
		
		System.out.println("완료");
		return map;
	}
	
	
	
}
