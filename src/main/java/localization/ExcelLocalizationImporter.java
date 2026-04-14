package localization;

import localization.dao.LocalizationStringDao;
import localization.entity.LocalizationStringEntity;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

public class ExcelLocalizationImporter {

    private static final Logger logger = Logger.getLogger(ExcelLocalizationImporter.class.getName());
    private final LocalizationStringDao dao;

    public ExcelLocalizationImporter() {
        this.dao = new LocalizationStringDao();
    }

    // Constructor for testing
    public ExcelLocalizationImporter(LocalizationStringDao dao) {
        this.dao = dao;
    }

    public void importExcel() {
        try (InputStream is = openExcelFile();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            String[] languages = readLanguages(rowIterator.next());

            while (rowIterator.hasNext()) {
                processRow(rowIterator.next(), languages);
            }

            logger.info("Excel import completed successfully!");

        } catch (Exception e) {
            logger.severe("Failed to import Excel: " + e.getMessage());
        }
    }

    static String[] readLanguages(Row headerRow) {
        int columnCount = headerRow.getLastCellNum();
        String[] languages = new String[columnCount - 1];

        for (int i = 1; i < columnCount; i++) {
            languages[i - 1] = headerRow.getCell(i).getStringCellValue().trim().toLowerCase();
        }
        return languages;
    }

    void processRow(Row row, String[] languages) throws SQLException {
        Cell keyCell = row.getCell(0);
        if (keyCell == null) return;

        String key = keyCell.getStringCellValue().trim();
        if (key.isEmpty()) return;

        for (int i = 1; i <= languages.length; i++) {

            Cell valueCell = row.getCell(i);
            String value = (valueCell != null)
                    ? valueCell.getStringCellValue().trim()
                    : "";

            if (value.isEmpty()) {
                continue; // Only one continue allowed
            }

            saveLocalization(key, languages[i - 1], value);
        }
    }

    void saveLocalization(String key, String lang, String value) throws SQLException {
        LocalizationStringEntity existing = dao.findByKeyAndLang(key, lang);

        if (existing == null) {
            dao.insert(new LocalizationStringEntity(key, value, lang));
        } else {
            existing.setValue(value);
            dao.update(existing);
        }
    }

    InputStream openExcelFile() {
        return getClass().getResourceAsStream("/localization.xlsx");
    }

    public static void main(String[] args) {
        new ExcelLocalizationImporter().importExcel();
    }
}
