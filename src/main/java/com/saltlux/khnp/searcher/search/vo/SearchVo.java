package com.saltlux.khnp.searcher.search.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVo {

	private String logId;
    private String logKeyword;
    private String createDt;
    private String clientIp;
    private String firstDt;
    private String lastDt;
}
