package com.saltlux.khnp.searcher;

import org.ahocorasick.trie.Token;
import org.ahocorasick.trie.Trie;

import java.util.Collection;

public class AhoCoraTest {
    public static void main(String[] args) {
        String speech = "The Answer to the Great Question... Of Life, " +
                "the Universe and Everything... Is... Forty-two,' said " +
                "Deep Thought, with infinite majesty and calm.";

        Trie trie = Trie.builder().ignoreOverlaps().onlyWholeWords().ignoreCase()
                .addKeyword("great question")
                .addKeyword("forty-two")
                .addKeyword("deep thought")
                .build();

        Collection<Token> tokens = trie.tokenize(speech);
        StringBuilder html = new StringBuilder();

        for (Token token : tokens) {
            if (token.isMatch()) {
                html.append("<엑엑>");
            }
            html.append(token.getFragment());
            if (token.isMatch()) {
                html.append("</윽윽>");
            }
        }

        System.out.println(html);
    }
}
