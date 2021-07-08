package com.saltlux.khnp.searcher.search.vo;

import com.saltlux.khnp.searcher.search.model.TermsDict;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TermsDictVo {

    private String id;
    private String glossaryName; //용어집 이름
    private String engTerms; // 영문 용어
    private String korTerms; // 한글 용어
    private String abbreviation; // 약어
    private String termsExplain; // 용어설명

    public TermsDictVo(){}
    public TermsDictVo(TermsDict termsDict){
        id = termsDict.getTermsId().toString();
        glossaryName = termsDict.getTermsDiv();
        engTerms = termsDict.getTermsEaName();
        korTerms = termsDict.getTermsKoName();
        abbreviation = termsDict.getTermsAbr();
        termsExplain = termsDict.getTermsContents();

    }
}
