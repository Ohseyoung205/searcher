package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.TermsDictLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TermsDictLogRepository extends JpaRepository<TermsDictLog, Integer> {

    public List<TermsDictLog> findByCreateDtGreaterThanOrderByCreateDt(Date createDt);
}
