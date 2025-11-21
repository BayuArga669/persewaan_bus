package dao;

import config.DatabaseConfig;
import model.AssignmentSopir;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSopirDAO {
    
    public boolean tambahAssignment(AssignmentSopir assignment) {
        String sql = "INSERT INTO tbl_assignment_sopir (id_booking, id_sopir, fee_sopir, status_bayar, keterangan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, assignment.getIdBooking());
            stmt.setInt(2, assignment.getIdSopir());
            stmt.setDouble(3, assignment.getFeeSopir());
            stmt.setString(4, assignment.getStatusBayar());
            stmt.setString(5, assignment.getKeterangan());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAssignment(AssignmentSopir assignment) {
        String sql = "UPDATE tbl_assignment_sopir SET id_sopir = ?, fee_sopir = ?, status_bayar = ?, tanggal_bayar = ?, keterangan = ? WHERE id_assignment = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, assignment.getIdSopir());
            stmt.setDouble(2, assignment.getFeeSopir());
            stmt.setString(3, assignment.getStatusBayar());
            if (assignment.getTanggalBayar() != null) {
                stmt.setDate(4, new java.sql.Date(assignment.getTanggalBayar().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, assignment.getKeterangan());
            stmt.setInt(6, assignment.getIdAssignment());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStatusBayar(int idAssignment, String statusBayar, Date tanggalBayar) {
        String sql = "UPDATE tbl_assignment_sopir SET status_bayar = ?, tanggal_bayar = ? WHERE id_assignment = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, statusBayar);
            stmt.setDate(2, new java.sql.Date(tanggalBayar.getTime()));
            stmt.setInt(3, idAssignment);
            
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
    
    public List<AssignmentSopir> getAssignmentsBySopir(int idSopir) {
        List<AssignmentSopir> assignments = new ArrayList<>();
        String sql = "SELECT * FROM view_sopir_order_pendapatan WHERE id_sopir = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSopir);
            ResultSet rs = stmt.executeQuery();
            
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
    
    public double getTotalPendapatanSopir(int idSopir, String statusBayar) {
        String sql = "SELECT COALESCE(SUM(fee_sopir), 0) as total FROM tbl_assignment_sopir WHERE id_sopir = ?";
        if (statusBayar != null && !statusBayar.isEmpty()) {
            sql += " AND status_bayar = ?";
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSopir);
            if (statusBayar != null && !statusBayar.isEmpty()) {
                stmt.setString(2, statusBayar);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
        assignment.setFeeSopir(rs.getDouble("fee_sopir"));
        assignment.setStatusBayar(rs.getString("status_bayar"));
        assignment.setTanggalBayar(rs.getDate("tanggal_bayar"));
        assignment.setKeterangan(rs.getString("keterangan"));
        return assignment;
    }
}