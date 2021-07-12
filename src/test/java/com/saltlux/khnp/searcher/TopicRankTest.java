package com.saltlux.khnp.searcher;

import com.saltlux.dor.api.IN2TMSOldOwlimSearch;
import com.saltlux.dor.api.common.query.IN2Query;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicRankTest {

    public static void main(String[] args) throws Exception {
        IN2TMSOldOwlimSearch searcher = new IN2TMSOldOwlimSearch();
        searcher.setServer("192.168.219.2", 10000);
        searcher.addIndex("SEARCHLOG_KHNP");

        searcher.setQuery(IN2Query.MatchingAllDocQuery());
        searcher.setSearchCount(10000);
        searcher.setDepth(1);
        searcher.setLanguage("KOR");
        searcher.setContentField("TMS_RAW_STREAM");
        searcher.setKeyField("_id");

        searcher.setMainToggle("ROOT");
        if (!searcher.analyzeDocument()) throw new RuntimeException(searcher.getLastErrorMessage());

        String xmlresult = searcher.getTopicRank();
        InputSource xmldoc = new InputSource(new StringReader(xmlresult));
        SAXBuilder builder = new SAXBuilder();

        Document doc = builder.build(xmldoc);

        Element root = doc.getRootElement();
        List<Element> nodes = root.getChildren("Node");
        List<Element> edges = root.getChildren("Edge");

        Set<String> stop = new HashSet<>();
        for (Element node : nodes) {
            String id = node.getAttributeValue("id");
            String name = node.getAttributeValue("name");
            String score = node.getAttributeValue("score");
            String categoty = node.getAttributeValue("categoty");

            if(stop.contains(name) || "ROOT".equals(name)){
                continue;
            }
            System.out.println(String.join(" | ", id, name, score, categoty));
        }

        for (Element child : edges) {
            String fromID = child.getAttributeValue("fromID");
            String toID = child.getAttributeValue("toID");

            if(stop.contains(fromID) || stop.contains(toID))
                continue;
            System.out.println(String.format("from[%s] -> to[%s]", fromID, toID));
        }
    }
}
