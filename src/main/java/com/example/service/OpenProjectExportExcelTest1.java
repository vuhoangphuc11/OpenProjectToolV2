package com.example.service;

import com.example.model.OpenProject;
import com.example.model.Task;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenProjectExportExcelTest1 {

    @Autowired
    private GetOpenProjectDataService service;

    private Workbook workbook;
    private Sheet sheet;
    private final Map<String, Map<String, Set<Task>>> exportData;

    private final OpenProject openProject;

    public OpenProjectExportExcelTest1(OpenProject openProject) {
        this.openProject = openProject;
        workbook = new XSSFWorkbook();
        exportData = new HashMap<>();
    }

    private void writeHeaderLine(String projectName) {
        sheet = workbook.createSheet(projectName);

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 12);
        style.setFont(font);

        // fill foreground color ...
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
        // and solid fill pattern produces solid grey cell fill
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);


        createCell(row, 0, "Date", style);
        createCell(row, 1, "Resource Name", style);
        createCell(row, 2, "Plan To Do (Morning)", style);
        createCell(row, 3, "Done (Evening)", style);
        createCell(row, 4, "Note", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {

        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
        sheet.autoSizeColumn(columnCount);
    }

    private void writeDataLines(Map<String, Set<Task>> mapOfTaskByEmployee) {
        LocalDateTime dateNow = LocalDateTime.now();
        String currentTime = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH).format(dateNow);

        //AtomicInteger rowCount = new AtomicInteger(1);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeight((short) 12);
        style.setFont(font);
        style.setWrapText(true);

        // fill foreground color ...
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
        // and solid fill pattern produces solid grey cell fill
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //set border
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);

        String excelFilePath = "C:\\Users\\vuhoa\\Desktop\\Daily_Report.xlsx";



        try {

            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            int rowCount = sheet.getLastRowNum();
            int count = ++rowCount;
            int finalRowCount = rowCount;

            mapOfTaskByEmployee.forEach((key, value) -> {
                Row row = sheet.createRow(count);

                int columnCount = 0;

                Cell cell = row.createCell(columnCount);
                cell.setCellValue(finalRowCount);

                StringBuilder todoContent = new StringBuilder();
                StringBuilder reportContent =  new StringBuilder();
                Iterator<Task> taskIterator = value.iterator();
                while (taskIterator.hasNext()) {
                    Task task = taskIterator.next();
                    todoContent.append(String.format("#%s: %s", task.getIdTask(), task.getNameTask()));
                    String progress = task.getProgress().equals(100.0) ? "Done" :
                            (task.getProgress().intValue()  + "%");
                    reportContent.append(String.format("#%s: %s (%s)", task.getIdTask(), task.getNameTask(), progress));

                    if (taskIterator.hasNext()) {
                        todoContent.append("\n");
                        reportContent.append("\n");
                    }
                }
                createCell(row, columnCount++, currentTime, style);
                createCell(row, columnCount++, key, style);
                createCell(row, columnCount++, todoContent.toString(), style);
                createCell(row, columnCount++, reportContent.toString(), style);
                createCell(row, columnCount, "", style);
            });

            //merge date
            sheet.addMergedRegion(new CellRangeAddress(1, rowCount - 1, 0, 0));

            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\vuhoa\\Desktop\\Daily_Report.xlsx");
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (IOException | EncryptedDocumentException ex) {
            ex.printStackTrace();
        }


    }

    public void export(HttpServletResponse response) throws IOException {
        prepareDataForExport();
        for (Map.Entry<String, Map<String, Set<Task>>> entry : exportData.entrySet()) {
            writeHeaderLine(entry.getKey());
            writeDataLines(entry.getValue());
        }

//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//
//        outputStream.close();
    }

    private void prepareDataForExport() {
        for (Task task:
                openProject.getEmbedded().getTasks()) {
            String projectName = task.getLink().getProject().getNameProject();
            String employeeName = task.getLink().getUser().getFullName();
            if (!exportData.containsKey(projectName)) {
                Set<Task> listOfTask = new TreeSet<>(Comparator.comparingInt(Task::getIdTask));
                listOfTask.add(task);
                Map<String, Set<Task>>  mapOfTaskByEmployee = new HashMap<>();
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
