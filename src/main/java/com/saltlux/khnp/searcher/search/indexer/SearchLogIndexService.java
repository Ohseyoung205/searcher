package com.saltlux.khnp.searcher.search.indexer;

import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.khnp.searcher.search.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SearchLogIndexService {
	
	
	@Value("${in2.dor.host}")
	private String host;
	@Value("${in2.dor.port1}")
	private Integer port;
	@Value("${in2.dor.index.searchlog}")
	private String index_name;
	
	public void indexing(SearchVo vo){
		IN2StdIndexer in2StdIndexer = new IN2StdIndexer();
		in2StdIndexer.setServer(host, port);
		in2StdIndexer.setIndex(index_name);

		final String[] dt = vo.getCreateDt().split(" ")[0].split("-");
		final String yyymmdd = dt[0] + dt[1] + dt[2];

		in2StdIndexer.addSource("LOGID", vo.getLogId(), in2StdIndexer.SOURCE_TYPE_TEXT);
		in2StdIndexer.addSource("LOGKEYWORD", vo.getLogKeyword(), in2StdIndexer.SOURCE_TYPE_TEXT);
		in2StdIndexer.addSource("CREATEDT", vo.getCreateDt(), in2StdIndexer.SOURCE_TYPE_TEXT);
		in2StdIndexer.addSource("CLIENTIP", vo.getClientIp(), in2StdIndexer.SOURCE_TYPE_TEXT);
		in2StdIndexer.addSource("YYYYMMDD", yyymmdd, in2StdIndexer.SOURCE_TYPE_TEXT);

		in2StdIndexer.addFieldFTR("LOGID", "LOGID", in2StdIndexer.TOKENIZER_TERM, true, true);
		in2StdIndexer.addFieldFTR("LOGKEYWORD", "LOGKEYWORD", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
		in2StdIndexer.addFieldFTR("CREATEDT", "CREATEDT", in2StdIndexer.TOKENIZER_TERM, true, true);
		in2StdIndexer.addFieldFTR("CLIENTIP", "CLIENTIP", in2StdIndexer.TOKENIZER_TERM, true, true);
		in2StdIndexer.addFieldFTR("YYYYMMDD", "YYYYMMDD", in2StdIndexer.TOKENIZER_TERM, true, true);

		in2StdIndexer.addFieldTMS("TMS", "KOR", "LOGKEYWORD", true, 100);

		if(!in2StdIndexer.addDocument()){
			log.error("SEARCH LOG IDX FAIL : id=[{}], {}", vo.getLogId(), in2StdIndexer.getLastErrorMessage());
		}
	}

}
