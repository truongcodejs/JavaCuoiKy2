package dao;

import model.Order;
import model.OrderDetail;
import util.DBConnection;
import java.sql.*;

public class OrderDAO {
    public void saveOrder(Order order) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO orders(user_id, order_time) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUserId());
            stmt.setTimestamp(2, order.getOrderTime());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int orderId = keys.getInt(1);
                for (OrderDetail detail : order.getDetails()) {
                    PreparedStatement dStmt = conn.prepareStatement(
                            "INSERT INTO order_details(order_id, item_id, quantity) VALUES (?, ?, ?)");
                    dStmt.setInt(1, orderId);
                    dStmt.setInt(2, detail.getItemId());
                    dStmt.setInt(3, detail.getQuantity());
                    dStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}