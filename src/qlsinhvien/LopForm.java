import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LopForm extends JFrame {
    MainForm parent;
    JTextField txtMa = new JTextField(8), txtTen = new JTextField(16);
    JComboBox<String> cboKhoa = new JComboBox<>();
    DefaultTableModel model = new DefaultTableModel(new String[]{"Mã lớp", "Tên lớp", "Mã khoa"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable tbl = new JTable(model);

    public LopForm(MainForm parent) {
        this.parent = parent;
        setTitle("Quản lý lớp");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JButton btnThem = new JButton("Thêm"), btnSua = new JButton("Sửa"), btnXoa = new JButton("Xóa");
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Mã lớp:"));  p.add(txtMa);
        p.add(new JLabel("Tên lớp:")); p.add(txtTen);
        p.add(new JLabel("Khoa:"));    p.add(cboKhoa);
        p.add(btnThem); p.add(btnSua); p.add(btnXoa);
        add(p, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        btnThem.addActionListener(e -> exec("INSERT INTO LOP(MaLop,TenLop,MaKhoa) VALUES(?,?,?)",
                txtMa.getText().trim(), txtTen.getText().trim(), cboKhoa.getSelectedItem()));
        btnSua.addActionListener(e -> exec("UPDATE LOP SET TenLop=?, MaKhoa=? WHERE MaLop=?",
                txtTen.getText().trim(), cboKhoa.getSelectedItem(), txtMa.getText().trim()));
        btnXoa.addActionListener(e -> exec("DELETE FROM LOP WHERE MaLop=?", txtMa.getText().trim()));

        tbl.getSelectionModel().addListSelectionListener(e -> {
            int r = tbl.getSelectedRow();
            if (r < 0) return;
            txtMa.setText(String.valueOf(model.getValueAt(r, 0)));
            txtTen.setText(String.valueOf(model.getValueAt(r, 1)));
            cboKhoa.setSelectedItem(model.getValueAt(r, 2));
        });
        loadKhoa();
        loadData();
    }

    void loadKhoa() {
        cboKhoa.removeAllItems();
        try (Connection c = DBConnect.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaKhoa FROM KHOA")) {
            while (rs.next()) cboKhoa.addItem(rs.getString(1));
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    void loadData() {
        model.setRowCount(0);
        try (Connection c = DBConnect.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaLop,TenLop,MaKhoa FROM LOP")) {
            while (rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    void exec(String sql, Object... params) {
        if (txtMa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã lớp không được để trống!");
            return;
        }
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
            txtMa.setText(""); txtTen.setText("");
            loadData();
            parent.loadLop();   // cập nhật combobox lớp ở form chính
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi (lớp đang có sinh viên thì không xóa được): " + ex.getMessage());
        }
    }
}
