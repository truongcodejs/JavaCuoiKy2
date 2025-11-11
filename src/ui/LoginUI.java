package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final AuthService authService = new AuthService();

    public LoginUI() {
        setTitle("🍔 FastFood - Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel trái - hình ảnh
        JLabel leftImage = new JLabel();
        leftImage.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/login-bg.jpg"))));
        leftImage.setHorizontalAlignment(SwingConstants.CENTER);
        leftImage.setVerticalAlignment(SwingConstants.CENTER);
        leftImage.setPreferredSize(new Dimension(350, 400));
        add(leftImage, BorderLayout.WEST);

        // Panel phải - form đăng nhập
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        formPanel.setPreferredSize(new Dimension(350, getHeight()));

        JLabel title = new JLabel("Đăng nhập hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(220, 53, 69));

        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(30));

        usernameField = createInputField("👤 Tên đăng nhập:");
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createTitledBorder("🔑 Mật khẩu"));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));

        JButton loginBtn = new JButton("Đăng nhập");
        loginBtn.setBackground(new Color(255, 87, 34));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.addActionListener(e -> login());

        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalGlue());

        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JTextField createInputField(String title) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(title));
        return field;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(username, password);  // ✅ Trả về User nếu đúng
        if (user != null) {
            JOptionPane.showMessageDialog(this, " Đăng nhập thành công!");
            dispose();
            new MainMenuUI(user);
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
