package com.saltlux.khnp.searcher;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.Arrays;
import java.util.List;

public class TrieTest {
    private static List<String> plants = Arrays.asList("2436", "2336", "2435", "2335", "2423", "2236", "2323", "2136", "2235", "2135");

    static Trie<String, String> trie = new PatriciaTrie<>();

    public static void main(String[] args) {
        plants.forEach(s -> trie.put(s, s + "한글"));
        print("2");
        print("24");
        print("243");
        print("2436");
    }

    private static void print(String query){
        trie.prefixMap(query)
                .entrySet()
                .forEach(e -> System.out.println(e.getValue()));
    }
}
