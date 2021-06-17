package com.saltlux.khnp.searcher.search.indexer;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.khnp.searcher.search.vo.SearchVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SearchLogIndexing {
	
	
	@Value("${in2.dor.host}")
	private String host;
	@Value("${in2.dor.port1}")
	private Integer port;
	@Value("${in2.dor.index.searchlog}")
	private String index_name;
	
	public boolean indexing(List<SearchVo> dataSet){
		IN2StdIndexer in2StdIndexer = new IN2StdIndexer();
		in2StdIndexer.setServer(host, port);
		
		boolean flag = false;
		Iterator<SearchVo> itr = dataSet.iterator();
		
		while (itr.hasNext()) {
			SearchVo data = itr.next();
	    	
	    	in2StdIndexer.newDocument();
	        in2StdIndexer.setIndex(index_name);
	        
	        String[] dt = data.getCreateDt().split(" ")[0].split("-");
	        String yyymmdd = dt[0]+dt[1]+dt[2];
	            
	        in2StdIndexer.addSource("LOGID", data.getLogId(), in2StdIndexer.SOURCE_TYPE_TEXT);
	        in2StdIndexer.addSource("LOGKEYWORD", data.getLogKeyword(), in2StdIndexer.SOURCE_TYPE_TEXT);
	        in2StdIndexer.addSource("CREATEDT", data.getCreateDt(), in2StdIndexer.SOURCE_TYPE_TEXT);
	        in2StdIndexer.addSource("CLIENTIP", data.getClientIp(), in2StdIndexer.SOURCE_TYPE_TEXT);
	        in2StdIndexer.addSource("YYYYMMDD", yyymmdd, in2StdIndexer.SOURCE_TYPE_TEXT);
		        
		    in2StdIndexer.addFieldFTR("LOGID", "LOGID", in2StdIndexer.TOKENIZER_TERM, true, true);
		    in2StdIndexer.addFieldFTR("LOGKEYWORD", "LOGKEYWORD", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
		    in2StdIndexer.addFieldFTR("CREATEDT", "CREATEDT", in2StdIndexer.TOKENIZER_TERM, true, true);
		    in2StdIndexer.addFieldFTR("CLIENTIP", "CLIENTIP", in2StdIndexer.TOKENIZER_TERM, true, true);
		    in2StdIndexer.addFieldFTR("YYYYMMDD", "YYYYMMDD", in2StdIndexer.TOKENIZER_TERM, true, true);
		    
	        in2StdIndexer.addFieldTMS("TMS", "KOR", "LOGKEYWORD", true, 100);

		    flag = in2StdIndexer.addDocument();
	        
	    }
		
		try {
	        if (flag == false)
	            log.info("TERMS IDX FAIL : " + in2StdIndexer.getLastErrorMessage());

	        return flag;

	    } catch (Exception e) {
	        log.error("TERMS IDX EXCEPTION FAIL : " + e.getMessage());
	        return false;
	    }
		
	}

}
