# QLSinhVien - Phần mềm quản lý sinh viên

Đồ án phần mềm Java (Java Swing + JDBC + SQL Server).

## Chức năng
- Form 1: Đăng nhập
- Form 2: Quản lý sinh viên (thêm, sửa, xóa, tìm kiếm)
- Form 3: Quản lý lớp (thêm, sửa, xóa)

## Cơ sở dữ liệu
Database: QuanLySinhVien (SQL Server), gồm các bảng:
SINHVIEN, LOP, KHOA, MONHOC, DIEM, TaiKhoan.
File tạo database và dữ liệu mẫu: QuanLySinhVien.sql

## Cách chạy
1. Mở SQL Server Management Studio (SSMS), chạy file QuanLySinhVien.sql
   để tạo database và dữ liệu.
2. Mở project bằng NetBeans (File > Open Project, chọn thư mục QLSinhVien).
3. Thêm thư viện mssql-jdbc-13.6.2.jre11.jar vào project
   (chuột phải Libraries > Add JAR/Folder).
4. Mở file src/qlsinhvien/DBConnect.java và kiểm tra thông tin kết nối
   cho đúng máy của bạn.
   - Hiện đang dùng Windows Authentication (integratedSecurity=true),
     cần có file mssql-jdbc_auth-x64.dll trong thư mục bin của JDK.
   - Nếu dùng tài khoản SQL Server thì bỏ integratedSecurity=true và
     thêm user/password vào hàm getConnection.
5. Chạy file LoginForm.java.

## Tài khoản đăng nhập phần mềm
- Tài khoản: admin
- Mật khẩu: 123456
