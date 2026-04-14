package localization.dao;

import connection.DatabaseConnection;
import localization.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageDaoTest {

    private LanguageDao dao;

    @BeforeEach
    void setup() {
        dao = new LanguageDao();
    }

    @Test
    void testFindAllReturnsLanguages() throws Exception {
        // Mock JDBC objects
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        // Mock static DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Fake result set
            when(mockRs.next()).thenReturn(true, true, false);
            when(mockRs.getString("code")).thenReturn("en", "fi");
            when(mockRs.getString("name")).thenReturn("English", "Finnish");

            // Execute
            List<Language> list = dao.findAll();

            // Verify
            assertEquals(2, list.size());
            assertEquals("en", list.get(0).getCode());
            assertEquals("English", list.get(0).getName());
            assertEquals("fi", list.get(1).getCode());
            assertEquals("Finnish", list.get(1).getName());

            verify(mockConn).prepareStatement("SELECT code, name FROM LANGUAGE ORDER BY name");
            verify(mockPs).executeQuery();
        }
    }

    @Test
    void testFindAllHandlesSQLException() throws Exception {
        Connection mockConn = mock(Connection.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            List<Language> list = dao.findAll();

            // Should return empty list on error
            assertTrue(list.isEmpty());
        }
    }

}
