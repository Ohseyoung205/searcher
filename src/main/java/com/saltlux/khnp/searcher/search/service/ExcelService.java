package com.saltlux.khnp.searcher.search.service;

import com.saltlux.khnp.searcher.search.helper.ExcelHelper;
import com.saltlux.khnp.searcher.search.model.SearchLog;
import com.saltlux.khnp.searcher.search.repository.SearchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class ExcelService {
  @Autowired
  SearchLogRepository repository;

  public ByteArrayInputStream load(String query) {
    query = StringUtils.isEmpty(query) ? "" : query;
    List<SearchLog> searchLogs = repository.findByLogKeywordContaining(query);

    ByteArrayInputStream in = ExcelHelper.searchLogsToExcel(searchLogs);
    return in;
  }

}
