package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "plant_operation_document")
public class PlantOperationDocument {

    @Id
    @Column(name = "document_id")
    private int documentId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "domainId")
    @OneToOne(targetEntity = DomainTable.class)
    private DomainTable domainTable;

}
