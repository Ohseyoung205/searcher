package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomDictRepository extends JpaRepository<CustomDict, Long> {
    //  전체검색 recYn= "Y"
    List<CustomDict> findByWordDivEqualsAndRecYnEquals(String wordDiv, String recYn); // wordDiv에 따른 전체검색 D이면 동의어, B이면 불용어

    // mainWord, subWord 검색
    List<CustomDict> findByWordDivEqualsAndMainWordContainingAndRecYnEquals(String wordDiv, String mainWord, String recYn);
    List<CustomDict> findByWordDivEqualsAndSubWordContainingAndRecYnEquals(String wordDiv, String subWord, String recYn);

    // 동의어사전 field 전체 검색 recYn= "Y"
    @Query("select d from CustomDict d where d.wordDiv = :wordDiv and " +
            "concat(d.mainWord, d.subWord) like concat('%', :keyword, '%') and d.recYn=:recYn" )
    List<CustomDict> findByWordDivExtra(@Param("wordDiv")String wordDiv, @Param("keyword")String keyword, @Param("recYn")String recYn);
    
 // 불용어 조회
    @Query("select d.mainWord from CustomDict d where d.wordDiv = 'B' and d.useYn = 'Y'")
    List<String> findByCustomDictList();

}
