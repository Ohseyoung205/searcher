package com.saltlux.khnp.searcher.deepqa.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.deepqa.model.DeepQARequest;
import com.saltlux.khnp.searcher.deepqa.service.DeepQAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/deepqa")
@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
public class DeepQAController {

    @Autowired
    DeepQAService deepQAService;

    @PostMapping
    public CommonResponseVo deepQA(@RequestBody DeepQARequest request){
        return new CommonResponseVo(deepQAService.deepQA(request));
    }

}
