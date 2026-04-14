package localization;

import localization.dao.LocalizationStringDao;
import localization.entity.LocalizationStringEntity;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.io.IOException;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExcelLocalizationImporterTest {
    @Test
    void testReadLanguages() throws IOException {
        Workbook wb = WorkbookFactory.create(true);
        Sheet sheet = wb.createSheet();
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("key");
        header.createCell(1).setCellValue("en");
        header.createCell(2).setCellValue("fi");

        String[] langs = ExcelLocalizationImporter.readLanguages(header);
        Assertions.assertArrayEquals(new String[]{"en", "fi"}, langs);
    }

    @Test
    void testProcessRow() throws Exception {
        LocalizationStringDao mockDao = mock(LocalizationStringDao.class);
        ExcelLocalizationImporter importer = new ExcelLocalizationImporter(mockDao);

        Workbook wb = WorkbookFactory.create(true);
        Sheet sheet = wb.createSheet();

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("hello");
        row.createCell(1).setCellValue("Hello");
        row.createCell(2).setCellValue("Moi");

        String[] langs = {"en", "fi"};

        importer.processRow(row, langs);

        verify(mockDao, times(2)).findByKeyAndLang(eq("hello"), anyString());
        verify(mockDao, times(2)).insert(any(LocalizationStringEntity.class));
    }

    @Test
    void testSaveLocalizationInsertAndUpdate() throws Exception {
        LocalizationStringDao mockDao = mock(LocalizationStringDao.class);
        ExcelLocalizationImporter importer = new ExcelLocalizationImporter(mockDao);

        // Case 1: insert
        when(mockDao.findByKeyAndLang("hello", "en")).thenReturn(null);

        importer.saveLocalization("hello", "en", "Hello");

        verify(mockDao).insert(any(LocalizationStringEntity.class));

        // Case 2: update
        LocalizationStringEntity entity = new LocalizationStringEntity("hello", "Old", "en");
        when(mockDao.findByKeyAndLang("hello", "en")).thenReturn(entity);

        importer.saveLocalization("hello", "en", "New");

        verify(mockDao).update(entity);
    }
}
