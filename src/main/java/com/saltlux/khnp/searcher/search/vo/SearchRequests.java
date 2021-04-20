package com.saltlux.khnp.searcher.search.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchRequests {

    private String query;

    private String range;

    private List<String> field;

    private List<Filter> filter;

    private List<Sort> sort;

    private List<Return> returns;

    private int offset;

    private int limit;

    @Getter
    @Setter
    public static class Filter{
        private String filterTerm;
        private String filterField;

        public Filter(String field, String term){
            filterField = field;
            filterTerm = term;
        }

        public Filter(){}
    }

    @Getter
    @Setter
    public static class Sort{
        private String sortField;
        private boolean asc;
    }

    @Getter
    @Setter
    public static class Return{
        private String returnField;
        private int returnLength;
        private boolean hilight;
    }
}

