package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RevenueStatsUI extends JFrame {
    private JLabel totalRevenueLabel, totalOrdersLabel;
    private JTable bestItemsTable;
    private DefaultTableModel bestItemsModel;
    private JPanel chartPanelContainer;

    public RevenueStatsUI() {
        setTitle(" Thống kê doanh thu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        totalRevenueLabel = new JLabel("Tổng doanh thu: 0 đ");
        totalOrdersLabel = new JLabel("Tổng số đơn hàng: 0");

        JButton refreshButton = new JButton(" Làm mới");
        refreshButton.addActionListener(e -> loadStats());

        topPanel.add(totalRevenueLabel);
        topPanel.add(totalOrdersLabel);
        add(topPanel, BorderLayout.NORTH);
        add(refreshButton, BorderLayout.SOUTH);

        // Bảng món bán chạy
        bestItemsModel = new DefaultTableModel();
        bestItemsModel.setColumnIdentifiers(new String[]{"Tên món", "Số lượng đã bán"});
        bestItemsTable = new JTable(bestItemsModel);
        add(new JScrollPane(bestItemsTable), BorderLayout.WEST);

        // Panel biểu đồ
        chartPanelContainer = new JPanel(new BorderLayout());
        add(chartPanelContainer, BorderLayout.CENTER);

        loadStats();
        setVisible(true);
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            // Tổng doanh thu + số đơn
            String sql1 = "SELECT COUNT(*) AS total_orders, SUM(total_price) AS total_revenue FROM orders";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                totalOrdersLabel.setText("Tổng số đơn hàng: " + rs1.getInt("total_orders"));
                totalRevenueLabel.setText("Tổng doanh thu: " + rs1.getDouble("total_revenue") + " đ");
            }

            // Lấy top món ăn và số lượng
            String sql2 = """
                SELECT mi.name, SUM(od.quantity) AS sold
                FROM order_details od
                JOIN menu_items mi ON od.item_id = mi.id
                GROUP BY mi.name
                ORDER BY sold DESC
                LIMIT 10
                """;
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ResultSet rs2 = ps2.executeQuery();

            bestItemsModel.setRowCount(0);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rs2.next()) {
                String name = rs2.getString("name");
                int quantity = rs2.getInt("sold");
                bestItemsModel.addRow(new Object[]{name, quantity});
                dataset.addValue(quantity, "Số lượng", name);
            }

            // Biểu đồ
            JFreeChart chart = ChartFactory.createBarChart(
                    "Top 10 món bán chạy", "Món ăn", "Số lượng bán",
                    dataset
            );
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanelContainer.removeAll();
            chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
            chartPanelContainer.validate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thống kê: " + e.getMessage());
        }
    }
}
