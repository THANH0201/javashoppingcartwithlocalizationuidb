package model.dao;

import connection.DatabaseConnection;
import model.entity.CartRecordEntity;

import java.sql.*;

public class CartRecordDao {

    public int insert(CartRecordEntity record) throws SQLException {
        String sql = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, record.getTotalItems());
            ps.setDouble(2, record.getTotalCost());
            ps.setString(3, record.getLanguage());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public CartRecordEntity getById(int id) throws SQLException {
        String sql = "SELECT * FROM cart_records WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                CartRecordEntity r = new CartRecordEntity();
                r.setId(rs.getInt("id"));
                r.setTotalItems(rs.getInt("total_items"));
                r.setTotalCost(rs.getDouble("total_cost"));
                r.setLanguage(rs.getString("language"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                return r;
            }
        }
        return null;
    }
}
