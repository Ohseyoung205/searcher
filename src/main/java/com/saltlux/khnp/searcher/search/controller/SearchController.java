package com.saltlux.khnp.searcher.search.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.indexer.service.IndexService;
import com.saltlux.khnp.searcher.indexer.service.TargetIndexService;
import com.saltlux.khnp.searcher.search.service.BrokerService;
import com.saltlux.khnp.searcher.search.service.SearchLogService;
import com.saltlux.khnp.searcher.search.service.SearchService;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import com.saltlux.khnp.searcher.search.vo.SearchVo;



@RestController
public class SearchController {

    @Autowired
    SearchService searchService;
   
    @Autowired
    BrokerService brokerService;
    
    @Autowired
    TargetIndexService targetIndexService;
    
    @Autowired
    SearchLogService searchLogService;
    
    @Autowired
	IndexService indexService;


    @PostMapping("/search")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public CommonResponseVo integrationSearch(@RequestBody SearchRequests requests){
        return new CommonResponseVo(searchService.integrationSearch(requests));
    }

    @GetMapping("/table")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public CommonResponseVo hierarchy(@RequestParam("plant") String plant,
                                      @RequestParam("query") String query,
                                      @RequestParam("inferred") boolean inferred) {

    	
    	return new CommonResponseVo(searchService.hierarchySearch(plant, query, inferred));
    }
    
    @RequestMapping(value="/khnpIndex")
    @CrossOrigin(origins="*", allowedHeaders="*")
	public Map<String, String> khnpIndex(String domain) throws Exception{
		System.out.println("색인 및 htm 변환 시작");
	//	String msg = indexService.indexService(domain);
		
	//	System.out.println("msg ::"+msg);
		return indexService.indexService(domain);
	}
    
//    @GetMapping("/targetList")
//    @CrossOrigin(origins="*", allowedHeaders="*")
//    public CommonResponseVo target() {
//    	
//    	return new CommonResponseVo(searchService.targetSearch());
//    }
    
    
    @GetMapping("/wordCloud")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public CommonResponseVo wordCloud(SearchVo searchVo) throws Exception{
    	
    	return new CommonResponseVo(searchLogService.wordCloud(searchVo));
    }
    
    @RequestMapping(value="/chat_dm/khnp")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public Map<String, Object> khnp_broker(HttpServletRequest request
			, HttpServletResponse response) throws Exception{	
    	return brokerService.khnpBroker(request, response);
    }
     
    
//    @Bean
//    public ServletWebServerFactory serverFactory() {
//    	TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//    	tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 톰캣에 Connector 추가
//    	return tomcat;
//    }
//    
//    private Connector createStandardConnector() {
//    	Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//    	connector.setPort(8700); // 포트 설정
//    	return connector;
//    }

}
