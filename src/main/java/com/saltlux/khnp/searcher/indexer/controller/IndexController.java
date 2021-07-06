package com.saltlux.khnp.searcher.indexer.controller;

import com.saltlux.khnp.searcher.indexer.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
public class IndexController {

    @Autowired
    IndexService indexService;

    @RequestMapping(value="/khnpIndex")
    public Map<String, String> khnpIndex(String domain) throws Exception{
        log.info("색인 및 htm 변환 시작");
        return indexService.indexService(domain);
    }

}
