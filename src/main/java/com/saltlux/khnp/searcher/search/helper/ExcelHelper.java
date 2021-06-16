package com.saltlux.khnp.searcher.search.helper;

import com.saltlux.khnp.searcher.search.model.CustomDict;
import com.saltlux.khnp.searcher.search.model.SearchLog;
import com.saltlux.khnp.searcher.search.model.TermsDict;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {
  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


  public static ByteArrayInputStream searchLogsToExcel(List<SearchLog> searchLogs) {
    String[] HEADERs = {"검색명",  "IP정보" ,"등록일"};
    String SHEET = "이력현황";
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
      Sheet sheet = workbook.createSheet(SHEET);

      // Header
      Row headerRow = sheet.createRow(0);

      for (int col = 0; col < HEADERs.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(HEADERs[col]);
      }

      int rowIdx = 1;
      for (SearchLog searchLog : searchLogs) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(searchLog.getLogKeyword());
        row.createCell(1).setCellValue(searchLog.getClientIp());
        row.createCell(2).setCellValue(searchLog.getCreateDt());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream customDictToExcel(List<CustomDict> customDicts) {
//    String[] HEADERs = {"main_word", "sub_word", "word_div", "use_yn", "rec_yn", "create_dt"};
    String[] HEADERs = {"대표어", "동의어", "등록일"};
    String SHEET = "동의어";
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
      Sheet sheet = workbook.createSheet(SHEET);

      // Header
      Row headerRow = sheet.createRow(0);

      for (int col = 0; col < HEADERs.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(HEADERs[col]);
      }

      int rowIdx = 1;
      for (CustomDict customeDict : customDicts) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(customeDict.getMainWord());
        row.createCell(1).setCellValue(customeDict.getSubWord());
        row.createCell(2).setCellValue(customeDict.getCreateDt());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream stopwordsToExcel(List<CustomDict> customDicts) {
//    String[] HEADERs = {"main_word", "sub_word", "word_div", "use_yn", "rec_yn", "create_dt"};
    String[] HEADERs = {"불용어", "등록일"};
    String SHEET = "불용어";
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
      Sheet sheet = workbook.createSheet(SHEET);

      // Header
      Row headerRow = sheet.createRow(0);

      for (int col = 0; col < HEADERs.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(HEADERs[col]);
      }

      int rowIdx = 1;
      for (CustomDict customeDict : customDicts) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(customeDict.getMainWord());
        row.createCell(1).setCellValue(customeDict.getCreateDt());

      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream termsDictToExcel(List<TermsDict> termsDicts) {
    String[] HEADERs = {"구분", "용어명", "영문명",  "약어", "설명", "등록일"};
    String SHEET = "용어사전";
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
      Sheet sheet = workbook.createSheet(SHEET);

      // Header
      Row headerRow = sheet.createRow(0);

      for (int col = 0; col < HEADERs.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(HEADERs[col]);
      }

      int rowIdx = 1;
      for (TermsDict termsDict : termsDicts) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(termsDict.getTermsDiv());
        row.createCell(1).setCellValue(termsDict.getTermsKoName());
        row.createCell(2).setCellValue(termsDict.getTermsEaName());
        row.createCell(3).setCellValue(termsDict.getTermsAbr());
        row.createCell(4).setCellValue(termsDict.getTermsContents());
        row.createCell(5).setCellValue(termsDict.getCreateDt());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

}
