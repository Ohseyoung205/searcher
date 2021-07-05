package com.saltlux.khnp.searcher.search.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "domain_table")
public class DomainTable {

	@Id
	@Column
	private Integer domainId; 	//도메인 키
	
	@Column
	private String name; 		//도메인 명
	
	@Column
	private String indexName; 	//색인테이블명
}
