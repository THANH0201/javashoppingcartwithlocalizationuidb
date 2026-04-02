package localization;

import localization.dao.LocalizationStringDao;
import localization.entity.LocalizationStringEntity;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.Iterator;


public class ExcelLocalizationImporter {

    private static final LocalizationStringDao dao = new LocalizationStringDao();

    /**
     * Import localization data directly from Excel file into database.
     */
    public static void importExcel() {
        try (InputStream is = ExcelLocalizationImporter.class.getResourceAsStream("/localization.xlsx");
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Read header row (key, en, fi, ko, lo, vi...)
            Row headerRow = rowIterator.next();
            int columnCount = headerRow.getLastCellNum();

            String[] languages = new String[columnCount - 1];
            for (int i = 1; i < columnCount; i++) {
                languages[i - 1] = headerRow.getCell(i).getStringCellValue().trim().toLowerCase();
            }

            // Read data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Cell keyCell = row.getCell(0);
                if (keyCell == null) continue;

                String key = keyCell.getStringCellValue().trim();
                if (key.isEmpty()) continue;

                // Loop through languages
                for (int i = 1; i < columnCount; i++) {
                    Cell valueCell = row.getCell(i);
                    String lang = languages[i - 1];

                    if (valueCell == null) continue;

                    String value = valueCell.getStringCellValue().trim();
                    if (value.isEmpty()) continue;

                    // Save or update
                    LocalizationStringEntity existing = dao.findByKeyAndLang(key, lang);

                    if (existing == null) {
                        dao.insert(new LocalizationStringEntity(key, value, lang));
                    } else {
                        existing.setValue(value);
                        dao.update(existing);
                    }
                }
            }

            System.out.println("Excel import completed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to import Excel: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ExcelLocalizationImporter.importExcel();
    }
}
