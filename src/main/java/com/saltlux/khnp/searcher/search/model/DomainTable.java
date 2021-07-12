package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
	private String indexName; 	//색인테이블명
	
	@Column
	private String uuid; 	//색인테이블명

	@Column
	private Date createDt;

	@Column
	@Type(type = "yes_no")
	private boolean recYn;
}
