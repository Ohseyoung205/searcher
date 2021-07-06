package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.service.SearchLogService;
import com.saltlux.khnp.searcher.search.service.SearchService;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import com.saltlux.khnp.searcher.search.vo.SearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
public class SearchController {

    @Autowired
    SearchService searchService;
   
    @Autowired
    SearchLogService searchLogService;
    

    @PostMapping("/search")
    public CommonResponseVo integrationSearch(@RequestBody SearchRequests requests){
        return new CommonResponseVo(searchService.integrationSearch(requests));
    }

    @GetMapping("/table")
    public CommonResponseVo hierarchy(@RequestParam("plant") String plant,
                                      @RequestParam("query") String query,
                                      @RequestParam("inferred") boolean inferred) {
    	return new CommonResponseVo(searchService.hierarchySearch(plant, query, inferred));
    }
    
    @GetMapping("/wordCloud")
    @CrossOrigin(origins="*", allowedHeaders="*")
    public CommonResponseVo wordCloud(SearchVo searchVo) throws Exception{
    	return new CommonResponseVo(searchLogService.wordCloud(searchVo));
    }
}
