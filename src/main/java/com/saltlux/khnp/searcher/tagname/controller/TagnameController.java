package com.saltlux.khnp.searcher.tagname.controller;


import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.tagname.service.TagnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tagname")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TagnameController {

    @Autowired
    TagnameService tagnameService;

    @GetMapping("")
    public CommonResponseVo searchByTalkbot(
            @RequestParam(value = "plant", defaultValue = "")String plant,
            @RequestParam(value = "query", defaultValue = "")String query) {
        return new CommonResponseVo(tagnameService.searchByTalkbot(plant, query));
    }

    @GetMapping("/search")
    public CommonResponseVo search(@RequestParam(value = "query", defaultValue = "")String query,
                                   @RequestParam(value = "plant", defaultValue = "")String plant,
                                   @RequestParam(value = "offset", defaultValue = "0")int offset,
                                   @RequestParam(value = "limit", defaultValue = "10")int limit) throws Exception{
        return new CommonResponseVo(tagnameService.search(plant, query, offset, limit));
    }

}
