package dao;

import config.DatabaseConfig;
import model.Sopir;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SopirDAO {
    
    public boolean tambahSopir(Sopir sopir) {
        String sql = "INSERT INTO tbl_sopir (id_user, no_sim, jenis_sim, masa_berlaku_sim, status_sopir) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sopir.getIdUser());
            stmt.setString(2, sopir.getNoSim());
            stmt.setString(3, sopir.getJenisSim());
            stmt.setDate(4, new java.sql.Date(sopir.getMasaBerlakuSim().getTime()));
            stmt.setString(5, sopir.getStatusSopir());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSopir(Sopir sopir) {
        String sql = "UPDATE tbl_sopir SET no_sim = ?, jenis_sim = ?, masa_berlaku_sim = ?, status_sopir = ? WHERE id_sopir = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sopir.getNoSim());
            stmt.setString(2, sopir.getJenisSim());
            stmt.setDate(3, new java.sql.Date(sopir.getMasaBerlakuSim().getTime()));
            stmt.setString(4, sopir.getStatusSopir());
            stmt.setInt(5, sopir.getIdSopir());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hapusSopir(int idSopir) {
        String sql = "DELETE FROM tbl_sopir WHERE id_sopir = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSopir);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Sopir> getAllSopir() {
        List<Sopir> sopirList = new ArrayList<>();
        String sql = "SELECT s.*, u.username, u.nama_lengkap, u.no_telp " +
                     "FROM tbl_sopir s " +
                     "JOIN tbl_users u ON s.id_user = u.id_user " +
                     "ORDER BY u.nama_lengkap";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Sopir sopir = new Sopir();
                sopir.setIdSopir(rs.getInt("id_sopir"));
                sopir.setIdUser(rs.getInt("id_user"));
                sopir.setNoSim(rs.getString("no_sim"));
                sopir.setJenisSim(rs.getString("jenis_sim"));
                sopir.setMasaBerlakuSim(rs.getDate("masa_berlaku_sim"));
                sopir.setStatusSopir(rs.getString("status_sopir"));
                sopir.setNamaSopir(rs.getString("nama_lengkap"));
                sopir.setUsername(rs.getString("username"));
                sopir.setNoTelp(rs.getString("no_telp"));
                sopirList.add(sopir);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sopirList;
    }
    
    public Sopir getSopirById(int idSopir) {
        String sql = "SELECT s.*, u.username, u.nama_lengkap, u.no_telp " +
                     "FROM tbl_sopir s " +
                     "JOIN tbl_users u ON s.id_user = u.id_user " +
                     "WHERE s.id_sopir = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSopir);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Sopir sopir = new Sopir();
                sopir.setIdSopir(rs.getInt("id_sopir"));
                sopir.setIdUser(rs.getInt("id_user"));
                sopir.setNoSim(rs.getString("no_sim"));
                sopir.setJenisSim(rs.getString("jenis_sim"));
                sopir.setMasaBerlakuSim(rs.getDate("masa_berlaku_sim"));
                sopir.setStatusSopir(rs.getString("status_sopir"));
                sopir.setNamaSopir(rs.getString("nama_lengkap"));
                sopir.setUsername(rs.getString("username"));
                sopir.setNoTelp(rs.getString("no_telp"));
                return sopir;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Sopir getSopirByUserId(int idUser) {
        String sql = "SELECT s.*, u.username, u.nama_lengkap, u.no_telp " +
                     "FROM tbl_sopir s " +
                     "JOIN tbl_users u ON s.id_user = u.id_user " +
                     "WHERE s.id_user = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Sopir sopir = new Sopir();
                sopir.setIdSopir(rs.getInt("id_sopir"));
                sopir.setIdUser(rs.getInt("id_user"));
                sopir.setNoSim(rs.getString("no_sim"));
                sopir.setJenisSim(rs.getString("jenis_sim"));
                sopir.setMasaBerlakuSim(rs.getDate("masa_berlaku_sim"));
                sopir.setStatusSopir(rs.getString("status_sopir"));
                sopir.setNamaSopir(rs.getString("nama_lengkap"));
                sopir.setUsername(rs.getString("username"));
                sopir.setNoTelp(rs.getString("no_telp"));
                return sopir;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Sopir> getSopirAktif() {
        List<Sopir> sopirList = new ArrayList<>();
        String sql = "SELECT s.*, u.username, u.nama_lengkap, u.no_telp " +
                     "FROM tbl_sopir s " +
                     "JOIN tbl_users u ON s.id_user = u.id_user " +
                     "WHERE s.status_sopir = 'aktif' " +
                     "ORDER BY u.nama_lengkap";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Sopir sopir = new Sopir();
                sopir.setIdSopir(rs.getInt("id_sopir"));
                sopir.setIdUser(rs.getInt("id_user"));
                sopir.setNoSim(rs.getString("no_sim"));
                sopir.setJenisSim(rs.getString("jenis_sim"));
                sopir.setMasaBerlakuSim(rs.getDate("masa_berlaku_sim"));
                sopir.setStatusSopir(rs.getString("status_sopir"));
                sopir.setNamaSopir(rs.getString("nama_lengkap"));
                sopir.setUsername(rs.getString("username"));
                sopir.setNoTelp(rs.getString("no_telp"));
                sopirList.add(sopir);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sopirList;
    }
}