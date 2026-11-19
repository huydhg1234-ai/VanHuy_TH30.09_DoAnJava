import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainForm extends JFrame {
    JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtNgaySinh = new JTextField(),
               txtDiaChi = new JTextField(), txtEmail = new JTextField(), txtTim = new JTextField(15);
    JComboBox<String> cboGT = new JComboBox<>(new String[]{"Nam", "Nữ"});
    JComboBox<String> cboLop = new JComboBox<>();
    DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã SV", "Họ tên", "Giới tính", "Ngày sinh", "Địa chỉ", "Email", "Lớp"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable tbl = new JTable(model);

    public MainForm() {
        setTitle("Quản lý sinh viên");
        setSize(950, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Khu vực nhập liệu ---
        JPanel pForm = new JPanel(new GridLayout(4, 4, 10, 10));
        pForm.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        pForm.add(new JLabel("Mã SV:"));      pForm.add(txtMa);
        pForm.add(new JLabel("Họ tên:"));     pForm.add(txtTen);
        pForm.add(new JLabel("Giới tính:"));  pForm.add(cboGT);
        pForm.add(new JLabel("Ngày sinh (yyyy-MM-dd):")); pForm.add(txtNgaySinh);
        pForm.add(new JLabel("Địa chỉ:"));    pForm.add(txtDiaChi);
        pForm.add(new JLabel("Email:"));      pForm.add(txtEmail);
        pForm.add(new JLabel("Lớp:"));        pForm.add(cboLop);

        // --- Nút chức năng ---
        JButton btnThem = new JButton("Thêm"), btnSua = new JButton("Sửa"),
                btnXoa = new JButton("Xóa"), btnMoi = new JButton("Làm mới"),
                btnTim = new JButton("Tìm kiếm"), btnLop = new JButton("Quản lý lớp");
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBtn.add(btnThem); pBtn.add(btnSua); pBtn.add(btnXoa); pBtn.add(btnMoi);
        pBtn.add(new JLabel("   Từ khóa:")); pBtn.add(txtTim); pBtn.add(btnTim);
        pBtn.add(btnLop);

        JPanel north = new JPanel(new BorderLayout());
        north.add(pForm, BorderLayout.CENTER);
        north.add(pBtn, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // --- Sự kiện ---
        btnThem.addActionListener(e -> {
            if (!hopLe()) return;
            exec("INSERT INTO SINHVIEN(MaSV,HoTen,NgaySinh,GioiTinh,DiaChi,Email,MaLop) VALUES(?,?,?,?,?,?,?)",
                    txtMa.getText().trim(), txtTen.getText().trim(), ngay(), cboGT.getSelectedItem(),
                    txtDiaChi.getText().trim(), txtEmail.getText().trim(), cboLop.getSelectedItem());
        });
        btnSua.addActionListener(e -> {
            if (!hopLe()) return;
            exec("UPDATE SINHVIEN SET HoTen=?,NgaySinh=?,GioiTinh=?,DiaChi=?,Email=?,MaLop=? WHERE MaSV=?",
                    txtTen.getText().trim(), ngay(), cboGT.getSelectedItem(),
                    txtDiaChi.getText().trim(), txtEmail.getText().trim(), cboLop.getSelectedItem(),
                    txtMa.getText().trim());
        });
        btnXoa.addActionListener(e -> {
            if (txtMa.getText().trim().isEmpty()) { msg("Hãy chọn sinh viên cần xóa!"); return; }
            if (JOptionPane.showConfirmDialog(this, "Xóa sinh viên này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                exec("DELETE FROM SINHVIEN WHERE MaSV=?", txtMa.getText().trim());
        });
        btnMoi.addActionListener(e -> { clear(); txtTim.setText(""); loadData(""); });
        btnTim.addActionListener(e -> loadData(txtTim.getText().trim()));
        btnLop.addActionListener(e -> new LopForm(this).setVisible(true));

        tbl.getSelectionModel().addListSelectionListener(e -> {
            int r = tbl.getSelectedRow();
            if (r < 0) return;
            txtMa.setText(str(r, 0));
            txtTen.setText(str(r, 1));
            cboGT.setSelectedItem(str(r, 2));
            txtNgaySinh.setText(str(r, 3));
            txtDiaChi.setText(str(r, 4));
            txtEmail.setText(str(r, 5));
            cboLop.setSelectedItem(str(r, 6));
        });

        loadLop();
        loadData("");
    }

    String str(int r, int c) { Object o = model.getValueAt(r, c); return o == null ? "" : o.toString(); }
    void msg(String s) { JOptionPane.showMessageDialog(this, s); }

    // Trả về ngày dạng java.sql.Date, hoặc null nếu để trống (đã kiểm tra trong hopLe)
    java.sql.Date ngay() {
        String s = txtNgaySinh.getText().trim();
        return s.isEmpty() ? null : java.sql.Date.valueOf(s);
    }

    boolean hopLe() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            msg("Mã SV và Họ tên không được để trống!");
            return false;
        }
        String s = txtNgaySinh.getText().trim();
        if (!s.isEmpty()) {
            try { java.sql.Date.valueOf(s); }
            catch (IllegalArgumentException ex) {
                msg("Ngày sinh phải theo dạng yyyy-MM-dd, ví dụ 2005-03-12");
                return false;
            }
        }
        return true;
    }

    void clear() {
        txtMa.setText(""); txtTen.setText(""); txtNgaySinh.setText("");
        txtDiaChi.setText(""); txtEmail.setText("");
        cboGT.setSelectedIndex(0);
        tbl.clearSelection();
    }

    // Nạp danh sách lớp vào combobox (LopForm cũng gọi lại hàm này)
    public void loadLop() {
        cboLop.removeAllItems();
        try (Connection c = DBConnect.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaLop FROM LOP")) {
            while (rs.next()) cboLop.addItem(rs.getString(1));
        } catch (Exception ex) { msg("Lỗi: " + ex.getMessage()); }
    }

    // Nạp dữ liệu vào bảng; keyword rỗng = lấy tất cả
    void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT MaSV,HoTen,GioiTinh,NgaySinh,DiaChi,Email,MaLop FROM SINHVIEN "
                   + "WHERE MaSV LIKE ? OR HoTen LIKE ? OR MaLop LIKE ? OR DiaChi LIKE ?";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) ps.setString(i, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[]{rs.getString("MaSV"), rs.getString("HoTen"),
                        rs.getString("GioiTinh"), rs.getString("NgaySinh"),
                        rs.getString("DiaChi"), rs.getString("Email"), rs.getString("MaLop")});
        } catch (Exception ex) { msg("Lỗi: " + ex.getMessage()); }
    }

    // Chạy câu lệnh INSERT/UPDATE/DELETE
    void exec(String sql, Object... params) {
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            int n = ps.executeUpdate();
            msg(n > 0 ? "Thực hiện thành công!" : "Không có dòng nào bị ảnh hưởng!");
            clear();
            loadData("");
        } catch (Exception ex) { msg("Lỗi: " + ex.getMessage()); }
    }
}
