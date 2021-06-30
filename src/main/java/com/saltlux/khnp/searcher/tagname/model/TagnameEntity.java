package com.saltlux.khnp.searcher.tagname.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tagname")
public class TagnameEntity {

    @Id
    @GeneratedValue
    private Integer tagid;

    @Column
    private String tagname;

    @Column
    private String description;

}
