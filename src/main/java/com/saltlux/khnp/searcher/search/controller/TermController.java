package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.service.TermSearchService;
import com.saltlux.khnp.searcher.search.vo.TermSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/term")
@RestController
public class TermController {

    @Autowired
    TermSearchService termSearchService;

    @GetMapping("/search")  // 용어검색 API
    public CommonResponseVo termSearch(TermSearchRequest requests) {
        return new CommonResponseVo(termSearchService.termSearch(requests));
    }

    @GetMapping("/source/groupBy") // 용어집이름 검색 API ---- 파라미터 없음
    public CommonResponseVo sourceGroupBy(){
        return new CommonResponseVo(termSearchService.sourceGroupBy());
    }

}
