package com.saltlux.khnp.searcher.tagname.analysis;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Cluster<T> implements Comparator<Cluster> {

    private int id;

    private List<T> member;

    public Cluster(int id){
        this.id = id;
        member = new ArrayList<>();
    }

    public void add(T point){
        member.add(point);
    }

    @Override
    public int compare(Cluster o1, Cluster o2) {
        return o2.id - o1.id;
    }
}
