package com.saltlux.khnp.searcher.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponseVo {

    private Object result;

    private int errorCode;
    private String errorMessage;

    public CommonResponseVo(Object result){
        this.result = result;
    }

    public CommonResponseVo(){}

}
