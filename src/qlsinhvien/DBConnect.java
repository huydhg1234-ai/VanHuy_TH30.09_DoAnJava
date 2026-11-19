import java.sql.*;

public class DBConnect {
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;"
        + "encrypt=true;trustServerCertificate=true;integratedSecurity=true";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
