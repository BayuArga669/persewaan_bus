package dao;

import config.DatabaseConfig;
import model.AssignmentSopir;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSopirDAO {

    // Query dasar dengan JOIN (digunakan di semua method)
    private static final String BASE_QUERY = """
        SELECT 
            a.id_assignment,
            a.id_booking,
            a.id_sopir,
            b.kode_booking,
            b.tanggal_mulai,
            b.tanggal_selesai,
            b.tujuan,
            b.status_booking,
            p.nama_pelanggan,
            bus.no_polisi,
            u_sopir.nama_lengkap AS nama_sopir,
            a.fee_sopir,
            a.status_bayar,
            a.tanggal_bayar,
            a.keterangan
        FROM tbl_assignment_sopir a
        JOIN tbl_booking b ON a.id_booking = b.id_booking
        JOIN tbl_pelanggan p ON b.id_pelanggan = p.id_pelanggan
        JOIN tbl_bus bus ON b.id_bus = bus.id_bus
        JOIN tbl_sopir s ON a.id_sopir = s.id_sopir
        JOIN tbl_users u_sopir ON s.id_user = u_sopir.id_user
        """;

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
        String sql = BASE_QUERY + " ORDER BY a.created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public List<AssignmentSopir> getAssignmentsBySopir(int idSopir) {
        List<AssignmentSopir> assignments = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE a.id_sopir = ? ORDER BY a.created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSopir);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public AssignmentSopir getAssignmentById(int idAssignment) {
        String sql = BASE_QUERY + " WHERE a.id_assignment = ?";
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
        String sql = BASE_QUERY + " WHERE a.id_booking = ?";
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

    // ✅ Cek ketersediaan sopir berdasarkan overlap tanggal
    public boolean isSopirAvailable(int idSopir, java.util.Date tanggalMulai, java.util.Date tanggalSelesai) {
    String sql = """
        SELECT COUNT(*)
        FROM tbl_assignment_sopir a
        INNER JOIN tbl_booking b ON a.id_booking = b.id_booking
        WHERE a.id_sopir = ?
          AND b.status_booking IN ('dikonfirmasi', 'selesai')
          AND ? <= b.tanggal_selesai
          AND ? >= b.tanggal_mulai
        """;

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idSopir);
        
        // Konversi aman ke java.sql.Date
        java.sql.Date sqlMulai = (tanggalMulai != null) ? 
                new java.sql.Date(tanggalMulai.getTime()) : null;
        java.sql.Date sqlSelesai = (tanggalSelesai != null) ? 
                new java.sql.Date(tanggalSelesai.getTime()) : null;

        stmt.setDate(2, sqlMulai);
        stmt.setDate(3, sqlSelesai);

        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) == 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
        }
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