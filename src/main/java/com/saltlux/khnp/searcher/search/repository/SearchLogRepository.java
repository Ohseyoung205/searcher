package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
    List<SearchLog> findByLogKeywordContaining(String query);
}
