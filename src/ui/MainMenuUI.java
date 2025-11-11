package ui;

import model.MenuItem;
import model.User;
import service.MenuService;
import thread.OrderProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainMenuUI extends JFrame {
    private User user;
    private JPanel menuPanel;
    private final MenuService menuService = new MenuService();

    public MainMenuUI(User user) {
        this.user = user;
        setTitle("FastFood - Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addSidebarMenu();

        JLabel title = new JLabel("📋 Danh sách món ăn", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Panel chứa các món ăn
        menuPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        menuPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        loadMenuItems();
        setVisible(true);
    }

    private void loadMenuItems() {
        List<MenuItem> items = menuService.getAllMenuItems();
        for (MenuItem item : items) {
            System.out.println("Đường dẫn ảnh: " + "/images/" + item.getImageUrl()); // ✅ Bây giờ item đã tồn tại
            JPanel itemCard = createItemCard(item);
            menuPanel.add(itemCard);
        }
    }

    private JPanel createItemCard(MenuItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel imgLabel = new JLabel();
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            String path = "/images/" + item.getImageUrl();
            System.out.println("▶️ Đang cố load ảnh: " + path);
            URL imageUrl = getClass().getResource(path);

            if (imageUrl != null) {
                BufferedImage image = ImageIO.read(imageUrl);
                if (image != null) {
                    Image scaled = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(scaled));
                } else {
                    System.err.println("Ảnh null khi đọc từ stream: " + path);
                    imgLabel.setText("Lỗi ảnh (null)");
                }
            } else {
                System.err.println(" Không tìm thấy ảnh: " + path);
                imgLabel.setText("Không có ảnh");
            }

        } catch (IOException e) {
            e.printStackTrace();
            imgLabel.setText("Lỗi ảnh (IO)");
        }

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("Giá: " + item.getPrice() + " đ");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton orderBtn = new JButton("Thêm vào đơn");
        orderBtn.setBackground(new Color(255, 87, 34));
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFocusPainted(false);
        orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã thêm " + item.getName() + " vào đơn hàng!");
        });

        card.add(imgLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(orderBtn);

        return card;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(160, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 87, 34));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private void addSidebarMenu() {
        JPanel leftMenuPanel = new JPanel();
        leftMenuPanel.setLayout(new BoxLayout(leftMenuPanel, BoxLayout.Y_AXIS));
        leftMenuPanel.setBackground(new Color(245, 245, 245));
        leftMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        leftMenuPanel.setPreferredSize(new Dimension(180, getHeight()));

        // Các nút chức năng
        JButton btnMenu = createSidebarButton("Danh sách món");
        JButton btnOrders = createSidebarButton("Quản lý đơn hàng");
        JButton btnLogout = createSidebarButton("Đăng xuất");

        // Gán sự kiện cho các nút chung
        btnOrders.addActionListener(e -> new OrderUI().setVisible(true));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        // Thêm các nút vào panel
        leftMenuPanel.add(btnMenu);
        leftMenuPanel.add(Box.createVerticalStrut(10));
        leftMenuPanel.add(btnOrders);
        leftMenuPanel.add(Box.createVerticalStrut(10));

        // Nếu là admin thì thêm nút quản lý nhân viên & thống kê
        if ("admin".equalsIgnoreCase(user.getRole().trim())) {
            JButton btnEmployees = createSidebarButton("Quản lý nhân viên");
            JButton btnStats = createSidebarButton("Thống kê");

            btnEmployees.addActionListener(e -> new EmployeeManagerUI().setVisible(true));
            btnStats.addActionListener(e -> new RevenueStatsUI().setVisible(true));

            leftMenuPanel.add(btnEmployees);
            leftMenuPanel.add(Box.createVerticalStrut(10));
            leftMenuPanel.add(btnStats);
            leftMenuPanel.add(Box.createVerticalStrut(10));
        }

        leftMenuPanel.add(Box.createVerticalStrut(10));
        leftMenuPanel.add(btnLogout);

        add(leftMenuPanel, BorderLayout.WEST);
    }
}

