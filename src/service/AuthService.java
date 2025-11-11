package service;

import dao.UserDAO;
import model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Xác thực đăng nhập người dùng.
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User nếu hợp lệ, null nếu sai tài khoản hoặc mật khẩu
     */
    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            System.out.println(" Không tìm thấy người dùng: " + username);
            return null;
        }

        System.out.println(" Đã tìm thấy người dùng: " + user.getUsername());
        System.out.println(" Mật khẩu nhập: " + password);
        System.out.println(" Mật khẩu lưu: " + user.getPassword());

        if (password.equals(user.getPassword())) {
            System.out.println(" Mật khẩu khớp. Đăng nhập thành công.");
            return user;
        } else {
            System.out.println(" Mật khẩu không khớp.");
            return null;
        }
    }
}
