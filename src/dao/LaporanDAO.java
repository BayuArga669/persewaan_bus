package dao;

import config.DatabaseConfig;
import model.LaporanKeuangan;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaporanDAO {
    
    /**
     * Generate laporan keuangan berdasarkan periode
     */
    public LaporanKeuangan getLaporanKeuangan(Date tanggalMulai, Date tanggalSelesai) {
        LaporanKeuangan laporan = new LaporanKeuangan();
        
        // 1. Hitung Total Pendapatan dari Pembayaran
        String sqlPendapatan = "SELECT " +
                "COALESCE(SUM(jumlah_bayar), 0) as total_pendapatan, " +
                "COALESCE(SUM(CASE WHEN metode_bayar = 'cash' THEN jumlah_bayar ELSE 0 END), 0) as pendapatan_cash, " +
                "COALESCE(SUM(CASE WHEN metode_bayar = 'transfer' THEN jumlah_bayar ELSE 0 END), 0) as pendapatan_transfer, " +
                "COALESCE(SUM(CASE WHEN metode_bayar = 'ewallet' THEN jumlah_bayar ELSE 0 END), 0) as pendapatan_ewallet " +
                "FROM tbl_pembayaran " +
                "WHERE DATE(tanggal_bayar) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPendapatan)) {
            
            stmt.setDate(1, tanggalMulai);
            stmt.setDate(2, tanggalSelesai);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                laporan.setTotalPendapatan(rs.getDouble("total_pendapatan"));
                laporan.setPendapatanCash(rs.getDouble("pendapatan_cash"));
                laporan.setPendapatanTransfer(rs.getDouble("pendapatan_transfer"));
                laporan.setPendapatanEwallet(rs.getDouble("pendapatan_ewallet"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 2. Hitung Biaya Sopir
        String sqlBiayaSopir = "SELECT " +
                "COALESCE(SUM(fee_sopir), 0) as total_biaya_sopir, " +
                "COALESCE(SUM(CASE WHEN status_bayar = 'belum_bayar' THEN fee_sopir ELSE 0 END), 0) as biaya_belum_bayar, " +
                "COALESCE(SUM(CASE WHEN status_bayar = 'dibayar' THEN fee_sopir ELSE 0 END), 0) as biaya_sudah_bayar " +
                "FROM tbl_assignment_sopir a " +
                "JOIN tbl_booking b ON a.id_booking = b.id_booking " +
                "WHERE DATE(b.tanggal_booking) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBiayaSopir)) {
            
            stmt.setDate(1, tanggalMulai);
            stmt.setDate(2, tanggalSelesai);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                laporan.setTotalBiayaSopir(rs.getDouble("total_biaya_sopir"));
                laporan.setBiayaSopirBelumBayar(rs.getDouble("biaya_belum_bayar"));
                laporan.setBiayaSopirSudahBayar(rs.getDouble("biaya_sudah_bayar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 3. Hitung Statistik Booking
        String sqlStats = "SELECT " +
                "COUNT(*) as total_booking, " +
                "SUM(CASE WHEN status_booking = 'selesai' THEN 1 ELSE 0 END) as booking_selesai, " +
                "SUM(CASE WHEN status_booking = 'dikonfirmasi' THEN 1 ELSE 0 END) as booking_dikonfirmasi, " +
                "SUM(CASE WHEN status_booking = 'dibatalkan' THEN 1 ELSE 0 END) as booking_dibatalkan " +
                "FROM tbl_booking " +
                "WHERE DATE(tanggal_booking) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlStats)) {
            
            stmt.setDate(1, tanggalMulai);
            stmt.setDate(2, tanggalSelesai);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                laporan.setTotalBooking(rs.getInt("total_booking"));
                laporan.setBookingSelesai(rs.getInt("booking_selesai"));
                laporan.setBookingDikonfirmasi(rs.getInt("booking_dikonfirmasi"));
                laporan.setBookingDibatalkan(rs.getInt("booking_dibatalkan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 4. Hitung Keuntungan
        double keuntunganKotor = laporan.getTotalPendapatan();
        double keuntunganBersih = keuntunganKotor - laporan.getTotalBiayaSopir();
        
        laporan.setKeuntunganKotor(keuntunganKotor);
        laporan.setKeuntunganBersih(keuntunganBersih);
        
        return laporan;
    }
    
    /**
     * Get detail transaksi untuk export
     */
    public List<Map<String, Object>> getDetailTransaksi(Date tanggalMulai, Date tanggalSelesai) {
        List<Map<String, Object>> transaksiList = new ArrayList<>();
        
        String sql = "SELECT " +
                "b.kode_booking, " +
                "b.tanggal_booking, " +
                "p.nama_pelanggan, " +
                "bus.no_polisi, " +
                "bus.tipe_bus, " +
                "b.tanggal_mulai, " +
                "b.tanggal_selesai, " +
                "b.lama_sewa, " +
                "b.total_harga, " +
                "b.status_booking, " +
                "COALESCE(pby.jumlah_bayar, 0) as jumlah_dibayar, " +
                "pby.metode_bayar, " +
                "pby.status_bayar, " +
                "COALESCE(a.fee_sopir, 0) as biaya_sopir, " +
                "u_sopir.nama_lengkap as nama_sopir " +
                "FROM tbl_booking b " +
                "JOIN tbl_pelanggan p ON b.id_pelanggan = p.id_pelanggan " +
                "JOIN tbl_bus bus ON b.id_bus = bus.id_bus " +
                "LEFT JOIN tbl_pembayaran pby ON b.id_booking = pby.id_booking " +
                "LEFT JOIN tbl_assignment_sopir a ON b.id_booking = a.id_booking " +
                "LEFT JOIN tbl_sopir s ON a.id_sopir = s.id_sopir " +
                "LEFT JOIN tbl_users u_sopir ON s.id_user = u_sopir.id_user " +
                "WHERE DATE(b.tanggal_booking) BETWEEN ? AND ? " +
                "ORDER BY b.tanggal_booking DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, tanggalMulai);
            stmt.setDate(2, tanggalSelesai);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("kode_booking", rs.getString("kode_booking"));
                transaksi.put("tanggal_booking", rs.getTimestamp("tanggal_booking"));
                transaksi.put("nama_pelanggan", rs.getString("nama_pelanggan"));
                transaksi.put("no_polisi", rs.getString("no_polisi"));
                transaksi.put("tipe_bus", rs.getString("tipe_bus"));
                transaksi.put("tanggal_mulai", rs.getDate("tanggal_mulai"));
                transaksi.put("tanggal_selesai", rs.getDate("tanggal_selesai"));
                transaksi.put("lama_sewa", rs.getInt("lama_sewa"));
                transaksi.put("total_harga", rs.getDouble("total_harga"));
                transaksi.put("status_booking", rs.getString("status_booking"));
                transaksi.put("jumlah_dibayar", rs.getDouble("jumlah_dibayar"));
                transaksi.put("metode_bayar", rs.getString("metode_bayar"));
                transaksi.put("status_bayar", rs.getString("status_bayar"));
                transaksi.put("biaya_sopir", rs.getDouble("biaya_sopir"));
                transaksi.put("nama_sopir", rs.getString("nama_sopir"));
                
                // Hitung keuntungan per transaksi
                double keuntungan = rs.getDouble("jumlah_dibayar") - rs.getDouble("biaya_sopir");
                transaksi.put("keuntungan", keuntungan);
                
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transaksiList;
    }
    
    /**
     * Get laporan per metode pembayaran
     */
    public Map<String, Double> getLaporanPerMetodeBayar(Date tanggalMulai, Date tanggalSelesai) {
        Map<String, Double> laporanMetode = new HashMap<>();
        
        String sql = "SELECT metode_bayar, SUM(jumlah_bayar) as total " +
                "FROM tbl_pembayaran " +
                "WHERE DATE(tanggal_bayar) BETWEEN ? AND ? " +
                "GROUP BY metode_bayar";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, tanggalMulai);
            stmt.setDate(2, tanggalSelesai);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                laporanMetode.put(rs.getString("metode_bayar"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return laporanMetode;
    }
}