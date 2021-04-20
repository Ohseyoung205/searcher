package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.service.SearchService;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/search")
public class SearchController {

    @Autowired
    SearchService searchService;


    @PostMapping("/search")
    public CommonResponseVo integrationSearch(SearchRequests requests){
        return new CommonResponseVo(searchService.integrationSearch(requests));
    }

    @GetMapping("/table")
    public CommonResponseVo hierarchy(@RequestParam("plant") String plant,
                                      @RequestParam("query") String query,
                                      @RequestParam("inferred") boolean inferred) {
        return new CommonResponseVo(null);
    }

}
