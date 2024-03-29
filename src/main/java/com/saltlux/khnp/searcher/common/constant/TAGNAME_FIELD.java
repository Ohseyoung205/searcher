package com.saltlux.khnp.searcher.common.constant;

import com.saltlux.dor.api.IN2StdSearcher;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum TAGNAME_FIELD {

    TAGID("TAGID", IN2StdSearcher.TOKENIZER_TERM, true, true),
    TAGNAME("TAGNAME", IN2StdSearcher.TOKENIZER_KOR_BIGRAM, true, true),
    DESCRIPTION("DESCRIPTION", IN2StdSearcher.TOKENIZER_KOR_BIGRAM, true, true),
    PLANT("PLANT", IN2StdSearcher.TOKENIZER_TERM, true, true),
    UNIT("UNIT", IN2StdSearcher.TOKENIZER_TERM, true, true),
    ALIAS("ALIAS", IN2StdSearcher.TOKENIZER_KOR_BIGRAM, true, true),
    CLUSTER("CLUSTER", IN2StdSearcher.TOKENIZER_TERM, true, true),
    INTEGRATION("TAGNAME/DESCRIPTION/PLANT/ALIAS", IN2StdSearcher.TOKENIZER_KOR_BIGRAM, true, true);

    private String fieldName;

    private String analyzer;

    private boolean indexed;

    private boolean stored;

    public static String[] getAllFields(){
        return Stream.of(TAGNAME_FIELD.values())
                .map(f -> f.getFieldName())
                .toArray(String[]::new);
    }

}
