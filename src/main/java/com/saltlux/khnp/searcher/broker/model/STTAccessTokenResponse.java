package com.saltlux.khnp.searcher.broker.model;

import lombok.Getter;

@Getter
public class STTAccessTokenResponse {

    private boolean result;

    private String errMsg;

    private String accessToken;
}
