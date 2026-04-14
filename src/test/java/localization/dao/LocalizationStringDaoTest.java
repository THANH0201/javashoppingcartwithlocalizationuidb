package localization.dao;

import connection.DatabaseConnection;
import localization.entity.LocalizationStringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalizationStringDaoTest {

    private LocalizationStringDao dao;
    private Connection mockConn;
    private PreparedStatement mockPs;
    private ResultSet mockRs;

    @BeforeEach
    void setup() throws Exception {
        dao = new LocalizationStringDao();
        mockConn = mock(Connection.class);
        mockPs = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
    }

    @Test
    void testInsert() throws Exception {
        LocalizationStringEntity entity = new LocalizationStringEntity("item.apple", "Apple", "en");

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            dao.insert(entity);

            verify(mockPs).setString(1, "item.apple");
            verify(mockPs).setString(2, "Apple");
            verify(mockPs).setString(3, "en");
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void testGetValue() throws Exception {
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("value")).thenReturn("Hello");

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            String value = dao.getValue("greeting", "en");

            assertEquals("Hello", value);
        }
    }

    @Test
    void testFindByKeyAndLang() throws Exception {
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("key")).thenReturn("item.apple");
        when(mockRs.getString("value")).thenReturn("Apple");
        when(mockRs.getString("language")).thenReturn("en");

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            LocalizationStringEntity result = dao.findByKeyAndLang("item.apple", "en");

            assertNotNull(result);
            assertEquals("item.apple", result.getKey());
            assertEquals("Apple", result.getValue());
            assertEquals("en", result.getLanguage());
        }
    }

    @Test
    void testUpdate() throws Exception {
        LocalizationStringEntity entity = new LocalizationStringEntity("item.apple", "Táo", "vi");

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            dao.update(entity);

            verify(mockPs).setString(1, "Táo");
            verify(mockPs).setString(2, "item.apple");
            verify(mockPs).setString(3, "vi");
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void testFindAllByLanguage() throws Exception {
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, true, false);
        when(mockRs.getString("key")).thenReturn("item.apple", "item.banana");
        when(mockRs.getString("value")).thenReturn("Apple", "Banana");

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            Map<String, String> map = dao.findAllByLanguage("en");

            assertEquals(2, map.size());
            assertEquals("Apple", map.get("item.apple"));
            assertEquals("Banana", map.get("item.banana"));
        }
    }

    @Test
    void testGetValueNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(false);

            String result = dao.getValue("key1", "en");

            assertNull(result);
        }
    }

    @Test
    void testGetValueFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("value")).thenReturn("Hello");

            String result = dao.getValue("key1", "en");

            assertEquals("Hello", result);
        }
    }
    @Test
    void testGetValueSQLException() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            // mock prepareStatement
            when(conn.prepareStatement(anyString())).thenReturn(ps);

            when(ps.executeQuery()).thenThrow(new SQLException("DB error"));

            assertThrows(SQLException.class, () -> dao.getValue("key1", "en"));

        }
    }
    @Test
    void testFindByKeyAndLangSQLException() {
        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {
            mocked.when(DatabaseConnection::getConnection).thenThrow(new SQLException("DB error"));

            LocalizationStringEntity result = dao.findByKeyAndLang("a", "b");

            assertNull(result);
        }
    }


}
