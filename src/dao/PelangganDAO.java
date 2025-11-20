package dao;

import config.DatabaseConfig;
import model.Pelanggan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganDAO {
    
    public boolean tambahPelanggan(Pelanggan pelanggan) {
        String sql = "INSERT INTO tbl_pelanggan (nama_pelanggan, no_telp, email, alamat, no_ktp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pelanggan.getNamaPelanggan());
            stmt.setString(2, pelanggan.getNoTelp());
            stmt.setString(3, pelanggan.getEmail());
            stmt.setString(4, pelanggan.getAlamat());
            stmt.setString(5, pelanggan.getNoKtp());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePelanggan(Pelanggan pelanggan) {
        String sql = "UPDATE tbl_pelanggan SET nama_pelanggan = ?, no_telp = ?, email = ?, alamat = ?, no_ktp = ? WHERE id_pelanggan = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pelanggan.getNamaPelanggan());
            stmt.setString(2, pelanggan.getNoTelp());
            stmt.setString(3, pelanggan.getEmail());
            stmt.setString(4, pelanggan.getAlamat());
            stmt.setString(5, pelanggan.getNoKtp());
            stmt.setInt(6, pelanggan.getIdPelanggan());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hapusPelanggan(int idPelanggan) {
        String sql = "DELETE FROM tbl_pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPelanggan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Pelanggan> getAllPelanggan() {
        List<Pelanggan> pelangganList = new ArrayList<>();
        String sql = "SELECT * FROM tbl_pelanggan ORDER BY nama_pelanggan";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pelanggan pelanggan = new Pelanggan();
                pelanggan.setIdPelanggan(rs.getInt("id_pelanggan"));
                pelanggan.setNamaPelanggan(rs.getString("nama_pelanggan"));
                pelanggan.setNoTelp(rs.getString("no_telp"));
                pelanggan.setEmail(rs.getString("email"));
                pelanggan.setAlamat(rs.getString("alamat"));
                pelanggan.setNoKtp(rs.getString("no_ktp"));
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pelangganList;
    }
    
    public Pelanggan getPelangganById(int idPelanggan) {
        String sql = "SELECT * FROM tbl_pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPelanggan);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Pelanggan pelanggan = new Pelanggan();
                pelanggan.setIdPelanggan(rs.getInt("id_pelanggan"));
                pelanggan.setNamaPelanggan(rs.getString("nama_pelanggan"));
                pelanggan.setNoTelp(rs.getString("no_telp"));
                pelanggan.setEmail(rs.getString("email"));
                pelanggan.setAlamat(rs.getString("alamat"));
                pelanggan.setNoKtp(rs.getString("no_ktp"));
                return pelanggan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Pelanggan> searchPelanggan(String keyword) {
        List<Pelanggan> pelangganList = new ArrayList<>();
        String sql = "SELECT * FROM tbl_pelanggan WHERE nama_pelanggan LIKE ? OR no_telp LIKE ? OR email LIKE ? ORDER BY nama_pelanggan";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pelanggan pelanggan = new Pelanggan();
                pelanggan.setIdPelanggan(rs.getInt("id_pelanggan"));
                pelanggan.setNamaPelanggan(rs.getString("nama_pelanggan"));
                pelanggan.setNoTelp(rs.getString("no_telp"));
                pelanggan.setEmail(rs.getString("email"));
                pelanggan.setAlamat(rs.getString("alamat"));
                pelanggan.setNoKtp(rs.getString("no_ktp"));
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pelangganList;
    }
    
    public int getLastInsertedId() {
        String sql = "SELECT LAST_INSERT_ID() as id";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}