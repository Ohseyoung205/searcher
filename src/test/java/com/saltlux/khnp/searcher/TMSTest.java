package com.saltlux.khnp.searcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saltlux.tms.analysis.TextAnalyzer;
import com.saltlux.tms.util.IN2TMSProperties;
import com.saltlux.tms3.util.TYPE;

import java.util.Map;

public class TMSTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("IN2_HOME", "D:\\solution\\[IN2] Discovery 3.0");
        IN2TMSProperties prop = IN2TMSProperties.getInstance();
        TextAnalyzer analyzer = TextAnalyzer.loadServer(prop);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<Long, String> rs = analyzer.getRawStream("솔트룩스에 입사하신걸 축하드립니다.", TYPE.LANG_KOR | TYPE.TYPE_NE_STR);
        for (Map.Entry<Long, String> longStringEntry : rs.entrySet()) {
            JsonElement je = new JsonParser().parse(longStringEntry.getValue());
            String prettyJsonString = gson.toJson(je);
            System.out.println(prettyJsonString);
        }
    }
}
