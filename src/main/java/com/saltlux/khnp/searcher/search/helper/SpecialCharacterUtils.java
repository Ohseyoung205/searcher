package com.saltlux.khnp.searcher.search.helper;

public class SpecialCharacterUtils {

    private static final String regex = "()[]{}!|^+-*?:\"\\";

    public static String replaceSpecialChar(String string) {
        StringBuilder sb = new StringBuilder();
        char[] c = string.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (regex.indexOf(c[i]) > -1)
                sb.append("\\");
            sb.append(c[i]);
        }
        return sb.toString();
    }
}
