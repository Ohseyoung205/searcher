package com.saltlux.khnp.searcher.dummy.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/plant")
public class DummyController {

    /*
    * 고리: 1 월성: 2 한빛: 3 한울: 4 신한울: 7 새울: 8
    * [1발전소(1,2호기): 1] [2발전소(3,4호기): 2] [3발전소(5,6호기): 3]
    * ?호기 : ?
    * */

    private List<String> plants = Arrays.asList("2135", "2136", "2235", "2236", "2323", "2335", "2336", "2423", "2435", "2436");

    private Trie<String, String> trie = new PatriciaTrie<>();

    private Map<Integer, Map<Integer, String>> map = new HashMap();

    @PostConstruct
    public void init(){

        Map<Integer, String> map1 = new HashMap();
        map1.put(1, "고리");
        map1.put(2, "월성");
        map1.put(3, "한빛");
        map1.put(4, "한울");
        map1.put(7, "신한울");
        map1.put(8, "새울");
        map.put(1, map1);

        Map<Integer, String> map2 = new HashMap();
        map2.put(1, "1발전소(1,2호기)");
        map2.put(2, "2발전소(3,4호기)");
        map2.put(3, "3발전소(5,6호기)");
        map.put(2, map2);

        Map<Integer, String> map3 = new HashMap();
        map3.put(1, "1호기");
        map3.put(2, "2호기");
        map3.put(3, "3호기");
        map3.put(4, "4호기");
        map3.put(5, "5호기");
        map3.put(6, "6호기");
        map3.put(7, "7호기");
        map3.put(8, "8호기");
        map.put(3, map3);

        plants.forEach(s -> trie.put(s, s));
    }

    @GetMapping("/groupBy/{prefix}")
    public CommonResponseVo plantGroupBy(@PathVariable String prefix) {
        return new CommonResponseVo(replace(prefix));
    }

    private List<String> replace(String prefix){
        List<String> list = trie.prefixMap(prefix).keySet().stream().collect(Collectors.toList());
        int prefixLength = prefix.length();

        if(!map.containsKey(prefixLength) || CollectionUtils.isEmpty(list)){
            return Collections.EMPTY_LIST;
        }

        Map<Integer, String> m = map.get(prefixLength);
        return list.stream()
                .map(str -> str.substring(prefixLength, prefixLength + 1))
                .map(s -> Integer.valueOf(s))
                .map(i -> m.get(i))
                .filter(s -> StringUtils.isNotEmpty(s))
                .distinct()
                .collect(Collectors.toList());
    }

    @GetMapping("/name/{prefix}")
    public CommonResponseVo name(@PathVariable String prefix) {
        if(prefix.length() != 4){
            return new CommonResponseVo(prefix);
        }

        String name = IntStream.of(1, 2, 3).boxed()
                .map(i -> map.get(i).get(Integer.valueOf(prefix.substring(i, i + 1))))
                .filter(r -> StringUtils.isNotEmpty(r))
                .collect(Collectors.joining(" "));
        return new CommonResponseVo(name);
    }
}
