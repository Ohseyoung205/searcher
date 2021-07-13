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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {
  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


  public static ByteArrayInputStream searchLogsToExcel(List<SearchLog> searchLogs) {
    String[] HEADERs = {"검색명",  "IP정보" ,"등록일"};
    String SHEET = "Sheet1";
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
    String[] HEADERs = {"대표어", "동의어"};
    String SHEET = "Sheet1";
//    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
//        row.createCell(2).setCellValue(format.format(customeDict.getCreateDt()));
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream stopwordsToExcel(List<CustomDict> customDicts) {
//    String[] HEADERs = {"main_word", "sub_word", "word_div", "use_yn", "rec_yn", "create_dt"};
    String[] HEADERs = {"불용어"};
    String SHEET = "Sheet1";
//    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
//        row.createCell(1).setCellValue(format.format(customeDict.getCreateDt()));

      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream termsDictToExcel(List<TermsDict> termsDicts, ArrayList<String> termsDivs) {
    String[] HEADERs = {"용어집이름",  "영문용어", "한글용어", "약어", "용어설명"};
//    String SHEET = "용어사전";
//    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {

      if(!termsDivs.isEmpty()){
        for (int i = 0; i < termsDivs.size(); i++) {
          String termsDiv = termsDivs.get(i);
          Sheet sheet = workbook.createSheet(termsDiv);
          // Header
          Row headerRow = sheet.createRow(0);

          for (int col = 0; col < HEADERs.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(HEADERs[col]);
          }

          int rowIdx = 1;
          for (TermsDict termsDict : termsDicts) {
            if (termsDiv.equals(termsDict.getTermsDiv())) {
              Row row = sheet.createRow(rowIdx++);
              row.createCell(0).setCellValue(termsDict.getTermsDiv());
              row.createCell(1).setCellValue(termsDict.getTermsEaName());
              row.createCell(2).setCellValue(termsDict.getTermsKoName());
              row.createCell(3).setCellValue(termsDict.getTermsAbr());
              row.createCell(4).setCellValue(termsDict.getTermsContents());
  //        row.createCell(5).setCellValue(format.format(termsDict.getCreateDt()));
            }
          }
        }
    } else {
        Sheet sheet = workbook.createSheet("Sheet1");
        // Header
        Row headerRow = sheet.createRow(0);

        for (int col = 0; col < HEADERs.length; col++) {
          Cell cell = headerRow.createCell(col);
          cell.setCellValue(HEADERs[col]);
        }
      }



      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

}
