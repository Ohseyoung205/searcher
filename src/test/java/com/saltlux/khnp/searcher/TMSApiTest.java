package com.saltlux.khnp.searcher;

import com.saltlux.tms.api.IN2TMSAnalyzer;
import com.saltlux.tms3.util.TYPE;

import java.util.Iterator;
import java.util.Map;

public class TMSApiTest {
    public static void main(String[] args) {
        IN2TMSAnalyzer analyzer = new IN2TMSAnalyzer();
        analyzer.setServer("192.168.219.2", 10100);
        Map<Long, String> rs = analyzer.getRawStream("엑소텍에 입사하신걸 축하드립니다.", TYPE.LANG_KOR | TYPE.TYPE_POS_STR);
        Iterator<Long> it = rs.keySet().iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
    }
}
