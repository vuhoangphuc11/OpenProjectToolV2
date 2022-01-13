package com.example.service;

import com.example.model.OpenProject;
import com.example.model.Task;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OpenProjectExportExcel {

  private XSSFWorkbook workbook;
  private XSSFSheet sheet;
  private final Map<String, Map<String, Set<Task>>> exportData;
  private final String fileName = "Daily_Report.xlsx";
  private final String date;

  private final OpenProject openProject;

  public OpenProjectExportExcel(OpenProject openProject, String date) {
    this.openProject = openProject;
    this.date = date;
    workbook = new XSSFWorkbook();
    exportData = new HashMap<>();
  }

  private void writeHeaderLine(String projectName) {
    sheet = workbook.createSheet(projectName);

    Row row = sheet.createRow(0);

    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setFontHeight(12);
    font.setFontName("Calibri");

    style.setFont(font);
    // fill foreground color ...
    style.setFillForegroundColor(IndexedColors.LIME.index);
    // and solid fill pattern produces solid grey cell fill
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);


    createCell(row, 0, "Date", style);
    createCell(row, 1, "Resource Name", style);
    createCell(row, 2, "Plan To Do (Morning)", style);
    createCell(row, 3, "Done (Evening)", style);
    createCell(row, 4, "Note", style);
  }

  private void createCell(Row row, int columnCount, Object value, CellStyle style) {

    Cell cell = row.createCell(columnCount);
    cell.setCellValue(String.valueOf(value));
    cell.setCellStyle(style);
    sheet.autoSizeColumn(columnCount);
  }

  private void writeDataLines(Map<String, Set<Task>> mapOfTaskByEmployee, String projectName) {

    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setFontHeight(12);
    font.setFontName("Calibri");

    style.setFont(font);
    style.setWrapText(true);

    //demo
    CellStyle styleDate =  workbook.createCellStyle();

    styleDate.setFont(font);
    styleDate.setWrapText(true);

    /* Center Align Cell Contents */
    styleDate.setAlignment(HorizontalAlignment.CENTER);
    styleDate.setVerticalAlignment(VerticalAlignment.CENTER);
    //demo


//    if (style.getIndex() % 2 == 0 && styleDate.getIndex() % 2 == 0) {
//      style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//      styleDate.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//    } else {
//      style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
//      styleDate.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
//    }

    // and solid fill pattern produces solid grey cell fill
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    // and solid fill pattern produces solid grey cell fill date
    styleDate.setFillPattern(FillPatternType.SOLID_FOREGROUND);


    //set border date
    styleDate.setBorderBottom(BorderStyle.THIN);
    styleDate.setBorderLeft(BorderStyle.THIN);
    styleDate.setBorderRight(BorderStyle.THIN);
    styleDate.setBorderTop(BorderStyle.THIN);
    //set border
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);

    sheet = workbook.getSheet(projectName);

    AtomicInteger starRow = new AtomicInteger(sheet.getLastRowNum() + 1);
    AtomicInteger rowCount = new AtomicInteger(starRow.get());

    //key: date, value: map<employee, tasks>
    Map<String, Map<String, List<Task>>> mapOfTasks = new TreeMap<>();
    mapOfTaskByEmployee.forEach((username, tasks) -> {
      for (Task task : tasks) {
        if (mapOfTasks.get(task.getSpentOn()) == null) {
          Map<String, List<Task>> taskOfemployee = new HashMap<>();
          List<Task> taskList = new ArrayList<>();
          taskList.add(task);
          taskOfemployee.put(username, taskList);
          mapOfTasks.put(task.getSpentOn(), taskOfemployee);
        } else {
          Map<String, List<Task>> taskOfemployee = mapOfTasks.get(task.getSpentOn());
          List<Task> taskList = taskOfemployee.get(username);
          if (taskList != null) {
            taskList.add(task);
          } else {
            taskList = new ArrayList<>();
            taskList.add(task);
            taskOfemployee.put(username, taskList);
          }
        }
      }
    });

    mapOfTasks.forEach((dateStr, tasksOfemployee) -> {


      tasksOfemployee.forEach((username, tasks) -> {
        Row row = sheet.createRow(rowCount.getAndIncrement());


        AtomicInteger columnCount = new AtomicInteger();

        Cell cell = row.createCell(columnCount.get());
        // cell.setCellValue(finalRowCount);
        StringBuilder todoContent = new StringBuilder();
        StringBuilder reportContent = new StringBuilder();
        Iterator<Task> taskIterator = tasks.iterator();
        while (taskIterator.hasNext()) {
          Task task = taskIterator.next();
          todoContent.append(String.format("#%s: %s", task.getIdTask(), task.getNameTask()));
          String progress =
              task.getProgress().equals(100.0) ? "Done" : (task.getProgress().intValue() + "%");
          reportContent.append(
              String.format("#%s: %s (%s)", task.getIdTask(), task.getNameTask(), progress));

          if (taskIterator.hasNext()) {
            todoContent.append("\n");
            reportContent.append("\n");
          }
        }

        if(cell.getRowIndex() % 2 == 0){
          style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
          styleDate.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        }else{
          style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
          styleDate.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        }


        createCell(row, columnCount.getAndIncrement(), dateStr, styleDate);
        createCell(row, columnCount.getAndIncrement(), username, style);
        createCell(row, columnCount.getAndIncrement(), todoContent.toString(), style);
        createCell(row, columnCount.getAndIncrement(), reportContent.toString(), style);
        createCell(row, columnCount.get(), "", style);
      });

      //merge date
      if (rowCount.get() - starRow.get() > 1) {
        sheet.addMergedRegion(new CellRangeAddress(starRow.get(), rowCount.get() - 1, 0, 0));
      }
      starRow.set(rowCount.get());

    });


