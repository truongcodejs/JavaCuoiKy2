package dao;

import model.User;
import util.DBConnection;
import java.sql.*;

public class UserDAO {
    public User findByUsername(String username) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // DEBUG: In ra thông tin lấy từ DB
                String u = rs.getString("username");
                String p = rs.getString("password");
                String r = rs.getString("role");

                System.out.println(">>> DB trả về: username=" + u + ", password=" + p + ", role=" + r);

                return new User(
                        rs.getInt("id"),
                        u,
                        p,
                        r
                );
            } else {
                System.out.println(">>> Không tìm thấy user với username: " + username);
            }
        } catch (SQLException e) {
            System.out.println(">>> Lỗi SQL:");
            e.printStackTrace();
        }
        return null;
    }
}
