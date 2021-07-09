package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.search.model.PlantInformation;
import com.saltlux.khnp.searcher.search.model.PlantOperationDocument;
import com.saltlux.khnp.searcher.search.repository.PlantInformationRepository;
import com.saltlux.khnp.searcher.search.repository.PlantOperationDocumentRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
public class FilterController {

    @Autowired
    PlantInformationRepository plantInformationRepository;

    @Autowired
    PlantOperationDocumentRepository plantOperationDocumentRepository;

    @GetMapping("/prefix/groupBy/{prefix}")  // 신호데이터 검색의 호기정보 필터 그룹핑
    public CommonResponseVo prefixgroupBy(@PathVariable(value = "prefix") String prefix){
        List<PlantInformation> list = plantInformationRepository.findByPrefixStartsWith(prefix);
        LinkedHashMap<String, String> result = filterName(list, prefix.length());
        return new CommonResponseVo(result);
    }

    private LinkedHashMap<String, String> filterName(List<PlantInformation> list, int len){
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (PlantInformation info : list) {
            List<String> tmp = Arrays.asList(info.getName().split("_"));
            // prefix가 "" 혹은 "2"로 날아올 경우 최상위 단계만 그룹핑
            if (len == 1 && CollectionUtils.isNotEmpty(tmp)) {
                String k = info.getPrefix().substring(1, 2);
                String v = tmp.get(0);
                result.put(k, v);
            } else if (len == 2 || len == 3 && tmp.size() >= 3) {
                String k = info.getPrefix().substring(2, 4);
                String v = tmp.subList(1, 3).stream().collect(Collectors.joining(" "));
                result.put(k, v);
            }
        }
        return result;
    }

    @GetMapping("/operationDocument/groupBy")  // 운영기술지침서 검색의 문서 목록 필터 그룹핑
    public CommonResponseVo operationDocumentgroupBy(@RequestParam(value = "recYn", defaultValue = "true") boolean recYn){
        List<PlantOperationDocument> list = null;
        if(recYn){
            list = plantOperationDocumentRepository.findByDomainTableNotNull();
        }else{
            list = plantOperationDocumentRepository.findAll();
        }
        return new CommonResponseVo(list);
    }
}
