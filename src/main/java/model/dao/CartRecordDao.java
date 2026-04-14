package model.dao;

import connection.DatabaseConnection;
import model.entity.CartRecordEntity;

import java.sql.*;

public class CartRecordDao {

    public int insert(CartRecordEntity cartRecord) throws SQLException {
        String sql = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cartRecord.getTotalItems());
            ps.setDouble(2, cartRecord.getTotalCost());
            ps.setString(3, cartRecord.getLanguage());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public CartRecordEntity getById(int id) throws SQLException {
        String sql = """
        SELECT
            id,
            total_items,
            total_cost,
            language,
            created_at
        FROM cart_records
        WHERE id = ?
        """;

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
