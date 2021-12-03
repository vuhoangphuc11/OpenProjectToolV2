package com.example.service;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.example.model.OpenProject;
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
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Project", style);
        createCell(row, 1, "Employee", style);
        createCell(row, 2, "Planning for Today", style);
        createCell(row, 3, "Report tody", style);
        createCell(row, 4, "Issue", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
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

            createCell(row, columnCount++, openProject.getEmbedded().getTasks().get(i).getIdTask(), style);
            createCell(row, columnCount++, openProject.getEmbedded().getTasks().get(i).getNameTask(), style);
            createCell(row, columnCount++, openProject.getEmbedded().getTasks().get(i).getSpentOn(), style);
            createCell(row, columnCount++, openProject.getEmbedded().getTasks().get(i).getLink().getUser(), style);
            createCell(row, columnCount++, progess.getPercentageDone(), style);
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
