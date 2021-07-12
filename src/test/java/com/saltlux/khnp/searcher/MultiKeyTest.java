package com.saltlux.khnp.searcher;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class MultiKeyTest {
    public static void main(String[] args) {
        CustomDict vo = new CustomDict();
        vo.setWordId(1L);
        CustomDict vo2 = new CustomDict();
        vo2.setWordId(1L);
        CustomDict vo3 = new CustomDict();
        vo3.setWordId(3L);

        BidiMap<String, CustomDict> map = new DualHashBidiMap<>();
        map.put("A", vo);
        map.put("B", vo);
        map.put("C", vo3);

        System.out.println(map.keySet().size());
        map.removeValue(vo2);
        System.out.println(map.keySet().size());
    }
}
