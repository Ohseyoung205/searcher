package com.saltlux.khnp.searcher.tagname.model;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.khnp.searcher.common.constant.TAGNAME_FIELD;
import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

@Getter
public class TagnameVo implements Comparator<TagnameVo>, Comparable<TagnameVo> {

    private String tagid;

    private String tagname;

    private String plant;

    private String description;

    private Integer cluster;

    private String unit;

    public TagnameVo(TagnameEntity e){
        tagid = String.format("%010d", e.getTagid());
        String[] tags = e.getTagname().split("-");
        tagname = tags[1];
        plant = tags[0];
        description = e.getDescription();
        unit = e.getUnit();
        cluster = dbscanCluster();
    }

    public TagnameVo(int i, IN2StdSearcher searcher) {
        tagid = searcher.getValueInDocument(i, TAGNAME_FIELD.TAGID.getFieldName());
        tagname = searcher.getValueInDocument(i, TAGNAME_FIELD.TAGNAME.getFieldName());
        plant = searcher.getValueInDocument(i, TAGNAME_FIELD.PLANT.getFieldName());
        description = searcher.getValueInDocument(i, TAGNAME_FIELD.DESCRIPTION.getFieldName());
        unit = searcher.getValueInDocument(i, TAGNAME_FIELD.UNIT.getFieldName());
        cluster = Integer.valueOf(searcher.getValueInDocument(i, TAGNAME_FIELD.CLUSTER.getFieldName()));
    }

    public TagnameVo setCluster(int cluster){
        this.cluster = cluster;
        return this;
    }

    private Integer dbscanCluster() {
        return -1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagid, tagname, plant, description);
    }

    @Override
    public int compare(TagnameVo o1, TagnameVo o2) {
        return Integer.valueOf(o2.getTagid()) - Integer.valueOf(o1.getTagid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TagnameVo))
            return false;
        TagnameVo other = (TagnameVo)o;
        return Objects.equals(tagid, other.tagid) &&
                Objects.equals(tagname, other.tagname) &&
                Objects.equals(plant, other.plant) &&
                Objects.equals(description, other.description);
    }

    @Override
    public String toString(){
        return String.format("%s-%s : %s", plant, tagname, description);
    }

    @Override
    public int compareTo(TagnameVo o) {
        return Integer.valueOf(this.tagid) - Integer.valueOf(o.getTagid());
    }
}
