package model.dao;

import connection.DatabaseConnection;
import model.entity.CartItemEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartItemDao {

    public void insert(CartItemEntity item) throws SQLException {
        String sql = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getCartRecordId());
            ps.setInt(2, item.getItemNumber());
            ps.setDouble(3, item.getPrice());
            ps.setInt(4, item.getQuantity());
            ps.setDouble(5, item.getSubtotal());
            ps.executeUpdate();
        }
    }

    public List<CartItemEntity> getByCartRecordId(int recordId) throws SQLException {
        List<CartItemEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE cart_record_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CartItemEntity item = new CartItemEntity();
                item.setId(rs.getInt("id"));
                item.setCartRecordId(rs.getInt("cart_record_id"));
                item.setItemNumber(rs.getInt("item_number"));
                item.setPrice(rs.getDouble("price"));
                item.setQuantity(rs.getInt("quantity"));
                item.setSubtotal(rs.getDouble("subtotal"));
                list.add(item);
            }
        }
        return list;
    }
}
