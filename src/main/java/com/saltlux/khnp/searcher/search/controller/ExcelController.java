package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.search.service.ExcelService;
import org.apache.commons.collections4.functors.ExceptionPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/excel/download")
public class ExcelController {

  @Autowired
  ExcelService fileService;

  @RequestMapping(value = "/searchlog") // 이력현황  엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> searchLog(HttpServletRequest request) throws Exception {
    String filename = "searchLog.xlsx";
    String field = request.getParameter("field")==null ? "" : request.getParameter("field");
    String keyword = request.getParameter("keyword")==null ? "" : request.getParameter("keyword");

    InputStreamResource file = new InputStreamResource(fileService.loadSearchLogByKeyword(field, keyword));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/synonym") // 동의어사전 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> synonym(HttpServletRequest request) throws Exception {
    String filename = "synonym.xlsx";
    String field = request.getParameter("field")==null ? "" : request.getParameter("field");
    String keyword = request.getParameter("keyword")==null ? "" : request.getParameter("keyword");
    InputStreamResource file = new InputStreamResource(fileService.loadSynonymByKeyword(field, keyword));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/stopword") // 불용어  검색 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> stopWord(HttpServletRequest request) throws Exception{
    String filename = "stopWord.xlsx";
    String keyword = request.getParameter("keyword")==null ? "" : request.getParameter("keyword");
    InputStreamResource file = new InputStreamResource(fileService.loadStopWordByKeyword(keyword));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/term") // 용어사전 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> termsDict(HttpServletRequest request) throws Exception {
    String filename = "term.xlsx";
    String source = request.getParameter("source")==null ? "" : request.getParameter("source");
    String keyword = request.getParameter("keyword")==null ? "" : request.getParameter("keyword");

    InputStreamResource file = source.equals("") ?
            new InputStreamResource(fileService.loadTermsDictByKeywordNoTermsDiv(keyword)) :
            new InputStreamResource(fileService.loadTermsDictByKeyword(source, keyword));
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }


}
