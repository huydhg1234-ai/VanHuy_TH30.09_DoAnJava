    import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {
    JTextField txtUser = new JTextField(15);
    JPasswordField txtPass = new JPasswordField(15);

    public LoginForm() {
        setTitle("Đăng nhập");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(3, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(new JLabel("Tài khoản:")); p.add(txtUser);
        p.add(new JLabel("Mật khẩu:"));  p.add(txtPass);
        JButton btn = new JButton("Đăng nhập");
        p.add(new JLabel()); p.add(btn);
        add(p);

        btn.addActionListener(e -> login());
        getRootPane().setDefaultButton(btn);
    }

    void login() {
        String sql = "SELECT * FROM TaiKhoan WHERE username=? AND password=?";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtUser.getText().trim());
            ps.setString(2, new String(txtPass.getPassword()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                new MainForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
