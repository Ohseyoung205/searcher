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

  public ByteArrayInputStream loadSearchLogByKeyword(String field, String keyword) {
    List<SearchLog> searchLogs;
    if ("searchWord".equalsIgnoreCase(field)) {
      searchLogs = searchLogRepository.findByLogKeywordContaining(keyword);
    } else if("ip".equalsIgnoreCase(field)) {
      searchLogs = searchLogRepository.findByClientIpContaining(keyword);
    } else {
      searchLogs = searchLogRepository.findByLogKeywordContainingOrClientIpContaining(keyword, keyword);
    }
    
    ByteArrayInputStream in = ExcelHelper.searchLogsToExcel(searchLogs);
    return in;
  }

  public ByteArrayInputStream loadSynonymByKeyword(String field, String keyword) {
    boolean recYn = true;
    List<CustomDict> customDicts;
    if ("mainWord".equalsIgnoreCase(field)) {
      customDicts = customDictRepository.findByWordDivEqualsAndMainWordContainingAndRecYnEquals(CustomDict.WORD_DIV.D, keyword, recYn);
    } else if("subWord".equalsIgnoreCase(field)) {
      customDicts = customDictRepository.findByWordDivEqualsAndSubWordContainingAndRecYnEquals(CustomDict.WORD_DIV.D, keyword, recYn);
    } else {
      customDicts = customDictRepository.findByWordDivExtra(CustomDict.WORD_DIV.D, keyword, recYn);
    }
    ByteArrayInputStream in = ExcelHelper.customDictToExcel(customDicts);
    return in;
  }

  public ByteArrayInputStream loadStopWordByKeyword(String keyword) { //  불용어찾기
    boolean recYn = true;
    List<CustomDict> customDicts = customDictRepository.findByWordDivEqualsAndMainWordContainingAndRecYnEquals(CustomDict.WORD_DIV.B, keyword, recYn);
    ByteArrayInputStream in = ExcelHelper.stopwordsToExcel(customDicts);
    return in;
  }

  //용어집 이름이 있을 때
  public ByteArrayInputStream loadTermsDictByKeyword(String source, String field, String keyword) {
    String recYn = "Y";
    List<TermsDict> termsDicts;
    if ("koName".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsKoNameContainingAndRecYnEquals(source, keyword, recYn);
    } else if("eaName".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsEaNameContainingAndRecYnEquals(source,keyword,recYn);
    } else if("contents".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsContentsContainingAndRecYnEquals(source, keyword, recYn);
    } else if("abr".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsDivEqualsAndTermsAbrContainingAndRecYnEquals(source, keyword, recYn);
    } else {
      termsDicts = termsDictRepository.findByTermsDivExtra(source,keyword, recYn);
    }
    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }

  // 용어집이름이 전체(공백)일 때
  public ByteArrayInputStream loadTermsDictByKeywordNoTermsDiv(String field, String keyword) {
    String recYn = "Y";
    List<TermsDict> termsDicts;
    if ("koName".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsKoNameContainingAndRecYnEquals(keyword, recYn);
    } else if("eaName".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsEaNameContainingAndRecYnEquals(keyword, recYn);
    } else if("contents".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsContentsContainingAndRecYnEquals(keyword, recYn);
    } else if("abr".equalsIgnoreCase(field)) {
      termsDicts = termsDictRepository.findByTermsAbrContainingAndRecYnEquals(keyword, recYn);
    } else {
      termsDicts = termsDictRepository.findByRecYnEqualsYAndKeyword(recYn, keyword);
    }
    ByteArrayInputStream in = ExcelHelper.termsDictToExcel(termsDicts);
    return in;
  }


}
