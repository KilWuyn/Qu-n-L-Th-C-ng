package cuoiky;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 150); // Điều chỉnh kích thước
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Thêm khoảng cách

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5)); // Thêm khoảng cách
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Thêm padding
        inputPanel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        inputPanel.add(txtUsername);
        inputPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        inputPanel.add(txtPassword);
        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(); // Panel cho nút để dễ căn chỉnh
        btnLogin = new JButton("Đăng nhập");
        buttonPanel.add(btnLogin);
        add(buttonPanel, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> doLogin());

        // Kiểm tra kết nối khi khởi tạo form, nếu không được thì vô hiệu hóa nút login
        try {
            Connection testConn = DBConnection.getConnection();
            if (testConn != null) {
                testConn.close();
            } else {
                disableLogin("Không thể thiết lập kết nối ban đầu đến CSDL.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            disableLogin("Lỗi kết nối CSDL ban đầu: " + ex.getMessage());
        }

        setVisible(true);
    }

    private void disableLogin(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        btnLogin.setEnabled(false);
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
    }
    
    private void doLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection(); 
            UsersDAO userDAO = new UsersDAO(conn);

            if (userDAO.checkLogin(username, password)) { 
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                dispose(); 
                SwingUtilities.invokeLater(() -> {
						new MainFrame().setVisible(true);
				});
                
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu.", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi khi đăng nhập hoặc kết nối CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close(); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}