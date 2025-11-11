package thread;

import model.Order;
import model.OrderDetail;
import util.DBConnection;
import util.FileLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderProcessor extends Thread {
    private Order order;

    public OrderProcessor() {
        this.order = order;
    }

    @Override
    public void run() {
        FileLogger.log(" Đang xử lý đơn hàng ID: " + order.getId());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String insertOrderSQL = "INSERT INTO orders (id, user_id, created_at, total_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertOrderSQL)) {
                stmt.setInt(1, order.getId());
                stmt.setInt(2, order.getUserId());
                stmt.setTimestamp(3, order.getOrderTime());

                double total = 0;
                for (OrderDetail detail : order.getDetails()) {
                    try (PreparedStatement priceStmt = conn.prepareStatement("SELECT price FROM menu_items WHERE id = ?")) {
                        priceStmt.setInt(1, detail.getItemId());
                        var rs = priceStmt.executeQuery();
                        if (rs.next()) {
                            total += rs.getDouble("price") * detail.getQuantity();
                        }
                    }
                }
                stmt.setDouble(4, total);
                stmt.executeUpdate();
            }

            // 2. Ghi vào bảng order_details
            String insertDetailSQL = "INSERT INTO order_details (order_id, item_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertDetailSQL)) {
                for (OrderDetail detail : order.getDetails()) {
                    stmt.setInt(1, order.getId());
                    stmt.setInt(2, detail.getItemId());
                    stmt.setInt(3, detail.getQuantity());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            FileLogger.log(" Đã hoàn tất đơn hàng ID: " + order.getId());

        } catch (SQLException e) {
            FileLogger.log(" Lỗi khi xử lý đơn hàng ID " + order.getId() + ": " + e.getMessage());
        }
    }

    public void setVisible(boolean b) {
    }
}
