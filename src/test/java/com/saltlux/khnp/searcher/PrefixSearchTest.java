package com.saltlux.khnp.searcher;

import com.saltlux.dor.api.IN2StdSearcher;
import com.saltlux.dor.api.common.query.IN2PrefixQuery;
import com.saltlux.dor.api.common.query.IN2Query;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PrefixSearchTest {

    public static void main(String[] args) throws Exception{
        IN2StdSearcher searcher = new IN2StdSearcher();
        searcher.setServer("192.168.219.2", 10000);
        searcher.addIndex("KHNP_TECH_BACKGROUND_DOCUMENT");
        IN2PrefixQuery q = new IN2PrefixQuery("TITLE4_1", "2.2");
//        IN2PrefixQuery q = new IN2PrefixQuery("INTEGRATION", "안전");
//        searcher.setQuery(q);
        searcher.setQuery(IN2Query.MatchingAllDocQuery());
        searcher.addReturnField(new String[]{"TITLE1", "CONTENT"});
        searcher.setReturnPositionCount(0, 10000);
        searcher.searchDocument();
        System.out.println(searcher.getTotalDocumentCount());

        List<String> lines = new ArrayList<>();
        String main = searcher.getValueInDocument(0, "TITLE1");
        StringBuffer sb = new StringBuffer();
        sb.append(searcher.getValueInDocument(0, "CONTENT"));
        for (int i = 1; i < searcher.getDocumentCount(); i++) {
            String title = searcher.getValueInDocument(i, "TITLE1");
            String cont = searcher.getValueInDocument(i, "CONTENT");
            if(StringUtils.isBlank(title) || StringUtils.isBlank(cont))
                continue;

            if(title.equals(main)){
                sb.append(cont + " ");
                continue;
            }

            if(StringUtils.isNotEmpty(sb)){
                lines.add(title + "|||" + cont);
            }

            main = title;
            sb = new StringBuffer();
        }
        IOUtils.writeLines(lines, "\n", new FileOutputStream(new File("./contents.txt")), "utf-8");
    }
}
