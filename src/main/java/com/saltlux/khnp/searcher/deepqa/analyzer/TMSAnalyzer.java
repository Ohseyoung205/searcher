package com.saltlux.khnp.searcher.deepqa.analyzer;

import com.google.gson.Gson;
import com.knu.lea.api.util.Tag.Analysis;
import com.knu.lea.api.util.Tag.Morph;
import com.knu.lea.api.util.Tag.Sentence;
import com.saltlux.tms.api.IN2TMSAnalyzer;
import com.saltlux.tms3.util.TYPE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class TMSAnalyzer {

    /*private TextAnalyzer analyzer;

    public TMSAnalyzer() throws Exception {
        System.setProperty("IN2_HOME", "D:\\solution\\[IN2] Discovery 3.0");
        IN2TMSProperties prop = IN2TMSProperties.getInstance();
        analyzer = TextAnalyzer.loadServer(prop);
    }*/

    @Value("${in2.dor.host}")
    private String host;

    @Value("${in2.tms.port}")
    private Integer port;

    public String getPOS(String text){
        IN2TMSAnalyzer analyzer = new IN2TMSAnalyzer();
        analyzer.setServer(host, port);
        long mode = TYPE.LANG_KOR | TYPE.TYPE_POS_STR;
        Map<Long, String> rs = analyzer.getRawStream(text, mode);
        if(rs.get(mode) == null){
            return text;
        }

        try{
            StringBuffer sb = new StringBuffer();
            Analysis analysis = new Gson().fromJson(rs.get(mode), Analysis.class);
            for(Sentence s : analysis.sentence){
                for(Morph m : s.morp){
                    if("-".equals(m.lemma) && "SS".equals(m.type))
                        continue;
                    sb.append(m.lemma).append("/").append(m.type).append(" ");
                }
                log.debug(s.toString());
            }
            return sb.toString().toLowerCase();
        }catch (Exception e){
            return text;
        }
    }

    public String getIndexWords(String text){
        IN2TMSAnalyzer analyzer = new IN2TMSAnalyzer();
        analyzer.setServer(host, port);
        long mode = TYPE.LANG_KOR | TYPE.TYPE_INDEX;
        Map<Long, String> rs = analyzer.getRawStream(text, mode);
        return rs.getOrDefault(mode, text);
    }
}
