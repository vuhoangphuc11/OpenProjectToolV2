package com.example.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.example.model.OpenProject;
import com.example.model.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenProjectExportExcel {

    @Autowired
    private GetOpenProjectDataService service;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private OpenProject openProject;
    private OpenProject progess;

    public OpenProjectExportExcel(OpenProject openProject, OpenProject progess) {
        this.openProject = openProject;
        this.progess = progess;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("OpenProject");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
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
        createCell(row, 1, "FullName", style);
        createCell(row, 2, "ID", style);
        createCell(row, 3, "Name Task   ", style);
        createCell(row, 4, "Progess", style);
        createCell(row, 5, "Spent On", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
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
    }

    private void writeDataLines() {
        LocalDateTime dateNow = LocalDateTime.now();
        String currentTime = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH).format(dateNow);

        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        // fill foreground color ...
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
        // and solid fill pattern produces solid grey cell fill
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //set border
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);

        Task task = null;
        for (int i = 0; i < openProject.getEmbedded().getTasks().size(); i++) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            task = openProject.getEmbedded().getTasks().get(i);

            createCell(row, columnCount++, currentTime, style);
            createCell(row, columnCount++, task.getLink().getUser().getFullName(), style);
            createCell(row, columnCount++, task.getIdTask(), style);
            createCell(row, columnCount++, task.getNameTask(), style);
            createCell(row, columnCount++, task.getProgress(), style);
            createCell(row, columnCount++, task.getSpentOn(), style);
        }

        //merge date
        sheet.addMergedRegion(new CellRangeAddress(1, rowCount - 1, 0, 0));

        //merge id, nameTask, progress
        for(int j = 1 ; j <= openProject.getEmbedded().getTasks().size() ; j++){
            sheet.addMergedRegion(new CellRangeAddress(j, j, 2, 4));
        }

    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
