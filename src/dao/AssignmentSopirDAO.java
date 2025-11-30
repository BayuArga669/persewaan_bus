package dao;

import config.DatabaseConfig;
import model.AssignmentSopir;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSopirDAO {
    
    public boolean tambahAssignment(AssignmentSopir assignment) {
        String sql = "INSERT INTO tbl_assignment_sopir (id_booking, id_sopir, keterangan) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, assignment.getIdBooking());
            stmt.setInt(2, assignment.getIdSopir());
            stmt.setString(3, assignment.getKeterangan());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAssignment(AssignmentSopir assignment) {
        String sql = "UPDATE tbl_assignment_sopir SET id_sopir = ?, keterangan = ? WHERE id_assignment = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, assignment.getIdSopir());
            stmt.setString(2, assignment.getKeterangan());
            stmt.setInt(3, assignment.getIdAssignment());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hapusAssignment(int idAssignment) {
        String sql = "DELETE FROM tbl_assignment_sopir WHERE id_assignment = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAssignment);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<AssignmentSopir> getAllAssignments() {
        List<AssignmentSopir> assignments = new ArrayList<>();
        String sql = "SELECT * FROM view_sopir_order_pendapatan";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                AssignmentSopir assignment = mapResultSetToAssignment(rs);
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }
    
    public AssignmentSopir getAssignmentById(int idAssignment) {
        String sql = "SELECT * FROM view_sopir_order_pendapatan WHERE id_assignment = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAssignment);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAssignment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AssignmentSopir getAssignmentByBooking(int idBooking) {
        String sql = "SELECT * FROM view_sopir_order_pendapatan WHERE id_booking = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBooking);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAssignment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 🔹 METODE BARU: Cek apakah sopir sedang bertugas (belum selesai/dibatalkan)
    public boolean isSopirSedangBertugas(int idSopir) {
        String sql = "SELECT COUNT(*) AS total " +
                     "FROM view_sopir_order_pendapatan " +
                     "WHERE id_sopir = ? " +
                     "  AND status_booking NOT IN ('selesai', 'dibatalkan')";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSopir);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cek ketersediaan sopir berdasarkan bentrok tanggal
    public boolean isSopirAvailable(int idSopir, Date tanggalMulai, Date tanggalSelesai) {
        String sql = "SELECT COUNT(*) as total FROM tbl_assignment_sopir a " +
                     "JOIN tbl_booking b ON a.id_booking = b.id_booking " +
                     "WHERE a.id_sopir = ? " +
                     "AND b.status_booking IN ('dikonfirmasi', 'pending') " +
                     "AND ((b.tanggal_mulai BETWEEN ? AND ?) " +
                     "OR (b.tanggal_selesai BETWEEN ? AND ?) " +
                     "OR (b.tanggal_mulai <= ? AND b.tanggal_selesai >= ?))";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            java.sql.Date sqlTglMulai = new java.sql.Date(tanggalMulai.getTime());
            java.sql.Date sqlTglSelesai = new java.sql.Date(tanggalSelesai.getTime());
            
            stmt.setInt(1, idSopir);
            stmt.setDate(2, sqlTglMulai);
            stmt.setDate(3, sqlTglSelesai);
            stmt.setDate(4, sqlTglMulai);
            stmt.setDate(5, sqlTglSelesai);
            stmt.setDate(6, sqlTglMulai);
            stmt.setDate(7, sqlTglSelesai);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public java.sql.Date[] getBookingDates(int idBooking) {
        String sql = "SELECT tanggal_mulai, tanggal_selesai FROM tbl_booking WHERE id_booking = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBooking);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                java.sql.Date[] dates = new java.sql.Date[2];
                dates[0] = rs.getDate("tanggal_mulai");
                dates[1] = rs.getDate("tanggal_selesai");
                return dates;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private AssignmentSopir mapResultSetToAssignment(ResultSet rs) throws SQLException {
        AssignmentSopir assignment = new AssignmentSopir();
        assignment.setIdAssignment(rs.getInt("id_assignment"));
        assignment.setIdBooking(rs.getInt("id_booking"));
        assignment.setIdSopir(rs.getInt("id_sopir"));
        assignment.setKodeBooking(rs.getString("kode_booking"));
        assignment.setTanggalMulai(rs.getDate("tanggal_mulai"));
        assignment.setTanggalSelesai(rs.getDate("tanggal_selesai"));
        assignment.setTujuan(rs.getString("tujuan"));
        assignment.setStatusBooking(rs.getString("status_booking"));
        assignment.setNamaPelanggan(rs.getString("nama_pelanggan"));
        assignment.setNoPolisi(rs.getString("no_polisi"));
        assignment.setNamaSopir(rs.getString("nama_sopir"));
        assignment.setKeterangan(rs.getString("keterangan"));
        return assignment;
    }
}