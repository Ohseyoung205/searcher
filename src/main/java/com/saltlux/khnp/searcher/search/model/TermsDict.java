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
@Table(name = "terms_dictionary")
public class TermsDict {

  @Id
  @Column
  private Integer termsId;

  @Column
  private String termsDiv;

  @Column
  private String termsKoName;

  @Column
  private String termsEaName;

  @Column
  private String termsContents;

  @Column
  private String termsAbr;

  @Column
  private String createDt;

  @Column
  private String useYn;

  @Column
  private String recYn;


}
