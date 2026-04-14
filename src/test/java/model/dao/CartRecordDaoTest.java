package model.dao;

import connection.DatabaseConnection;
import model.entity.CartRecordEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartRecordDaoTest {

    private CartRecordDao dao;

    @BeforeEach
    void setup() {
        dao = new CartRecordDao();
    }

    // ----------------------------------------------------
    // TEST INSERT()
    // ----------------------------------------------------
    @Test
    void testInsertSuccess() throws Exception {
        CartRecordEntity cartRecord = new CartRecordEntity();
        cartRecord.setTotalItems(3);
        cartRecord.setTotalCost(15.5);
        cartRecord.setLanguage("en");

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockKeys = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPs);

            when(mockPs.getGeneratedKeys()).thenReturn(mockKeys);
            when(mockKeys.next()).thenReturn(true);
            when(mockKeys.getInt(1)).thenReturn(99);

            int id = dao.insert(cartRecord);

            assertEquals(99, id);

            verify(mockPs).setInt(1, 3);
            verify(mockPs).setDouble(2, 15.5);
            verify(mockPs).setString(3, "en");
            verify(mockPs).executeUpdate();
        }
    }

    // ----------------------------------------------------
    // TEST getById()
    // ----------------------------------------------------
    @Test
    void testGetByIdReturnsRecord() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true);

            when(mockRs.getInt("id")).thenReturn(10);
            when(mockRs.getInt("total_items")).thenReturn(5);
            when(mockRs.getDouble("total_cost")).thenReturn(20.0);
            when(mockRs.getString("language")).thenReturn("fi");
            when(mockRs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf("2024-01-01 10:00:00"));

            CartRecordEntity r = dao.getById(10);

            assertNotNull(r);
            assertEquals(10, r.getId());
            assertEquals(5, r.getTotalItems());
            assertEquals(20.0, r.getTotalCost());
            assertEquals("fi", r.getLanguage());
        }
    }

    // ----------------------------------------------------
    // TEST getById() not found cardRecord
    // ----------------------------------------------------
    @Test
    void testGetByIdNotFound() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(false);

            CartRecordEntity r = dao.getById(999);

            assertNull(r);
        }
    }

    // ----------------------------------------------------
    // TEST SQLException
    // ----------------------------------------------------
    @Test
    void testGetByIdThrowsSQLException() throws Exception {
        Connection mockConn = mock(Connection.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(SQLException.class, () -> dao.getById(1));
        }
    }
}
