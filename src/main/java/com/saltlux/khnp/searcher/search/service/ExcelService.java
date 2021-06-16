package com.saltlux.khnp.searcher.search.service;

import com.saltlux.khnp.searcher.search.helper.ExcelHelper;
import com.saltlux.khnp.searcher.search.model.CustomDict;
import com.saltlux.khnp.searcher.search.model.SearchLog;
import com.saltlux.khnp.searcher.search.model.TermsDict;
import com.saltlux.khnp.searcher.search.repository.CustomDictRepository;
import com.saltlux.khnp.searcher.search.repository.SearchLogRepository;
import com.saltlux.khnp.searcher.search.repository.TermsDictRepository;
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
    List<SearchLog> searchLogs =  new ArrayList<>();;
    if (field.equals("searchWord")) {
      searchLogs = searchLogRepository.findByLogKeywordContaining(keyword);
    } else if(field.equals("ip")) {
      searchLogs = searchLogRepository.findByClientIpContaining(keyword);
    } else if(field.equals("")) {
      searchLogs = searchLogRepository.findByLogKeywordContainingOrClientIpContaining(keyword, keyword);
    }
    
    ByteArrayInputStream in = ExcelHelper.searchLogsToExcel(searchLogs);
    return in;
  }

  public ByteArrayInputStream loadSynonymAll() {
    String wordDiv = "D"; //wordDiv 는 D는 대표어
    List<CustomDict> customDicts = customDictRepository.findByWordDivEquals(wordDiv);
    ByteArrayInputStream in = ExcelHelper.customDictToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadSynonymByKeyword(String field, String keyword) {
    String wordDiv = "D"; //wordDiv 는 D는 대표어
    List<CustomDict> customDicts =  new ArrayList<>();;
    if (field.equals("mainWord")) {
      customDicts = customDictRepository.findByWordDivEqualsAndMainWordContaining(wordDiv, keyword);
    } else if(field.equals("subWord")) {
      customDicts = customDictRepository.findByWordDivEqualsAndSubWordContaining(wordDiv, keyword);
    } else if(field.equals("")) {
      customDicts = customDictRepository.findByWordDivExtra(wordDiv,keyword);
    }
    ByteArrayInputStream in = ExcelHelper.customDictToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadStopWordAll() {
    String wordDiv = "B"; //wordDiv 는 B는 불용어
    List<CustomDict> customDicts = customDictRepository.findByWordDivEquals(wordDiv);
    ByteArrayInputStream in = ExcelHelper.stopwordsToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadStopWordByKeyword(String keyword) { //  불용어찾기
    String wordDiv = "B"; //wordDiv 는 B는 불용어
    List<CustomDict> customDicts = customDictRepository.findByWordDivEqualsAndMainWordContaining(wordDiv, keyword);
    ByteArrayInputStream in = ExcelHelper.stopwordsToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadTermsDictAll() {
    List<TermsDict> termsDicts = termsDictRepository.findAll();
    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }

  public ByteArrayInputStream loadTermsDictByKeyword(String source, String field, String keyword) {
    List<TermsDict> termsDicts =  new ArrayList<>();;
    if (field.equals("koName")) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsKoNameContaining(source, keyword);
    } else if(field.equals("eaName")) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsEaNameContaining(source,keyword);
    } else if(field.equals("contents")) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsContentsContaining(source, keyword);
    } else if(field.equals("abr")) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsAbrContaining(source, keyword);
    } else if(field.equals("")) {
      termsDicts = termsDictRepository.findByTermsDivExtra(source,keyword);
    }
    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }

  public ByteArrayInputStream loadTermsDictByKeywordNoTermsDiv(String field, String keyword) {
    List<TermsDict> termsDicts =  new ArrayList<>();;
    if (field.equals("koName")) {
      termsDicts = termsDictRepository.findByTermsKoNameContaining(keyword);
    } else if(field.equals("eaName")) {
      termsDicts = termsDictRepository.findByTermsEaNameContaining(keyword);
    } else if(field.equals("contents")) {
      termsDicts = termsDictRepository.findByTermsContentsContaining(keyword);
    } else if(field.equals("abr")) {
      termsDicts = termsDictRepository.findByTermsAbrContaining(keyword);
    } else if(field.equals("")) {
      termsDicts = termsDictRepository.findByTermsKoNameContainingOrTermsEaNameContainingOrTermsContentsContainingOrTermsAbrContaining(keyword, keyword, keyword, keyword);
    }
    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }


}
