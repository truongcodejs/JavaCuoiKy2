package service;

import model.Cart;
import model.CartItem;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.StringJoiner;

public class OrderService {
    public void saveOrder(String fullName, Cart cart) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String insertOrderSql = "INSERT INTO orders (full_name, ordered_items, total_price, order_time) VALUES (?, ?, ?, ?)";
            PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS);

            StringJoiner itemsJoiner = new StringJoiner(", ");
            for (CartItem item : cart.getItems()) {
                itemsJoiner.add(item.getMenuItem().getName() + " x" + item.getQuantity());
            }

            psOrder.setString(1, fullName);
            psOrder.setString(2, itemsJoiner.toString());
            psOrder.setDouble(3, cart.getTotal());
            psOrder.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            psOrder.executeUpdate();

            int orderId = 0;
            var rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            String insertDetailSql = "INSERT INTO order_details (order_id, item_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(insertDetailSql);
            for (CartItem item : cart.getItems()) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, item.getMenuItem().getId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.addBatch();
            }
            psDetail.executeBatch();

            conn.commit(); // Xác nhận giao dịch

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback(); // Nếu lỗi thì rollback
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
