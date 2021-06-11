package com.saltlux.khnp.searcher.search.service;

import com.saltlux.khnp.searcher.search.helper.ExcelHelper;
import com.saltlux.khnp.searcher.search.model.CustomDict;
import com.saltlux.khnp.searcher.search.model.SearchLog;
import com.saltlux.khnp.searcher.search.model.TermsDict;
import com.saltlux.khnp.searcher.search.repository.CustomDictRepository;
import com.saltlux.khnp.searcher.search.repository.SearchLogRepository;
import com.saltlux.khnp.searcher.search.repository.TermsDictRepository;
import com.saltlux.tms3.common.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {
  @Autowired
  SearchLogRepository searchLogRepository;

  @Autowired
  CustomDictRepository customDictRepository;

  @Autowired
  TermsDictRepository termsDictRepository;

  public ByteArrayInputStream loadSearchLogAll() {

    List<SearchLog> searchLogs = searchLogRepository.findAll();

    ByteArrayInputStream in = ExcelHelper.searchLogsToExcel(searchLogs);
    return in;
  }

  public ByteArrayInputStream loadSearchLogByKeyword(String field, String keyword) {

//    field = StringUtils.isEmpty(field) ? "" : field;
//    keyword = StringUtils.isEmpty(keyword) ? "" : keyword;
    List<SearchLog> searchLogs =  new ArrayList<>();;
    if (field.equals("searchword")) {
      searchLogs = searchLogRepository.findByLogKeywordContaining(keyword);
    } else if(field.equals("ip")) {
      searchLogs = searchLogRepository.findByClientIpContaining(keyword);
    } else if(field.equals("")) {
      searchLogs = searchLogRepository.findByLogKeywordContainingOrClientIpContaining(keyword, keyword);
    }
    
    ByteArrayInputStream in = ExcelHelper.searchLogsToExcel(searchLogs);
    return in;
  }

  public ByteArrayInputStream loadCustomDictAll() {

    List<CustomDict> customDicts = customDictRepository.findAll();

    ByteArrayInputStream in = ExcelHelper.customDictToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadCustomDictByKeyword(String field, String keyword) {
//    field = StringUtils.isEmpty(field) ? "" : field;
//    keyword = StringUtils.isEmpty(keyword) ? "" : keyword;
    List<CustomDict> customDicts =  new ArrayList<>();;
    if (field.equals("mainword")) {
      customDicts = customDictRepository.findByMainWordContaining(keyword);
    } else if(field.equals("subword")) {
      customDicts = customDictRepository.findBySubWordContaining(keyword);
    } else if(field.equals("")) {
      customDicts = customDictRepository.findByMainWordContainingOrSubWordContaining(keyword, keyword);
    }
    ByteArrayInputStream in = ExcelHelper.customDictToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadTermsDictAll() {

    List<TermsDict> termsDicts = termsDictRepository.findAll();

    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }

  public ByteArrayInputStream loadTermsDictByKeyword(String field, String keyword) {
//    field = StringUtils.isEmpty(field) ? "" : field;
//    keyword = StringUtils.isEmpty(keyword) ? "" : keyword;
    List<TermsDict> termsDicts =  new ArrayList<>();;
    if (field.equals("korName")) {
      termsDicts = termsDictRepository.findByTermsKoNameContaining(keyword);
    } else if(field.equals("engName")) {
      termsDicts = termsDictRepository.findByTermsEaNameContaining(keyword);
    } else if(field.equals("content")) {
      termsDicts = termsDictRepository.findByTermsContentsContaining(keyword);
    } else if(field.equals("abr")) {
      termsDicts = termsDictRepository.findByTermsAbrContaining(keyword);
    } else if(field.equals("")) {
      termsDicts = termsDictRepository.findByTermsKoNameContainingOrTermsEaNameContainingOrTermsAbrContainingOrTermsAbrContaining(keyword, keyword, keyword, keyword);
    }
    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }


}
