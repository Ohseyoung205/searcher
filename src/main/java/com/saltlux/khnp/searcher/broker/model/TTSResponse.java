package com.saltlux.khnp.searcher.broker.model;

import lombok.Getter;

@Getter
public class TTSResponse {

    private String base64Audio;

    public TTSResponse(){}
    public TTSResponse(String base64Audio){
        this.base64Audio = base64Audio;
    }
}
