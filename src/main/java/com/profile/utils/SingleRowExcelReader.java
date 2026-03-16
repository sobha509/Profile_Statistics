package com.profile.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

/**
 * Classpath-based Apache POI reader for EXACTLY two data rows.
 * - Place the Excel in src/test/resources
 * - Sheet name: "Person"
 * - Header row (0): Years | Websites | Apps | ExpectedStatus | ExpectedPersona
 * - Data rows: 1 and 2
 * Java 8 compatible.
 */
public class SingleRowExcelReader {

    public static Object[][] readExactlyTwoRows(String resourceName, String sheetName) {
        InputStream is = null;
        Workbook wb = null;

        try {
            is = Thread.currentThread()
                       .getContextClassLoader()
                       .getResourceAsStream(resourceName);

            if (is == null) {
                throw new RuntimeException("Resource not found on test classpath: " + resourceName);
            }

            wb = new XSSFWorkbook(is);
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            // We need header + 2 data rows minimum (0-based lastRowNum >= 2)
            int last = sheet.getLastRowNum();
            if (last < 2) {
                throw new RuntimeException("Expected exactly 2 data rows (rows 1..2). Found only " + last);
            }

            final int COL_YEARS = 0;
            final int COL_WEBS  = 1;
            final int COL_APPS  = 2;
            final int COL_STAT  = 3;
            final int COL_PERS  = 4;

            Object[][] out = new Object[2][5];
            DataFormatter fmt = new DataFormatter();

            for (int i = 0; i < 2; i++) {
                int rowIndex = i + 1; // 1 and 2
                Row r = sheet.getRow(rowIndex);
                if (r == null) throw new RuntimeException("Missing data row at Excel row " + (rowIndex + 1));

                String years  = fmt.formatCellValue(r.getCell(COL_YEARS)).trim();
                String wsStr  = fmt.formatCellValue(r.getCell(COL_WEBS)).trim();
                String appStr = fmt.formatCellValue(r.getCell(COL_APPS)).trim();
                String stat   = fmt.formatCellValue(r.getCell(COL_STAT)).trim();
                String pers   = fmt.formatCellValue(r.getCell(COL_PERS)).trim();

                int websites = parseInt(wsStr,  "Websites", rowIndex);
                int apps     = parseInt(appStr, "Apps",     rowIndex);

                out[i][0] = years;
                out[i][1] = websites;
                out[i][2] = apps;
                out[i][3] = stat;
                out[i][4] = pers;
            }

            return out;

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read exactly two rows: " + e.getMessage(), e);
        } finally {
            try { if (wb != null) wb.close(); } catch (Exception ignored) {}
            try { if (is != null) is.close(); } catch (Exception ignored) {}
        }
    }

    private static int parseInt(String v, String col, int rowIndex) {
        try {
            if (v == null || v.isEmpty()) return 0;
            return Integer.parseInt(v);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid integer in column '" + col +
                "' at Excel row " + (rowIndex + 1) + ": '" + v + "'");
        }
    }
}