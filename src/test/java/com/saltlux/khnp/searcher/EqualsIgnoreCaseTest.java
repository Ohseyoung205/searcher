package com.saltlux.khnp.searcher;

import java.util.ArrayList;
import java.util.Iterator;

public class EqualsIgnoreCaseTest {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        Iterator<String> iter = list.iterator();
        while (iter.hasNext()){
            String s = iter.next();
            if("B".equalsIgnoreCase(s)){
                iter.remove();
            }
        }
        System.out.println(list);
    }
}
