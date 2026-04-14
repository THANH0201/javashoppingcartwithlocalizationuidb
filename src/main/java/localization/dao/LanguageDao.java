package localization.dao;

import connection.DatabaseConnection;
import localization.entity.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LanguageDao {
    static Logger logger = Logger.getLogger(LanguageDao.class.getName());

    public List<Language> findAll() {
        List<Language> list = new ArrayList<>();
        String sql = "SELECT code, name FROM LANGUAGE ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Language(
                        rs.getString("code"),
                        rs.getString("name")
                ));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
        }

        return list;
    }
}
