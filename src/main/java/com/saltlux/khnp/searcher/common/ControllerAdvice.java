package com.saltlux.khnp.searcher.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public CommonResponseVo exceptHandle(RuntimeException e) {
        log.error("", e);
        CommonResponseVo res = new CommonResponseVo();
        res.setErrorMessage(e.getMessage());
        res.setErrorCode(-1);
        return res;
    }

}
