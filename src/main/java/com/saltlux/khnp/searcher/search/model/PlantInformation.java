package com.saltlux.khnp.searcher.search.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "plant_information")
public class PlantInformation {

    @Id
    @Column(name = "prefix", unique = true, nullable = true, length = 4)
    private String prefix;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
