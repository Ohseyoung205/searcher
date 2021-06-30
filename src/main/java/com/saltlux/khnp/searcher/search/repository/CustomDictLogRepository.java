package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.CustomDictLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CustomDictLogRepository extends JpaRepository<CustomDictLog, Long> {

    List<CustomDictLog> findByCreateDtGreaterThan(Date createDt);
}

