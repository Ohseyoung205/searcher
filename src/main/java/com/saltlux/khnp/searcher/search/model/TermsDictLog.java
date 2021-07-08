package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class TermsDictLog {

    @Id
    private int logId;

    @JoinColumn(name = "termsId")
    @ManyToOne(targetEntity = TermsDict.class)
    private TermsDict termsDict;

    @Column
    private Date createDt;
}
