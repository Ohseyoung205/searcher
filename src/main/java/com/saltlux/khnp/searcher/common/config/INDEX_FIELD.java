package com.saltlux.khnp.searcher.common.config;

import java.util.Arrays;
import java.util.Optional;

public enum INDEX_FIELD {

    // 카테고리 ex) 운영기술지침서=A, 기술배경서=B
    CATEGORY("CATOGORY", "TERM", true, true),
    // 목차 ex) 3.6.1
    NUMBER("NUMBER", "TERM", true, true),
    // 제목 ex) 운전제한조건 및 점검요구사항 적용|||반응도제어계통|||반응도평형
    TITLE("TITLE", "KOR_BIGRAM", true, true),
    // 내용
    CONTENTS("CONTENTS", "KOR_BIGRAM", true, true),
	
	YYYYMMDD("YYYYMMDD", "TERM", true, true);

    public String fieldName;
    public String analyzer;
    public boolean bIndex;
    public boolean bStore;

    private INDEX_FIELD(String fn, String an, boolean bi, boolean bs){
        fieldName = fn;
        analyzer = an;
        bIndex = bi;
        bStore = bs;
    }

    public static INDEX_FIELD find(String fieldName){
        Optional<INDEX_FIELD> optional = Arrays.asList(INDEX_FIELD.values())
                .stream()
                .filter(field -> fieldName.equalsIgnoreCase(field.fieldName))
                .findFirst();
        if(optional.isPresent())
            return optional.get();
        return null;
    }
}
