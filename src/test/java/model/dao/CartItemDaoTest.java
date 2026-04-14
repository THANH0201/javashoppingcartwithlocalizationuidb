package model.dao;

import connection.DatabaseConnection;
import model.entity.CartItemEntity;
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

class CartItemDaoTest {

    private CartItemDao dao;

    @BeforeEach
    void setup() {
        dao = new CartItemDao();
    }

    // ----------------------------------------------------
    // TEST INSERT()
    // ----------------------------------------------------
    @Test
    void testInsertSuccess() throws Exception {
        CartItemEntity item = new CartItemEntity(1, 2, "Apple", 3.5, 4);

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);

            dao.insert(item);

            verify(mockPs).setInt(1, 1);
            verify(mockPs).setInt(2, 2);
            verify(mockPs).setString(3, "Apple");
            verify(mockPs).setDouble(4, 3.5);
            verify(mockPs).setDouble(5, 4.0);
            verify(mockPs).setDouble(6, 14.0);
            verify(mockPs).executeUpdate();
        }
    }

    // ----------------------------------------------------
    // TEST getByCartRecordId()
    // ----------------------------------------------------
    @Test
    void testGetByCartRecordIdReturnsItems() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Fake 2 rows
            when(mockRs.next()).thenReturn(true, true, false);

            when(mockRs.getInt("id")).thenReturn(1, 2);
            when(mockRs.getInt("cart_record_id")).thenReturn(10, 10);
            when(mockRs.getInt("item_number")).thenReturn(1, 2);
            when(mockRs.getString("item_name")).thenReturn("Apple", "Banana");
            when(mockRs.getDouble("price")).thenReturn(3.5, 2.0);
            when(mockRs.getDouble("quantity")).thenReturn(4.0, 5.0);
            when(mockRs.getDouble("subtotal")).thenReturn(14.0, 10.0);

            List<CartItemEntity> list = dao.getByCartRecordId(10);

            assertEquals(2, list.size());

            assertEquals("Apple", list.get(0).getItemName());
            assertEquals("Banana", list.get(1).getItemName());

            verify(mockPs).setInt(1, 10);
        }
    }

    // ----------------------------------------------------
    // TEST SQLException
    // ----------------------------------------------------
    @Test
    void testGetByCartRecordIdThrowsSQLException() throws Exception {
        Connection mockConn = mock(Connection.class);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            assertThrows(SQLException.class, () -> dao.getByCartRecordId(5));
        }
    }
}
