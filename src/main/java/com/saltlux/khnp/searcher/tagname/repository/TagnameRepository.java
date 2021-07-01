package com.saltlux.khnp.searcher.tagname.repository;

import com.saltlux.khnp.searcher.tagname.model.TagnameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagnameRepository extends JpaRepository<TagnameEntity, Integer> {
}
