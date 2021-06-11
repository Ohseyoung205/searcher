package com.saltlux.khnp.searcher.search.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.service.BrokerService;
import com.saltlux.khnp.searcher.search.service.SearchService;
import com.saltlux.khnp.searcher.search.service.TargetIndexService;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;


@RestController
public class SearchController {

    @Autowired
    SearchService searchService;
   
    @Autowired
    BrokerService brokerService;
    
    @Autowired
    TargetIndexService targetIndexService;


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
    
    @GetMapping("/targetList")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public CommonResponseVo target() {
    	
    	return new CommonResponseVo(searchService.targetSearch());
    }
    
    @GetMapping("/targetIndex")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public void targetIndex(@RequestParam("query") String query) throws Exception{
    	targetIndexService.targetIndex(query);
    }
    
    @ResponseBody
    @RequestMapping(value="/chat_dm/khnp")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public Map<String, String> khnp_broker(HttpServletRequest request
			, HttpServletResponse response) throws Exception{	
    	return brokerService.khnpBroker(request, response);
    }
     
    
    @Bean
    public ServletWebServerFactory serverFactory() {
    	TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    	tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 톰캣에 Connector 추가
    	return tomcat;
    }
    
    private Connector createStandardConnector() {
    	Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    	connector.setPort(8700); // 포트 설정
    	return connector;
    }

}
