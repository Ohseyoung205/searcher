package com.saltlux.khnp.searcher;

import com.saltlux.dor.api.IN2StdIndexer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class TagIndexerTest {
    public static boolean addDoc(String[] arr){
        String id = arr[0];
        String plant = arr[1];
        String tagname = arr[2];
        String description = arr[3];

        IN2StdIndexer indexer = new IN2StdIndexer();
        indexer.setServer("192.168.219.2", 10200);
        indexer.setIndex("KHNP_TAGNAME");

        indexer.addSource("id", id, IN2StdIndexer.SOURCE_TYPE_TEXT);
        indexer.addSource("plant", plant, IN2StdIndexer.SOURCE_TYPE_TEXT);
        indexer.addSource("tagname", tagname, IN2StdIndexer.SOURCE_TYPE_TEXT);
        indexer.addSource("description", description, IN2StdIndexer.SOURCE_TYPE_TEXT);

        indexer.addFieldFTR("id", "id", IN2StdIndexer.TOKENIZER_TERM, true, true);
        indexer.addFieldFTR("plant", "plant", IN2StdIndexer.TOKENIZER_TERM, true, true);
        indexer.addFieldFTR("tagname", "tagname", IN2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
        indexer.addFieldFTR("description", "description", IN2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);
        indexer.addFieldFTR("integration", "id/tagname/description", IN2StdIndexer.TOKENIZER_KOR_BIGRAM, true, true);

        indexer.addFieldTMS("TMS", "KOR", "description", true, 100);

        return indexer.addDocument();
    }

    public static void main(String[] args) throws Exception {
        File tagfile = new File("D:\\workspace\\khnp_lucene\\data\\tagnames.txt");
        List<String> lines = FileUtils.readLines(tagfile, "utf-8");
        lines.stream()
                .filter(line -> !line.startsWith("#"))
                .filter(line -> line.split("\t").length > 3)
                .map(line -> addDoc(line.split("\t")))
                .collect(Collectors.toList());
    }
}
