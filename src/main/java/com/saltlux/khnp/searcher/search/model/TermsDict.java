package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Entity
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
  private Date createDt;

  @Column
  @Type(type = "yes_no")
  private boolean useYn;

  @Column
  @Type(type = "yes_no")
  private boolean recYn;


}
