package com.saltlux.khnp.searcher.broker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TTSRequest {

    /*
    voiceware tts type : [3,10,19,21]
    saltlux tts type : [saltlux]
     */
    private String voiceType = "3";

    private String message;

}
