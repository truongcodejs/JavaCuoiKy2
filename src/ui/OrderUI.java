package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderUI extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public OrderUI() {
        setTitle("Quản lý đơn hàng");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("📦 Quản lý đơn hàng", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // setup bảng order
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Khách hàng", "Món đã đặt", "Tổng tiền", "Thời gian"});
        orderTable = new JTable(tableModel);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // panel nút chức năng + tìm kiếm
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnSearch = new JButton("Tìm");
        searchField = new JTextField(15);
        controlPanel.add(btnAdd);
        controlPanel.add(btnEdit);
        controlPanel.add(btnDelete);
        controlPanel.add(searchField);
        controlPanel.add(btnSearch);
        add(controlPanel, BorderLayout.SOUTH);

        // gán sự kiện
        btnAdd.addActionListener(e -> showAddForm());
        btnEdit.addActionListener(e -> showEditForm());
        btnDelete.addActionListener(e -> deleteSelectedOrder());
        btnSearch.addActionListener(e -> searchOrder());

        loadOrders();
        setVisible(true);
    }

    private void loadOrders() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, full_name, ordered_items, total_price, order_time FROM orders");
             ResultSet rs = ps.executeQuery()) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("ordered_items"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_time")
                });
            }
        } catch (SQLException e) {
            showError("Lỗi tải danh sách đơn hàng: " + e.getMessage());
        }
    }

    private void showAddForm() {
        JTextField nameField = new JTextField();
        JTextField itemsField = new JTextField();
        JTextField totalField = new JTextField();

        Object[] inputs = {
                "Khách hàng:", nameField,
                "Món đã đặt:", itemsField,
                "Tổng tiền:", totalField
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Thêm đơn hàng", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO orders(full_name, ordered_items, total_price, order_time) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, itemsField.getText());
                ps.setDouble(3, Double.parseDouble(totalField.getText()));
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                loadOrders();
            } catch (SQLException ex) {
                showError("Lỗi thêm đơn hàng: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Tổng tiền phải là số hợp lệ.");
            }
        }
    }

    private void showEditForm() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            showError("Vui lòng chọn đơn hàng để sửa.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);
        String currentItems = (String) tableModel.getValueAt(row, 2);
        String currentTotal = String.valueOf(tableModel.getValueAt(row, 3));

        JTextField nameField = new JTextField(currentName);
        JTextField itemsField = new JTextField(currentItems);
        JTextField totalField = new JTextField(currentTotal);

        Object[] inputs = {
                "Khách hàng:", nameField,
                "Món đã đặt:", itemsField,
                "Tổng tiền:", totalField
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Sửa đơn hàng", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE orders SET full_name=?, ordered_items=?, total_price=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, itemsField.getText());
                ps.setDouble(3, Double.parseDouble(totalField.getText()));
                ps.setInt(4, id);
                ps.executeUpdate();
                loadOrders();
            } catch (SQLException ex) {
                showError("Lỗi cập nhật đơn hàng: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Tổng tiền phải là số hợp lệ.");
            }
        }
    }

    private void deleteSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            showError("Vui lòng chọn đơn hàng để xóa.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đơn này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);

                // Xóa chi tiết đơn hàng trước
                try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM order_details WHERE order_id = ?")) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                }

                // Xóa đơn hàng
                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM orders WHERE id = ?")) {
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }

                conn.commit();
                loadOrders();
            } catch (SQLException ex) {
                showError("Lỗi xóa đơn hàng: " + ex.getMessage());
                try (Connection conn = DBConnection.getConnection()) {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        }
    }

    private void searchOrder() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadOrders();
            return;
        }
        String sql = "SELECT id, full_name, ordered_items, total_price, order_time "
                + "FROM orders WHERE full_name LIKE ? OR ordered_items LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("ordered_items"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_time")
                });
            }
        } catch (SQLException e) {
            showError("Lỗi tìm kiếm đơn hàng: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
