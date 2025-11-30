package dao;

import model.LaporanKeuangan;
import config.DatabaseConfig;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class LaporanDAO {
    
    public LaporanKeuangan getLaporanKeuangan(Date tanggalMulai, Date tanggalSelesai) {
        LaporanKeuangan laporan = new LaporanKeuangan();
        
        String sql = "SELECT " +
                    // Pendapatan
                    "COALESCE(SUM(CASE WHEN pby.status_bayar = 'lunas' THEN pby.jumlah_bayar ELSE 0 END), 0) as total_pendapatan, " +
                    "COALESCE(SUM(CASE WHEN pby.status_bayar = 'lunas' AND pby.metode_bayar = 'cash' THEN pby.jumlah_bayar ELSE 0 END), 0) as pendapatan_cash, " +
                    "COALESCE(SUM(CASE WHEN pby.status_bayar = 'lunas' AND pby.metode_bayar = 'transfer' THEN pby.jumlah_bayar ELSE 0 END), 0) as pendapatan_transfer, " +
                    "COALESCE(SUM(CASE WHEN pby.status_bayar = 'lunas' AND pby.metode_bayar = 'ewallet' THEN pby.jumlah_bayar ELSE 0 END), 0) as pendapatan_ewallet, " +
                    // Biaya Operasional
                    "COALESCE((SELECT SUM(jumlah) FROM tbl_biaya_operasional bo2 " +
                    "WHERE DATE(bo2.tanggal_biaya) BETWEEN ? AND ?), 0) as total_biaya_operasional, " +
                    "COALESCE((SELECT SUM(jumlah) FROM tbl_biaya_operasional bo2 " +
                    "WHERE DATE(bo2.tanggal_biaya) BETWEEN ? AND ? AND bo2.status_bayar = 'sudah_bayar'), 0) as biaya_operasional_sudah_bayar, " +
                    "COALESCE((SELECT SUM(jumlah) FROM tbl_biaya_operasional bo2 " +
                    "WHERE DATE(bo2.tanggal_biaya) BETWEEN ? AND ? AND bo2.status_bayar = 'belum_bayar'), 0) as biaya_operasional_belum_bayar, " +
                    // Statistik Booking
                    "COUNT(DISTINCT b.id_booking) as total_booking, " +
                    "SUM(CASE WHEN b.status_booking = 'selesai' THEN 1 ELSE 0 END) as booking_selesai, " +
                    "SUM(CASE WHEN b.status_booking = 'dikonfirmasi' THEN 1 ELSE 0 END) as booking_dikonfirmasi, " +
                    "SUM(CASE WHEN b.status_booking = 'dibatalkan' THEN 1 ELSE 0 END) as booking_dibatalkan " +
                    "FROM tbl_booking b " +
                    "LEFT JOIN tbl_pembayaran pby ON b.id_booking = pby.id_booking " +
                    "WHERE DATE(b.tanggal_booking) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            java.sql.Date sqlDateMulai = new java.sql.Date(tanggalMulai.getTime());
            java.sql.Date sqlDateSelesai = new java.sql.Date(tanggalSelesai.getTime());
            
            stmt.setDate(1, sqlDateMulai);
            stmt.setDate(2, sqlDateSelesai);
            stmt.setDate(3, sqlDateMulai);
            stmt.setDate(4, sqlDateSelesai);
            stmt.setDate(5, sqlDateMulai);
            stmt.setDate(6, sqlDateSelesai);
            stmt.setDate(7, sqlDateMulai);
            stmt.setDate(8, sqlDateSelesai);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Pendapatan
                laporan.setTotalPendapatan(rs.getDouble("total_pendapatan"));
                laporan.setPendapatanCash(rs.getDouble("pendapatan_cash"));
                laporan.setPendapatanTransfer(rs.getDouble("pendapatan_transfer"));
                laporan.setPendapatanEwallet(rs.getDouble("pendapatan_ewallet"));
                
                // Biaya Operasional (menggantikan biaya sopir)
                laporan.setTotalBiayaSopir(rs.getDouble("total_biaya_operasional"));
                laporan.setBiayaSopirSudahBayar(rs.getDouble("biaya_operasional_sudah_bayar"));
                laporan.setBiayaSopirBelumBayar(rs.getDouble("biaya_operasional_belum_bayar"));
                
                // Keuntungan
                double keuntunganKotor = laporan.getTotalPendapatan();
                double keuntunganBersih = keuntunganKotor - rs.getDouble("total_biaya_operasional");
                laporan.setKeuntunganKotor(keuntunganKotor);
                laporan.setKeuntunganBersih(keuntunganBersih);
                
                // Statistik
                laporan.setTotalBooking(rs.getInt("total_booking"));
                laporan.setBookingSelesai(rs.getInt("booking_selesai"));
                laporan.setBookingDikonfirmasi(rs.getInt("booking_dikonfirmasi"));
                laporan.setBookingDibatalkan(rs.getInt("booking_dibatalkan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return laporan;
    }
    
    public List<Map<String, Object>> getDetailTransaksi(Date tanggalMulai, Date tanggalSelesai) {
        List<Map<String, Object>> list = new ArrayList<>();
        
        String sql = "SELECT b.kode_booking, b.tanggal_booking, p.nama_pelanggan, " +
                    "bus.no_polisi, bus.tipe_bus, b.tanggal_mulai, b.tanggal_selesai, " +
                    "b.total_harga, b.status_booking, " +
                    "pby.jumlah_bayar as jumlah_dibayar, pby.metode_bayar, pby.status_bayar, " +
                    "u.nama_lengkap as nama_kasir " +
                    "FROM tbl_booking b " +
                    "JOIN tbl_pelanggan p ON b.id_pelanggan = p.id_pelanggan " +
                    "JOIN tbl_bus bus ON b.id_bus = bus.id_bus " +
                    "JOIN tbl_users u ON b.id_kasir = u.id_user " +
                    "LEFT JOIN tbl_pembayaran pby ON b.id_booking = pby.id_booking " +
                    "WHERE DATE(b.tanggal_booking) BETWEEN ? AND ? " +
                    "ORDER BY b.tanggal_booking DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            java.sql.Date sqlDateMulai = new java.sql.Date(tanggalMulai.getTime());
            java.sql.Date sqlDateSelesai = new java.sql.Date(tanggalSelesai.getTime());
            
            stmt.setDate(1, sqlDateMulai);
            stmt.setDate(2, sqlDateSelesai);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("kode_booking", rs.getString("kode_booking"));
                row.put("tanggal_booking", rs.getTimestamp("tanggal_booking"));
                row.put("nama_pelanggan", rs.getString("nama_pelanggan"));
                row.put("no_polisi", rs.getString("no_polisi"));
                row.put("tipe_bus", rs.getString("tipe_bus"));
                row.put("tanggal_mulai", rs.getDate("tanggal_mulai"));
                row.put("tanggal_selesai", rs.getDate("tanggal_selesai"));
                row.put("total_harga", rs.getDouble("total_harga"));
                row.put("status_booking", rs.getString("status_booking"));
                row.put("jumlah_dibayar", rs.getDouble("jumlah_dibayar"));
                row.put("metode_bayar", rs.getString("metode_bayar"));
                row.put("status_bayar", rs.getString("status_bayar"));
                row.put("nama_kasir", rs.getString("nama_kasir"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    public Map<String, Double> getDetailBiayaOperasional(Date tanggalMulai, Date tanggalSelesai) {
        Map<String, Double> detail = new HashMap<>();
        
        String sql = "SELECT " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'gaji_sopir' THEN jumlah ELSE 0 END), 0) as gaji_sopir, " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'bbm' THEN jumlah ELSE 0 END), 0) as bbm, " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'tol' THEN jumlah ELSE 0 END), 0) as tol, " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'parkir' THEN jumlah ELSE 0 END), 0) as parkir, " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'makan_sopir' THEN jumlah ELSE 0 END), 0) as makan_sopir, " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'maintenance' THEN jumlah ELSE 0 END), 0) as maintenance, " +
                    "COALESCE(SUM(CASE WHEN jenis_biaya = 'lainnya' THEN jumlah ELSE 0 END), 0) as lainnya " +
                    "FROM tbl_biaya_operasional " +
                    "WHERE DATE(tanggal_biaya) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            java.sql.Date sqlDateMulai = new java.sql.Date(tanggalMulai.getTime());
            java.sql.Date sqlDateSelesai = new java.sql.Date(tanggalSelesai.getTime());
            
            stmt.setDate(1, sqlDateMulai);
            stmt.setDate(2, sqlDateSelesai);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                detail.put("gaji_sopir", rs.getDouble("gaji_sopir"));
                detail.put("bbm", rs.getDouble("bbm"));
                detail.put("tol", rs.getDouble("tol"));
                detail.put("parkir", rs.getDouble("parkir"));
                detail.put("makan_sopir", rs.getDouble("makan_sopir"));
                detail.put("maintenance", rs.getDouble("maintenance"));
                detail.put("lainnya", rs.getDouble("lainnya"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return detail;
    }
}