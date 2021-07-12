package com.saltlux.khnp.searcher.search.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saltlux.khnp.searcher.search.model.DomainTable;


public interface DomainRepository extends JpaRepository<DomainTable, Long> {

	@Query("select d from DomainTable d where d.indexName = :domain order by d.domainId desc")
    List<DomainTable> findByDomainList(@Param("domain") String domain);
	
	@Query("select d from DomainTable d where d.uuid = :uuid order by d.domainId desc")
    List<DomainTable> findByUuidList(@Param("uuid") String uuid);
}
