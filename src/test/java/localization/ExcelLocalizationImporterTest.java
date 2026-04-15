package localization;

import localization.dao.LocalizationStringDao;
import localization.entity.LocalizationStringEntity;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


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
    @Test
    void testSaveLocalization_Insert() throws Exception {
        LocalizationStringDao mockDao = mock(LocalizationStringDao.class);
        ExcelLocalizationImporter importer = new ExcelLocalizationImporter(mockDao);

        when(mockDao.findByKeyAndLang("hello", "en")).thenReturn(null);

        importer.saveLocalization("hello", "en", "Hello");

        verify(mockDao, times(1)).insert(any(LocalizationStringEntity.class));
        verify(mockDao, never()).update(any());
    }
    @Test
    void testSaveLocalization_Update() throws Exception {
        LocalizationStringDao mockDao = mock(LocalizationStringDao.class);
        ExcelLocalizationImporter importer = new ExcelLocalizationImporter(mockDao);

        LocalizationStringEntity existing = new LocalizationStringEntity("hello", "Hi", "en");
        when(mockDao.findByKeyAndLang("hello", "en")).thenReturn(existing);

        importer.saveLocalization("hello", "en", "Hello");

        verify(mockDao, times(1)).update(existing);
        verify(mockDao, never()).insert(any());
    }
    @Test
    void testOpenExcelFile() {
        ExcelLocalizationImporter importer = new ExcelLocalizationImporter();
        Assertions.assertNotNull(importer.openExcelFile());
    }
    @Test
    void testImportExcel() throws Exception {
        LocalizationStringDao mockDao = mock(LocalizationStringDao.class);
        ExcelLocalizationImporter importer = spy(new ExcelLocalizationImporter(mockDao));

        // Fake InputStream
        InputStream fakeStream = new ByteArrayInputStream(new byte[1]);
        doReturn(fakeStream).when(importer).openExcelFile();

        // Mock workbook + sheet + rows
        Workbook workbook = mock(Workbook.class);
        Sheet sheet = mock(Sheet.class);
        Row header = mock(Row.class);
        Row row = mock(Row.class);
        Iterator<Row> iterator = mock(Iterator.class);

        // Iterator: header → row → end
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(header, row);

        when(workbook.getSheetAt(0)).thenReturn(sheet);
        when(sheet.iterator()).thenReturn(iterator);

        // Mock WorkbookFactory and readLanguages
        try (MockedStatic<WorkbookFactory> factory = mockStatic(WorkbookFactory.class);
             MockedStatic<ExcelLocalizationImporter> mockedStatic = mockStatic(ExcelLocalizationImporter.class)) {
            factory.when(() -> WorkbookFactory.create(any(InputStream.class))).thenReturn(workbook);
            mockedStatic.when(() -> ExcelLocalizationImporter.readLanguages(any(Row.class))).thenReturn(new String[]{"en", "fi"});

            importer.importExcel();
        }

        // Verify processRow
        verify(importer, times(1)).processRow(eq(row), any());
    }



}
