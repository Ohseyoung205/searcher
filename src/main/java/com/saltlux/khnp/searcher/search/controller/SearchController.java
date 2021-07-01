package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.service.BrokerService;
import com.saltlux.khnp.searcher.search.service.SearchLogService;
import com.saltlux.khnp.searcher.search.service.SearchService;
import com.saltlux.khnp.searcher.search.service.TargetIndexService;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import com.saltlux.khnp.searcher.search.vo.SearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


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
    
    @GetMapping("/wordCloud")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public CommonResponseVo wordCloud(SearchVo searchVo) throws Exception{
    	
    	return new CommonResponseVo(searchLogService.wordCloud(searchVo));
    }
    
    @ResponseBody
    @RequestMapping(value="/chat_dm/khnp")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public Map<String, String> khnp_broker(HttpServletRequest request
			, HttpServletResponse response) throws Exception{	
    	return brokerService.khnpBroker(request, response);
    }
}
