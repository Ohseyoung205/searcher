package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomDictRepository extends JpaRepository<CustomDict, Long> {
    List<CustomDict> findByMainWordContaining(String query);
    List<CustomDict> findBySubWordContaining(String query);
    List<CustomDict> findByMainWordContainingOrSubWordContaining(String mainWord, String subWord);

}
