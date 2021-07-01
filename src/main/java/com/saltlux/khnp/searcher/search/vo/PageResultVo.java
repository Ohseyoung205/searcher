package com.saltlux.khnp.searcher.search.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PageResultVo {

    private Collection result;

    private long totalCount;

    public PageResultVo(Collection result, long totalCount){
        this.result = result;
        this.totalCount = totalCount;
    }


}
