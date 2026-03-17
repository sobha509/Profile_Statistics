package com.profile.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

/**
 * Classpath-based Apache POI reader for EXACTLY four data rows.
 *
 * Place the Excel under src/test/resources (e.g., testdata/Corrected_PersonData_FourUsers.xlsx)
 * Sheet name: "Person"
 *
 * Header row (row 0) expects columns:
 *   Years | Websites | Apps | ExpectedStatus | ExpectedPersona
 *
 * Data rows must be present at row indices: 1, 2, 3, 4 (4 persons).
 * Returns Object[][] for TestNG DataProviders:
 *   { years(String), websites(int), apps(int), expectedStatus(String), expectedPersona(String) }
 */
public class ExcelReader {

    public static Object[][] readExactlyFourRows(String resourceName, String sheetName) {
        InputStream is = null;
        Workbook wb = null;

        try {
            // Load from classpath (src/test/resources -> target/test-classes at runtime)
            is = Thread.currentThread()
                       .getContextClassLoader()
                       .getResourceAsStream(resourceName);

            if (is == null) {
                throw new RuntimeException("Resource not found on classpath: " + resourceName);
            }

            wb = new XSSFWorkbook(is);
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            // We need at least row 4 to exist (0-based lastRowNum >= 4)
            int last = sheet.getLastRowNum();
            if (last < 4) {
                throw new RuntimeException(
                    "Need exactly 4 data rows at row indices 1..4; found lastRowNum=" + last
                );
            }

            // Column indices by header order
            final int C_YEARS = 0;
            final int C_WEBS  = 1;
            final int C_APPS  = 2;
            final int C_STAT  = 3;
            final int C_PERS  = 4;

            Object[][] out = new Object[4][5];
            DataFormatter fmt = new DataFormatter();

            for (int i = 0; i < 4; i++) {
                int rowIndex = i + 1; // 1..4
                Row r = sheet.getRow(rowIndex);
                if (r == null) throw new RuntimeException("Missing data row at index " + rowIndex);

                String years   = fmt.formatCellValue(r.getCell(C_YEARS)).trim();
                String wsStr   = fmt.formatCellValue(r.getCell(C_WEBS)).trim();
                String appStr  = fmt.formatCellValue(r.getCell(C_APPS)).trim();
                String expStat = fmt.formatCellValue(r.getCell(C_STAT)).trim();
                String expPers = fmt.formatCellValue(r.getCell(C_PERS)).trim();

                int websites = parseInt(wsStr,  "Websites", rowIndex);
                int apps     = parseInt(appStr, "Apps",     rowIndex);

                out[i][0] = years;
                out[i][1] = websites;
                out[i][2] = apps;
                out[i][3] = expStat;
                out[i][4] = expPers;
            }

            return out;

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read four rows: " + e.getMessage(), e);
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
