package com.saltlux.khnp.searcher.search.vo;

import com.saltlux.khnp.searcher.search.model.SearchLog;
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

    public SearchVo(){}
    public SearchVo(SearchLog searchLog){
        logId = String.valueOf(searchLog.getLogId());
        logKeyword = searchLog.getLogKeyword();
        createDt = searchLog.getCreateDt();
        clientIp = searchLog.getClientIp();
    }
}
