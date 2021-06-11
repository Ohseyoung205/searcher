package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "custom_dictionary")
public class CustomDict {

  @Id
  @Column
  private Integer wordId;

  @Column
  private String mainWord;

  @Column
  private String subWord;

  @Column
  private String wordDiv;

  @Column
  private String useYn;

  @Column
  private String recYn;

  @Column
  private String createDt;


}
