package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomDictRepository extends JpaRepository<CustomDict, Long> {
    List<CustomDict> findByWordDivEquals(String wordDiv); // wordDiv에 따른 전체검색 D이면 동의어, B이면 불용어

    List<CustomDict> findByWordDivEqualsAndMainWordContaining(String wordDiv, String mainWord);
    List<CustomDict> findByWordDivEqualsAndSubWordContaining(String wordDiv, String subWord);

    // 동의어사전 field 전체 검색
    @Query("select d from CustomDict d where d.wordDiv = :wordDiv and " +
            "concat(d.mainWord, d.subWord) like concat('%', :keyword, '%')")
    List<CustomDict> findByWordDivExtra(String wordDiv, String keyword);



}
