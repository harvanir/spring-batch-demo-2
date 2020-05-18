package org.harvanir.batch.springbatch.batch.writer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.harvanir.batch.springbatch.batch.report.Report;
import org.springframework.batch.item.ItemWriter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Harvan Irsyadi
 */
public class WorkbookItemWriter implements ItemWriter<List<Object>> {

    private final Sheet sheet;

    private int rowNum = 0;

    public WorkbookItemWriter(Workbook workBook, Report report) {
        this.sheet = workBook.createSheet(report.getName());

        writeRow(Arrays.asList(report.getHeader()));
    }

    private String valueOf(Object object) {
        return object != null ? String.valueOf(object) : "";
    }

    private void writeRow(List<Object> values) {
        Row row = sheet.createRow(rowNum++);

        for (int i = 0; i < values.size(); i++) {
            row.createCell(i).setCellValue(valueOf(values.get(i)));
        }
    }

    @Override
    public void write(List<? extends List<Object>> items) {
        items.forEach(this::writeRow);
    }
}