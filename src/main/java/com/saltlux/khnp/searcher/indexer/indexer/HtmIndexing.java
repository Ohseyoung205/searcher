package com.saltlux.khnp.searcher.indexer.indexer;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.saltlux.dor.api.IN2IndexManager;
import com.saltlux.dor.api.IN2StdCommander;
import com.saltlux.dor.api.IN2StdDeleter;
import com.saltlux.dor.api.IN2StdFieldUpdater;
import com.saltlux.dor.api.IN2StdIndexer;
import com.saltlux.khnp.searcher.indexer.vo.Document;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HtmIndexing {

	 @Value("${in2.dor.port1}")
	 private int SERVER_PORT;
	 
	 @Value("${in2.dor.host}")
	 private String SERVER_IP;
	 
	 public boolean dropIndex(String indexName) {
		 IN2StdCommander deleter = new IN2StdCommander();
	    	deleter.setSocketTimeOut(1000);
	    	deleter.newQuery();
	    	deleter.setServer(SERVER_IP, SERVER_PORT);
	    return deleter.dropIndex(indexName, "admin", "");
	 }
	 
	 
	 public boolean indexing(List<Document> dataSet, String indexName, int tmpInt) {

			IN2StdIndexer in2StdIndexer = new IN2StdIndexer();
		    in2StdIndexer.setServer(SERVER_IP, SERVER_PORT);

		    boolean flag = false;
		    Iterator<Document> itr = dataSet.iterator();
		       
		    while (itr.hasNext()) {
		    	Document data = itr.next();
		         	
		    	in2StdIndexer.newDocument();
		        in2StdIndexer.setIndex(indexName);

//			        if(data.getContent().getBytes().length > 32000) {
//			        	System.out.println("========초");
//			        	data.setContent("======================");
//			        }

				String title = Stream.of(data.getTitle0(), data.getTitle1(), data.getTitle2(), data.getTitle3(), data.getTitle4(), data.getTitle4_1())
						.filter(s -> StringUtils.isNotEmpty(s))
						.collect(Collectors.joining(">"));
				String number = StringUtils.isNotEmpty(data.getNumber3()) ? data.getNumber3() : (StringUtils.isNotEmpty(data.getNumber2()) ? data.getNumber2() : data.getNumber1());
		        in2StdIndexer.addSource("TITLE", title, in2StdIndexer.SOURCE_TYPE_TEXT);
		        in2StdIndexer.addSource("NUMBER", number, in2StdIndexer.SOURCE_TYPE_TEXT);
		        in2StdIndexer.addSource("TITLE0", data.getTitle0(), in2StdIndexer.SOURCE_TYPE_TEXT);
		        in2StdIndexer.addSource("TITLE1", data.getTitle1(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("TITLE2", data.getTitle2(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("TITLE3", data.getTitle3(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("TITLE4", data.getTitle4(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("TITLE4_1", data.getTitle4_1(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("NUMBER0", data.getNumber0(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("NUMBER1", data.getNumber1(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("NUMBER2", data.getNumber2(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("NUMBER3", data.getNumber3(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("NUMBER4", data.getNumber4(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("CONTENT", data.getContent(), in2StdIndexer.SOURCE_TYPE_HTML);
			    in2StdIndexer.addSource("ETC", data.getEtc(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("INDEXGB", data.getIndexgb(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("DOMAIN", data.getDomain(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("LEVEL", data.getLevel(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("POSITION", data.getPosition(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    in2StdIndexer.addSource("FILENM", data.getFileNm(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    if(tmpInt == 3) {
			    	in2StdIndexer.addSource("TOKENTITLE", data.getTitle3(), in2StdIndexer.SOURCE_TYPE_TEXT);
			    }else {
			    	in2StdIndexer.addSource("TOKENTITLE", data.getTitle1(), in2StdIndexer.SOURCE_TYPE_TEXT);	
			    }
			    
			        
			    	
			    in2StdIndexer.addFieldFTR("TITLE", "TITLE", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("NUMBER", "NUMBER", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("TITLE0", "TITLE0", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("TITLE1", "TITLE1", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("TITLE2", "TITLE2", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("TITLE3", "TITLE3", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("TITLE4", "TITLE4", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("TITLE4_1", "TITLE4_1", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("NUMBER0", "NUMBER0", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("NUMBER1", "NUMBER1", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("NUMBER2", "NUMBER2", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("NUMBER3", "NUMBER3", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("NUMBER4", "NUMBER4", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("CONTENT", "CONTENT", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("ETC", "ETC", in2StdIndexer.TOKENIZER_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("INDEXGB", "INDEXGB", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("DOMAIN", "DOMAIN", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("LEVEL", "LEVEL", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("POSITION", "POSITION", in2StdIndexer.TOKENIZER_TERM, true, true);
			    in2StdIndexer.addFieldFTR("FILENM", "FILENM", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldFTR("TOKENTITLE", "TOKENTITLE", in2StdIndexer.TOKENIZER_TERM, true, true);
			    	
			    in2StdIndexer.addFieldFTR("INTEGRATION", "DOMAIN/TITLE1/TITLE2/TITLE3/TITLE4/CONTENT", in2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
			    in2StdIndexer.addFieldTMS("TMS", "KOR", "TITLE1/TITLE2/TITLE3", true, 100);

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
