package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.TermsDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TermsDictRepository extends JpaRepository<TermsDict, Long>, JpaSpecificationExecutor<TermsDict> {

    // 용어집 이름이 전체일 때
    List<TermsDict> findByTermsKoNameContaining(String termsKoName);
    List<TermsDict> findByTermsEaNameContaining(String termsEaName);
    List<TermsDict> findByTermsContentsContaining(String termsContent);
    List<TermsDict> findByTermsAbrContaining(String abr);
    List<TermsDict> findByTermsKoNameContainingOrTermsEaNameContainingOrTermsContentsContainingOrTermsAbrContaining(String termsKoName, String termsEaName, String termsContents, String termsAbr);


    // 용어집이름이 들어올 때
    List<TermsDict> findByTermsDivEqualsAndTermsKoNameContaining(String termsDiv, String termsKoName);
    List<TermsDict> findByTermsDivEqualsAndTermsEaNameContaining(String termsDiv, String termsEaName);
    List<TermsDict> findByTermsDivEqualsAndTermsContentsContaining(String termsDiv, String termsContent);
    List<TermsDict> findByTermsDivEqualsAndTermsAbrContaining(String termsDiv, String abr);

    // field 전체 검색
    @Query("select d from TermsDict d where d.termsDiv = :termsDiv and " +
            "concat(d.termsKoName, d.termsEaName, d.termsContents, d.termsAbr) like concat('%', :keyword, '%')")
    List<TermsDict> findByTermsDivExtra(@Param("termsDiv")String termsDiv, @Param("keyword")String keyword);

}
