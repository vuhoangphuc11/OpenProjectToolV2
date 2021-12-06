package com.example.service;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.example.model.OpenProject;
import com.example.model.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
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

    public OpenProjectExportExcel(OpenProject openProject,OpenProject progess) {
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
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID Task", style);
        createCell(row, 1, "Name Task", style);
        createCell(row, 2, "Spent On", style);
        createCell(row, 3, "User", style);
        createCell(row, 4, "Progess", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }  else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (int i = 0; i < openProject.getEmbedded().getTasks().size(); i++) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            Task task = openProject.getEmbedded().getTasks().get(i);

            createCell(row, columnCount++, task.getIdTask(), style);
            createCell(row, columnCount++, task.getNameTask(), style);
            createCell(row, columnCount++, task.getSpentOn(), style);
            createCell(row, columnCount++, task.getLink().getUser().getFullName(), style);
            createCell(row, columnCount++, task.getProgress(), style);
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
