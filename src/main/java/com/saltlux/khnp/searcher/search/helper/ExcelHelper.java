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
    String[] HEADERs = {"log_keyword", "create_dt", "client_ip" };
    String SHEET = "searchLogs";
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
        row.createCell(1).setCellValue(searchLog.getCreateDt());
        row.createCell(2).setCellValue(searchLog.getClientIp());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  public static ByteArrayInputStream customDictToExcel(List<CustomDict> customDicts) {
//    String[] HEADERs = {"main_word", "sub_word", "word_div", "use_yn", "rec_yn", "create_dt"};
    String[] HEADERs = {"main_word", "sub_word", "create_dt"};
    String SHEET = "synonyms";
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

  public static ByteArrayInputStream termsDictToExcel(List<TermsDict> termsDicts) {
    String[] HEADERs = {"terms_div", "terms_ko_name", "terms_ea_name", "terms_contents", "terms_abr", "create_dt"};
    String SHEET = "synonyms";
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
        row.createCell(3).setCellValue(termsDict.getTermsContents());
        row.createCell(4).setCellValue(termsDict.getTermsAbr());
        row.createCell(5).setCellValue(termsDict.getCreateDt());
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

}
