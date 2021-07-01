package com.saltlux.khnp.searcher.search.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PageResultVo {

    private Collection documents;

    private long totalCount;

    public PageResultVo(Collection documents, long totalCount){
        this.documents = documents;
        this.totalCount = totalCount;
    }


}
