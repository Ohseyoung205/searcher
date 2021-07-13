package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.TermsDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface TermsDictRepository extends JpaRepository<TermsDict, Long>, JpaSpecificationExecutor<TermsDict> {

    //용어집 이름이 전체일 때 field 전체 검색 recYn= "Y"
    @Query("select d from TermsDict d where d.recYn = :recYn and " +
            "concat(d.termsKoName, d.termsEaName, d.termsContents, d.termsAbr) like concat('%', :keyword, '%')")
    List<TermsDict> findByRecYnEqualsYAndKeyword(@Param("recYn")boolean recYn, @Param("keyword")String keyword);

    // 엑셀파일 sheet이름이 될 termsDiv 중복제거하여 불러오기
    @Query("select distinct d.termsDiv from TermsDict d where d.recYn = :recYn and " +
            "concat(d.termsKoName, d.termsEaName, d.termsContents, d.termsAbr) like concat('%', :keyword, '%')")
    ArrayList<String> findByRecYnEqualsYAndKeywordDistinct(@Param("recYn")boolean recYn, @Param("keyword")String keyword);


    // 용어집이름이 있을 때 field 전체 검색 recYn= "Y"
    @Query("select d from TermsDict d where d.termsDiv = :termsDiv and " +
            "concat(d.termsKoName, d.termsEaName, d.termsContents, d.termsAbr) like concat('%', :keyword, '%')and d.recYn=:recYn")
    List<TermsDict> findByTermsDivExtra(@Param("termsDiv")String termsDiv, @Param("keyword")String keyword, @Param("recYn")boolean recYn);

    // 엑셀파일 sheet이름이 될 termsDiv 중복제거하여 불러오기
    @Query("select distinct d.termsDiv from TermsDict d where d.termsDiv = :termsDiv and " +
            "concat(d.termsKoName, d.termsEaName, d.termsContents, d.termsAbr) like concat('%', :keyword, '%')and d.recYn=:recYn")
    ArrayList<String> findByTermsDivExtraDistinct(@Param("termsDiv")String termsDiv, @Param("keyword")String keyword, @Param("recYn")boolean recYn);

}