//
//        mapOfTaskByEmployee.forEach((key, value) -> {
//            Row row = sheet.createRow(rowCount.getAndIncrement());
//
//            int columnCount = 0;
//
//            Cell cell = row.createCell(columnCount);
//            // cell.setCellValue(finalRowCount);
//
//            StringBuilder todoContent = new StringBuilder();
//            StringBuilder reportContent = new StringBuilder();
//            Iterator<Task> taskIterator = value.iterator();
//            while (taskIterator.hasNext()) {
//                Task task = taskIterator.next();
//                todoContent.append(String.format("#%s: %s", task.getIdTask(), task.getNameTask()));
//                String progress = task.getProgress().equals(100.0) ? "Done" :
//                        (task.getProgress().intValue() + "%");
//                reportContent.append(String.format("#%s: %s (%s)", task.getIdTask(), task.getNameTask(), progress));
//
//                if (taskIterator.hasNext()) {
//                    todoContent.append("\n");
//                    reportContent.append("\n");
//                }
//            }
//            createCell(row, columnCount++, date, style);
//            createCell(row, columnCount++, key, style);
//            createCell(row, columnCount++, todoContent.toString(), style);
//            createCell(row, columnCount++, reportContent.toString(), style);
//            createCell(row, columnCount, "", style);
//        });


  }

  public void export(String filePath) throws IOException {
    prepareDataForExport();
    String excelFilePath = filePath + fileName;
    FileInputStream inputStream = null;
    FileOutputStream outputStream = null;
    try {

      File inputFile = new File(excelFilePath);
      if (inputFile.isFile()) {
        inputStream = new FileInputStream(inputFile);
        workbook = new XSSFWorkbook(inputStream);
      }

      for (Map.Entry<String, Map<String, Set<Task>>> entry : exportData.entrySet()) {
        if (Objects.isNull(workbook.getSheet(entry.getKey()))) {
          writeHeaderLine(entry.getKey());
        }
        writeDataLines(entry.getValue(), entry.getKey());
      }

      if (inputStream != null) {
        inputStream.close();
      }

      outputStream = new FileOutputStream(excelFilePath);
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
    } catch (IOException | EncryptedDocumentException ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

  private void prepareDataForExport() {
    for (Task task : openProject.getEmbedded().getTasks()) {
      String projectName = task.getLink().getProject().getNameProject();
      String employeeName = task.getLink().getUser().getFullName();
      if (!exportData.containsKey(projectName)) {
        Set<Task> listOfTask = new TreeSet<>(Comparator.comparingInt(Task::getIdTask));
        listOfTask.add(task);
        Map<String, Set<Task>> mapOfTaskByEmployee = new HashMap<>();
        mapOfTaskByEmployee.put(employeeName, listOfTask);

        exportData.put(projectName, mapOfTaskByEmployee);
      } else {
        Map<String, Set<Task>> mapOfTaskByEmployee = exportData.get(projectName);
        if (mapOfTaskByEmployee.containsKey(employeeName)) {
          mapOfTaskByEmployee.get(employeeName).add(task);
        } else {
          Set<Task> listOfTask = new TreeSet<>(Comparator.comparingInt(Task::getIdTask));
          listOfTask.add(task);
          mapOfTaskByEmployee.put(employeeName, listOfTask);
        }
      }
    }
  }
}
