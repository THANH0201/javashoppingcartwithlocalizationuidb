package localization.service;

import localization.dao.LocalizationStringDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalizationServiceTest {

    private LocalizationStringDao dao;
    private LocalizationService service;

    @BeforeEach
    void setUp() {
        dao = Mockito.mock(LocalizationStringDao.class);
        service = new LocalizationService(dao);
    }

    @Test
    void testGetLocalizedStrings_ReturnsDaoResult() {
        // Arrange
        Locale locale = Locale.of("en");
        Map<String, String> fakeMap = Map.of("shopping.title", "Shopping Cart");
        when(dao.findAllByLanguage("en")).thenReturn(fakeMap);

        // Act
        Map<String, String> result = service.getLocalizedStrings(locale);

        // Assert
        assertEquals("Shopping Cart", result.get("shopping.title"));
        verify(dao, times(1)).findAllByLanguage("en");
    }

    @Test
    void testGetLocalizedStrings_ReturnsFallback_WhenDaoReturnsNull() {
        // Arrange
        Locale locale = Locale.of("en");
        when(dao.findAllByLanguage("en")).thenReturn(null);

        // Act
        Map<String, String> result = service.getLocalizedStrings(locale);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Select Item", result.get("shopping.select.item"));
    }

    @Test
    void testGetLocalizedStrings_ReturnsFallback_WhenDaoReturnsEmpty() {
        // Arrange
        Locale locale = Locale.of("en");
        when(dao.findAllByLanguage("en")).thenReturn(Map.of());

        // Act
        Map<String, String> result = service.getLocalizedStrings(locale);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Enter Price", result.get("shopping.enter.price"));
    }
}
