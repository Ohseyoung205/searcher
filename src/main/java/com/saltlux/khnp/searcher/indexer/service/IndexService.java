package com.saltlux.khnp.searcher.indexer.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.saltlux.khnp.searcher.indexer.indexer.HtmIndexing;
import com.saltlux.khnp.searcher.indexer.vo.IndexVo;
import com.saltlux.khnp.searcher.search.model.DomainTable;
import com.saltlux.khnp.searcher.search.model.PlantOperationDocument;
import com.saltlux.khnp.searcher.search.repository.DomainRepository;
import com.saltlux.khnp.searcher.search.repository.PlantOperationDocumentRepository;
import com.saltlux.khnp.searcher.search.vo.IntegrationSearchResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	@Autowired
	private PlantOperationDocumentRepository plantRepository;
	
	@Value("${htm.file.path}")
    private String htmFilePath;
	
	
	public IntegrationSearchResult indexService(String documentId, String path) throws Exception{
		HashMap<String, String> map = new HashMap<>();
		List<HashMap<String, String>> documents = new ArrayList<>();
		
		Optional<PlantOperationDocument> optional = plantRepository.findByDocumentId(Integer.valueOf(documentId));
		
		if(!optional.isPresent() || optional.get().getDomainTable() == null){
			new IntegrationSearchResult(new ArrayList<>(), 0);
		}
		
		List<DomainTable>  doaminList = repository.findAll(Sort.by(Sort.Direction.DESC,"domainId"));
		
		System.out.println("doaminList.get(0).getDomainId() ::"+doaminList.get(0).getDomainId());
		DomainTable domainTable = new DomainTable();
		String indexDomain = "KHNP_DOCUMENT";
		indexDomain = indexDomain+optional.get().getDocumentId();
		
		if(doaminList.size() == 0) {
			domainTable.setDomainId(1);
			domainTable.setUuid(path);
			domainTable.setRecYn(true);
			domainTable.setCreateDt(new Date());
			domainTable.setIndexName(indexDomain+String.format("%05d", 0));
			domainTable = repository.save(domainTable);
			
		}else {
			domainTable.setDomainId(doaminList.get(0).getDomainId()+1);
			domainTable.setUuid(path);
			domainTable.setRecYn(true);
			domainTable.setCreateDt(new Date());
			domainTable.setIndexName(indexDomain+String.format("%05d", doaminList.get(0).getDomainId()));
			domainTable = repository.save(domainTable);
		}
		
		if(domainTable.getIndexName() != null && !"".equals(domainTable.getIndexName())) {
			File rw = new File(htmFilePath+File.separator+path);
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
				if(htmChange.indexHtmChange(path, htmFileName)) {
					String[] fileNm = htmFileName.split(";");
					Arrays.sort(fileNm);
					for(int i=0;i<fileNm.length; i++) {
						System.out.println(fileNm[i]+"파일 색인중....");
						htmVo = indexHtm.indexHtm(optional.get().getName(), fileNm[i], domainTable.getIndexName(), htmVo, (i+1), path);
						tarVo = indexTarget.indexTarget(optional.get().getName(), fileNm[i], domainTable.getIndexName(), tarVo, (i+1), path);

						if(!htmVo.isBln() || !tarVo.isBln()) {
							map.put("errorYn", "Y");
							map.put("msg", fileNm[i]+" 파일 에서 오류가 발생하였습니다.");
							break;
						}
					}
					if(!htmVo.isBln() || !tarVo.isBln()) {
						htmIndexing.dropIndex(domainTable.getIndexName());
						documents.add(map);
						return new IntegrationSearchResult(documents, 0);
					}else {
//						List<DomainTable>  doaminList1 = repository.findByDomainList(path);
//						if(doaminList1.size() > 1) {
//							htmIndexing.dropIndex(doaminList1.get(1).getIndexName());
//							DomainTable delDomain = new DomainTable();
//							delDomain = doaminList1.get(1);
//							repository.delete(delDomain);
//						}
						PlantOperationDocument documentVo = new PlantOperationDocument();
						documentVo = optional.get();
						documentVo.setDomainTable(domainTable);
						plantRepository.save(documentVo);
					}
					
					
					Thread.sleep(1000);
					
					
					for(int i=0; i<fileNm.length; i++) {
						targetIndexService.targetIndex(path, fileNm[i], domainTable.getIndexName());
					}
					map.put("errorYn", "N");
					map.put("msg", "색인및 htm 변환을 완료하였습니다.");
					documents.add(map);
				}else {
					map.put("errorYn", "Y");
					map.put("msg", "htm 변환 중 오류가 발생하였습니다.");
					documents.add(map);
					return new IntegrationSearchResult(documents, 0);
				}
			}
		}else {
			map.put("errorYn", "Y");
			map.put("msg", "색인 테이블 저장중 오류가 발생하였습니다.");
			documents.add(map);
		}
		
		return new IntegrationSearchResult(documents, 0);
	}
	
	
	
}
