package dao;

import config.DatabaseConfig;
import model.Pembayaran;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PembayaranDAO {

    public boolean tambahPembayaran(Pembayaran pembayaran) {
        String sql = "INSERT INTO tbl_pembayaran (id_booking, tanggal_bayar, jumlah_bayar, metode_bayar, bukti_transfer, status_bayar, keterangan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pembayaran.getIdBooking());
            stmt.setTimestamp(2, new java.sql.Timestamp(pembayaran.getTanggalBayar().getTime()));
            stmt.setDouble(3, pembayaran.getJumlahBayar());
            stmt.setString(4, pembayaran.getMetodeBayar());
            stmt.setString(5, pembayaran.getBuktiTransfer());
            stmt.setString(6, pembayaran.getStatusBayar());
            stmt.setString(7, pembayaran.getKeterangan());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePembayaran(Pembayaran pembayaran) {
        String sql = "UPDATE tbl_pembayaran SET jumlah_bayar = ?, metode_bayar = ?, bukti_transfer = ?, status_bayar = ?, keterangan = ? WHERE id_pembayaran = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, pembayaran.getJumlahBayar());
            stmt.setString(2, pembayaran.getMetodeBayar());
            stmt.setString(3, pembayaran.getBuktiTransfer());
            stmt.setString(4, pembayaran.getStatusBayar());
            stmt.setString(5, pembayaran.getKeterangan());
            stmt.setInt(6, pembayaran.getIdPembayaran());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ FIX UTAMA: Hitung total_dibayar & sisa langsung di SQL
    public List<Pembayaran> getAllPembayaran() {
        List<Pembayaran> pembayaranList = new ArrayList<>();
        String sql = """
            SELECT 
                p.id_pembayaran,
                p.id_booking,
                p.tanggal_bayar,
                p.jumlah_bayar,
                p.metode_bayar,
                p.bukti_transfer,
                p.status_bayar,
                p.keterangan,
                b.kode_booking,
                b.total_harga,
                pl.nama_pelanggan,
                COALESCE(SUM(p2.jumlah_bayar), 0) AS total_dibayar,
                (b.total_harga - COALESCE(SUM(p2.jumlah_bayar), 0)) AS sisa_pembayaran
            FROM tbl_pembayaran p
            INNER JOIN tbl_booking b ON p.id_booking = b.id_booking
            INNER JOIN tbl_pelanggan pl ON b.id_pelanggan = pl.id_pelanggan
            LEFT JOIN tbl_pembayaran p2 ON p.id_booking = p2.id_booking
            GROUP BY p.id_pembayaran, b.kode_booking, b.total_harga, pl.nama_pelanggan
            ORDER BY p.tanggal_bayar DESC
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pembayaranList.add(mapResultSetToPembayaran(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pembayaranList;
    }

    public Pembayaran getPembayaranById(int idPembayaran) {
        String sql = """
            SELECT 
                p.id_pembayaran,
                p.id_booking,
                p.tanggal_bayar,
                p.jumlah_bayar,
                p.metode_bayar,
                p.bukti_transfer,
                p.status_bayar,
                p.keterangan,
                b.kode_booking,
                b.total_harga,
                pl.nama_pelanggan,
                COALESCE(SUM(p2.jumlah_bayar), 0) AS total_dibayar,
                (b.total_harga - COALESCE(SUM(p2.jumlah_bayar), 0)) AS sisa_pembayaran
            FROM tbl_pembayaran p
            INNER JOIN tbl_booking b ON p.id_booking = b.id_booking
            INNER JOIN tbl_pelanggan pl ON b.id_pelanggan = pl.id_pelanggan
            LEFT JOIN tbl_pembayaran p2 ON p.id_booking = p2.id_booking
            WHERE p.id_pembayaran = ?
            GROUP BY p.id_pembayaran, b.kode_booking, b.total_harga, pl.nama_pelanggan
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPembayaran);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPembayaran(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getTotalPembayaranByBookingId(int idBooking) {
        String sql = "SELECT COALESCE(SUM(jumlah_bayar), 0) as total FROM tbl_pembayaran WHERE id_booking = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBooking);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hapusPembayaran(int idPembayaran) {
        String sql = "DELETE FROM tbl_pembayaran WHERE id_pembayaran = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPembayaran);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Versi fix: ambil total_dibayar & sisa_pembayaran dari kolom SQL
    private Pembayaran mapResultSetToPembayaran(ResultSet rs) throws SQLException {
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdPembayaran(rs.getInt("id_pembayaran"));
        pembayaran.setIdBooking(rs.getInt("id_booking"));
        pembayaran.setTanggalBayar(rs.getTimestamp("tanggal_bayar"));
        pembayaran.setJumlahBayar(rs.getDouble("jumlah_bayar"));
        pembayaran.setMetodeBayar(rs.getString("metode_bayar"));
        pembayaran.setBuktiTransfer(rs.getString("bukti_transfer"));
        pembayaran.setStatusBayar(rs.getString("status_bayar"));
        pembayaran.setKeterangan(rs.getString("keterangan"));
        pembayaran.setKodeBooking(rs.getString("kode_booking"));
        pembayaran.setNamaPelanggan(rs.getString("nama_pelanggan"));
        pembayaran.setTotalHarga(rs.getDouble("total_harga"));
        
        // ✅ Ambil dari hasil query (bukan panggil DAO lain!)
        double totalDibayar = rs.getDouble("total_dibayar");
        double sisa = rs.getDouble("sisa_pembayaran");
        
        pembayaran.setSisaPembayaran(sisa);
        // Optional: tambahkan setter totalDibayar jika perlu di model
        return pembayaran;
    }

    // ✅ Fix: tambahkan kolom total_dibayar & sisa_pembayaran di search juga
    public List<Pembayaran> searchPembayaran(String keyword, String statusFilter) {
        List<Pembayaran> pembayaranList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT 
                p.id_pembayaran,
                p.id_booking,
                p.tanggal_bayar,
                p.jumlah_bayar,
                p.metode_bayar,
                p.bukti_transfer,
                p.status_bayar,
                p.keterangan,
                b.kode_booking,
                b.total_harga,
                pl.nama_pelanggan,
                COALESCE(SUM(p2.jumlah_bayar), 0) AS total_dibayar,
                (b.total_harga - COALESCE(SUM(p2.jumlah_bayar), 0)) AS sisa_pembayaran
            FROM tbl_pembayaran p
            INNER JOIN tbl_booking b ON p.id_booking = b.id_booking
            INNER JOIN tbl_pelanggan pl ON b.id_pelanggan = pl.id_pelanggan
            LEFT JOIN tbl_pembayaran p2 ON p.id_booking = p2.id_booking
            WHERE 1=1
            """);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (b.kode_booking LIKE ? OR pl.nama_pelanggan LIKE ?)");
        }
        
        if (statusFilter != null && !statusFilter.equals("Semua")) {
            sql.append(" AND p.status_bayar = ?");
        }
        
        sql.append(" GROUP BY p.id_pembayaran, b.kode_booking, b.total_harga, pl.nama_pelanggan");
        sql.append(" ORDER BY p.tanggal_bayar DESC");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            if (statusFilter != null && !statusFilter.equals("Semua")) {
                stmt.setString(paramIndex, statusFilter.toLowerCase());
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pembayaranList.add(mapResultSetToPembayaran(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pembayaranList;
    }
}