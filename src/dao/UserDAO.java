package dao;

import config.DatabaseConfig;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User login(String username, String password) {
        String sql = "SELECT * FROM tbl_users WHERE username = ? AND password = ? AND status = 'aktif'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setUsername(rs.getString("username"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setEmail(rs.getString("email"));
                user.setNoTelp(rs.getString("no_telp"));
                user.setAlamat(rs.getString("alamat"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean tambahUser(User user) {
        String sql = "INSERT INTO tbl_users (username, password, nama_lengkap, email, no_telp, alamat, role, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getNamaLengkap());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getNoTelp());
            stmt.setString(6, user.getAlamat());
            stmt.setString(7, user.getRole());
            stmt.setString(8, user.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE tbl_users SET nama_lengkap = ?, email = ?, no_telp = ?, alamat = ?, status = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getNamaLengkap());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getNoTelp());
            stmt.setString(4, user.getAlamat());
            stmt.setString(5, user.getStatus());
            stmt.setInt(6, user.getIdUser());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hapusUser(int idUser) {
        String sql = "DELETE FROM tbl_users WHERE id_user = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM tbl_users ORDER BY id_user DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setUsername(rs.getString("username"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setEmail(rs.getString("email"));
                user.setNoTelp(rs.getString("no_telp"));
                user.setAlamat(rs.getString("alamat"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public List<User> getKasirOnly() {
        List<User> kasirs = new ArrayList<>();
        String sql = "SELECT * FROM tbl_users WHERE role = 'kasir' ORDER BY nama_lengkap";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setUsername(rs.getString("username"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                kasirs.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kasirs;
    }
    
    public boolean changePassword(int idUser, String newPassword) {
        String sql = "UPDATE tbl_users SET password = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setInt(2, idUser);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔹 METODE BARU: Ambil ID User berdasarkan Username
    public int getUserIdByUsername(String username) {
        String sql = "SELECT id_user FROM tbl_users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}