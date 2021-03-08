package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.service.SearchService;
import com.saltlux.khnp.searcher.search.vo.SearchRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @PostMapping("/integration")
    public CommonResponseVo integrationSearch(SearchRequests requests){
        return new CommonResponseVo(searchService.integrationSearch(requests));
    }
}
