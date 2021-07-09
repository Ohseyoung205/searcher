package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "search_log")
public class SearchLog {

  @Id
  @Column
  private Integer logId;

  @Column
  private String logKeyword;

  @Column
  private String createDt;

  @Column
  private String clientIp;

}
