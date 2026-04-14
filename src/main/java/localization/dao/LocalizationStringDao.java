package localization.dao;

import connection.DatabaseConnection;
import localization.entity.LocalizationStringEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalizationStringDao {
    private static final String ERR_DB = "Database error";

    private static final Logger logger = Logger.getLogger(LocalizationStringDao.class.getName());

    // SonarQube fix: avoid duplicating literal "value"
    private static final String COL_VALUE = "value";

    public void insert(LocalizationStringEntity ls) throws SQLException {
        String sql = "INSERT INTO localization_strings (`key`, value, language) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ls.getKey());
            ps.setString(2, ls.getValue());
            ps.setString(3, ls.getLanguage());
            ps.executeUpdate();
        }
    }

    public String getValue(String key, String language) throws SQLException {
        String sql = "SELECT value FROM localization_strings WHERE `key` = ? AND language = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, language);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(COL_VALUE);
            }
        }
        return null;
    }

    public LocalizationStringEntity findByKeyAndLang(String key, String lang) {
        String sql = "SELECT `key`, `value`, `language` FROM localization_strings WHERE `key` = ? AND language = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, lang);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalizationStringEntity entity = new LocalizationStringEntity();
                entity.setKey(rs.getString("key"));
                entity.setValue(rs.getString(COL_VALUE));
                entity.setLanguage(rs.getString("language"));
                return entity;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, ERR_DB, e);
        }
        return null;
    }

    public void update(LocalizationStringEntity existing) {
        String sql = "UPDATE localization_strings SET value = ? WHERE `key` = ? AND language = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, existing.getValue());
            ps.setString(2, existing.getKey());
            ps.setString(3, existing.getLanguage());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, ERR_DB, e);
        }
    }

    public Map<String, String> findAllByLanguage(String lang) {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lang);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("key"), rs.getString(COL_VALUE));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, ERR_DB, e);
        }

        return map;
    }
}
