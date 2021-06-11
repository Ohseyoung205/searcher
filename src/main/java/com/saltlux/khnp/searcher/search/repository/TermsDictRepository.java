package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import com.saltlux.khnp.searcher.search.model.SearchLog;
import com.saltlux.khnp.searcher.search.model.TermsDict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsDictRepository extends JpaRepository<TermsDict, Long> {
    List<TermsDict> findByTermsKoNameContaining(String koName);
    List<TermsDict> findByTermsEaNameContaining(String eaName);
    List<TermsDict> findByTermsAbrContaining(String abr);
    List<TermsDict> findByTermsContentsContaining(String content);
    List<TermsDict> findByTermsKoNameContainingOrTermsEaNameContainingOrTermsAbrContainingOrTermsAbrContaining(String koName, String eaName, String abr, String content);
}
