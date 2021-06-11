package com.saltlux.khnp.searcher.search.controller;

import com.saltlux.khnp.searcher.search.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/excel/download")
public class ExcelController {

  @Autowired
  ExcelService fileService;

  @GetMapping(value = {"/searchlog/{query}", "/searchlog"})
  public ResponseEntity<Resource> searchLog(@PathVariable(required = false) String query) {
    String filename = "searchLog.xlsx";
    InputStreamResource file = new InputStreamResource(fileService.load(query));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
  }

}
