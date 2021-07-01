package com.saltlux.khnp.searcher.deepqa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeepQAResult {

    private String answer;

    private Double score;

    private Integer start;

    private Integer end;

    private String context;

}
