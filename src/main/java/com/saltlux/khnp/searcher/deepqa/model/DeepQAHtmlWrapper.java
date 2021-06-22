package com.saltlux.khnp.searcher.deepqa.model;

import com.saltlux.khnp.searcher.deepqa.analyzer.TMSAnalyzer;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class DeepQAHtmlWrapper {

    TMSAnalyzer analyzer;
    Document document;

    AtomicInteger tokenKey;
    AtomicInteger sequenceLength;

    LinkedHashMap<Integer, String> texts;
    LinkedHashMap<Integer, String> morphTexts;
    LinkedHashMap<Integer, Integer> morphSequence;

    public DeepQAHtmlWrapper(Document document, TMSAnalyzer analyzer){
        this.analyzer = analyzer;
        this.document = document;
        this.tokenKey = new AtomicInteger(0);
        this.sequenceLength = new AtomicInteger(0);
        this.texts = new LinkedHashMap();
        this.morphTexts = new LinkedHashMap();
        this.morphSequence = new LinkedHashMap();
        tokenize(document.body());
    }

    private void tokenize(Element element){
        texts.put(tokenKey.getAndIncrement(), tagPrefix(element));

        // never visit
        if(element.childNodeSize() == 0 && StringUtils.isNotBlank(element.text())){
            texts.put(tokenKey.getAndIncrement(), element.text());
            return;
        }

        for(Node n : element.childNodes()){
            if(n instanceof TextNode){
                if(StringUtils.isNotBlank(n.outerHtml())) {
                    texts.put(tokenKey.get(), n.outerHtml());
                    String pos = analyzer.getPOS(((TextNode) n).text());
                    if(StringUtils.isNotBlank(pos)){
                        morphTexts.put(tokenKey.get(), pos);
                        morphSequence.put(tokenKey.get(), sequenceLength.addAndGet(pos.length()));
                    }
                    tokenKey.getAndIncrement();
                }
            }else{
                tokenize((Element)n);
            }
        }
        texts.put(tokenKey.getAndIncrement(), tagSuffix(element));
    }

    public static String tagPrefix(Element e) {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(e.tagName());
        for(Attribute attr : e.attributes()){
            sb.append(String.format(" %s=\"%s\"", attr.getKey(), attr.getValue()));
        }
        sb.append(">");
        return sb.toString();
    }

    public static String tagSuffix(Element e) {
        return String.format("</%s>", e.tagName());
    }

    public String joinMorphTexts(){
        return morphTexts.entrySet().stream().map(e -> e.getValue()).collect(Collectors.joining());
    }

    public List<Integer> matches(Integer start, Integer end){
        List<Integer> keyList = new ArrayList<>();
        boolean flag = false;
        Integer beforeSeq = 0;

        for(Integer key : morphSequence.keySet()){
            Integer v = morphSequence.get(key);
            if(beforeSeq <= start && start < v && !flag){
                flag = true;
            }else if(flag && end < v){
                break;
            }
            beforeSeq = v;

            if(flag)
                keyList.add(key);
        }
        return keyList;
    }
}