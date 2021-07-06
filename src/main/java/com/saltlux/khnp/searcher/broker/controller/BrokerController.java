package com.saltlux.khnp.searcher.broker.controller;

import com.saltlux.khnp.searcher.broker.model.STTRequest;
import com.saltlux.khnp.searcher.broker.model.TTSRequest;
import com.saltlux.khnp.searcher.broker.service.BrokerService;
import com.saltlux.khnp.searcher.common.CommonResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/broker")
@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
public class BrokerController {

    @Autowired
    BrokerService brokerService;

    @PostMapping(value="/stt")
    public CommonResponseVo speechToText(@RequestBody STTRequest request) {
        return new CommonResponseVo(brokerService.speechToText(request.getBase64Audio()));
    }

    @GetMapping(value="/tts")
    public CommonResponseVo textToSpeech(TTSRequest request) throws Exception{
        return new CommonResponseVo(brokerService.textToSpeech(request));
    }
}
