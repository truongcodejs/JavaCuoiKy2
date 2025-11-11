package dao;

import model.MenuItem;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class MenuItemDAO {
    public List<MenuItem> getAll() {
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items");
            while (rs.next()) {
                items.add(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("image_url")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
