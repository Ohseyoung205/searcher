package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.search.service.ExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @RequestMapping(value = "/searchlog") // 이력현황 전체 엑셀다운로드 API
  public ResponseEntity<Resource> searchLog() {
    logger.info("searchlog All");
    String filename = "searchLog.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadSearchLogAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/searchlog", params = {"field", "keyword"}) // 이력현황 키워드 또는 IP의 검색어에 따른 엑셀다운로드 API
  public ResponseEntity<Resource> searchLog(@RequestParam(value = "field", required = false, defaultValue = "") String field,
                                            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    logger.info("searchlog fieldandkeyword");
    String filename = "searchLog.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadSearchLogByKeyword(field, keyword));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/customdict") // 동의어사전 전체 엑셀다운로드 API
  public ResponseEntity<Resource> customDict() {
    logger.info("customdict All");
    String filename = "synonyms.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadCustomDictAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/customdict", params = {"field", "keyword"}) // 동의어사전 대표어 또는 동의어의 검색어에 따른 엑셀다운로드 API
  public ResponseEntity<Resource> customDict(@RequestParam(value = "field", required = false, defaultValue = "") String field,
                                            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    logger.info("customdict field and keyword");
    String filename = "synonyms.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadCustomDictByKeyword(field, keyword));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/termsdict") // 용어사전 전체 엑셀다운로드 API
  public ResponseEntity<Resource> termsDict() {
    logger.info("termsdict All");
    String filename = "terms.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadTermsDictAll());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

  @RequestMapping(value = "/termsdict", params = {"field", "keyword"}) // 용어사전 용어명, 영문명, 약어, 설명의 검색어에 따른 엑셀다운로드 API
  public ResponseEntity<Resource> termsDict(@RequestParam(value = "field", required = false, defaultValue = "") String field,
                                             @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
    logger.info("termsdict field and keyword");
    String filename = "terms.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.loadTermsDictByKeyword(field, keyword));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }


}
