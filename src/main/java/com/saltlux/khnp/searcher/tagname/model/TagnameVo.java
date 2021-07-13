package com.saltlux.khnp.searcher.tagname.model;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.khnp.searcher.common.constant.TAGNAME_FIELD;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Comparator;

@Getter
@EqualsAndHashCode(exclude = {"cluster"})
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
        cluster = -1;
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

    @Override
    public int compare(TagnameVo o1, TagnameVo o2) {
        return Integer.valueOf(o2.getTagid()) - Integer.valueOf(o1.getTagid());
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
