package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:8889/db_busrental";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Sesuaikan dengan password MySQL Anda
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Driver MySQL tidak ditemukan!\n" + e.getMessage(), 
                "Error Driver", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Koneksi database gagal!\n" + e.getMessage(), 
                "Error Koneksi", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error saat menutup koneksi: " + e.getMessage());
        }
    }
    
    public static boolean testConnection() {
        Connection conn = getConnection();
        return conn != null;
    }
}