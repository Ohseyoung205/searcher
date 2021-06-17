package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.search.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/excel/download")
public class ExcelController {

  @Autowired
  ExcelService fileService;

  @RequestMapping(value = "/searchlog") // 이력현황 전체 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> searchLog() {
    String filename = "searchLog.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadSearchLogAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/searchlog", params = {"field", "keyword"}) // 이력현황 키워드 또는 IP의 검색어에 따른 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> searchLog(@RequestParam(value = "field", required = false, defaultValue = "") String field,
                                            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    String filename = "searchLog.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadSearchLogByKeyword(field, keyword));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/synonym") // 동의어사전 전체 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> synonym() {
    String filename = "synonym.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadSynonymAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/synonym", params = {"field", "keyword"}) // 동의어사전 대표어 또는 동의어의 검색 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> synonym(@RequestParam(value = "field", required = false, defaultValue = "") String field,
                                            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    String filename = "synonym.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadSynonymByKeyword(field, keyword));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/stopword") // 불용어  검색 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> stopWord() {
    String filename = "stopWord.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadStopWordAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/stopword", params = "keyword") // 불용어  검색 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> stopWord(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    String filename = "stopWord.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadStopWordByKeyword(keyword));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/term") // 용어사전 전체 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> termsDict() {
    String filename = "term.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadTermsDictAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/term", params = {"source", "field", "keyword"}) // 용어사전 용어명, 영문명, 약어, 설명의 검색어에 따른 엑셀다운로드 API
  @CrossOrigin(origins="*", allowedHeaders="*")
  public ResponseEntity<Resource> termsDict(@RequestParam(value = "source", required = false, defaultValue = "") String source,
                                            @RequestParam(value = "field", required = false, defaultValue = "") String field,
                                            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    String filename = "term.xlsx";
    InputStreamResource file = source.equals("") ?
            new InputStreamResource(fileService.loadTermsDictByKeywordNoTermsDiv(field, keyword)) :
            new InputStreamResource(fileService.loadTermsDictByKeyword(source, field, keyword));
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }


}
