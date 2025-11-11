package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeeManagerUI extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public EmployeeManagerUI() {
        setTitle("Quản lý nhân viên");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("👨‍💼 Quản lý nhân viên", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Bảng nhân viên
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Họ tên", "Tài khoản", "Chức vụ"});
        employeeTable = new JTable(tableModel);
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // Panel nút và tìm kiếm
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

        // Gắn chức năng
        btnAdd.addActionListener(e -> showAddForm());
        btnEdit.addActionListener(e -> showEditForm());
        btnDelete.addActionListener(e -> deleteSelectedEmployee());
        btnSearch.addActionListener(e -> searchEmployee());

        loadEmployees();
        setVisible(true);
    }

    private void loadEmployees() {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT id, name, username, role FROM employees";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            showError("Lỗi khi tải nhân viên: " + e.getMessage());
        }
    }

    private void showAddForm() {
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField roleField = new JTextField();

        Object[] inputs = {
                "Họ tên:", nameField,
                "Tên tài khoản:", usernameField,
                "Mật khẩu:", passField,
                "Chức vụ:", roleField
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Thêm nhân viên", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO employees(name, username, password, role) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, usernameField.getText());
                ps.setString(3, new String(passField.getPassword()));
                ps.setString(4, roleField.getText());
                ps.executeUpdate();
                loadEmployees();
            } catch (SQLException ex) {
                showError("Lỗi thêm nhân viên: " + ex.getMessage());
            }
        }
    }

    private void showEditForm() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            showError("Vui lòng chọn một nhân viên để sửa.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);
        String currentUsername = (String) tableModel.getValueAt(row, 2);
        String currentRole = (String) tableModel.getValueAt(row, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField usernameField = new JTextField(currentUsername);
        JTextField roleField = new JTextField(currentRole);

        Object[] inputs = {
                "Họ tên:", nameField,
                "Tên tài khoản:", usernameField,
                "Chức vụ:", roleField
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Sửa nhân viên", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE employees SET name=?, username=?, role=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, usernameField.getText());
                ps.setString(3, roleField.getText());
                ps.setInt(4, id);
                ps.executeUpdate();
                loadEmployees();
            } catch (SQLException ex) {
                showError("Lỗi sửa nhân viên: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            showError("Vui lòng chọn một nhân viên để xóa.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM employees WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();
                loadEmployees();
            } catch (SQLException ex) {
                showError("Lỗi xóa nhân viên: " + ex.getMessage());
            }
        }
    }

    private void searchEmployee() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadEmployees(); // Hiện lại tất cả nếu không nhập gì
            return;
        }

        Connection conn = DBConnection.getConnection();
        String sql = "SELECT id, name, username, role FROM employees WHERE name LIKE ? OR username LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            showError("Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "❌ Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
