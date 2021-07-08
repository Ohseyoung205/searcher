package com.saltlux.khnp.searcher.search.model;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "custom_dictionary_log")
public class CustomDictLog {

    public enum LOG_DIV{
        C, U, D
    }

    @Id
    private Long logId;

    @Column
    @Enumerated(EnumType.STRING)
    private LOG_DIV logDiv;

    @Column
    private Date createDt;

    @JoinColumn(name = "word_id")
    @ManyToOne(targetEntity = CustomDict.class)
    private CustomDict wordEntity;

}
