package dao;

import model.BiayaOperasional;
import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date; // ✅ Pastikan ini ada

public class BiayaOperasionalDAO {
    
    public boolean tambahBiaya(BiayaOperasional biaya) {
        String sql = "INSERT INTO tbl_biaya_operasional (id_booking, jenis_biaya, keterangan, " +
                    "jumlah, status_bayar, tanggal_bayar, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, biaya.getIdBooking());
            stmt.setString(2, biaya.getJenisBiaya());
            stmt.setString(3, biaya.getKeterangan());
            stmt.setDouble(4, biaya.getJumlah());
            stmt.setString(5, biaya.getStatusBayar());
            
            if (biaya.getTanggalBayar() != null) {
                stmt.setTimestamp(6, new Timestamp(biaya.getTanggalBayar().getTime()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            stmt.setInt(7, biaya.getCreatedBy());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBiaya(BiayaOperasional biaya) {
        String sql = "UPDATE tbl_biaya_operasional SET jenis_biaya = ?, keterangan = ?, " +
                    "jumlah = ?, status_bayar = ?, tanggal_bayar = ? " +
                    "WHERE id_biaya = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, biaya.getJenisBiaya());
            stmt.setString(2, biaya.getKeterangan());
            stmt.setDouble(3, biaya.getJumlah());
            stmt.setString(4, biaya.getStatusBayar());
            
            if (biaya.getTanggalBayar() != null) {
                stmt.setTimestamp(5, new Timestamp(biaya.getTanggalBayar().getTime()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setInt(6, biaya.getIdBiaya());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteBiaya(int idBiaya) {
        String sql = "DELETE FROM tbl_biaya_operasional WHERE id_biaya = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBiaya);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<BiayaOperasional> getAllBiaya() {
        List<BiayaOperasional> list = new ArrayList<>();
        String sql = "SELECT * FROM view_biaya_operasional_booking";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(extractBiayaFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    public List<BiayaOperasional> getBiayaByBooking(int idBooking) {
        List<BiayaOperasional> list = new ArrayList<>();
        String sql = "SELECT * FROM view_biaya_operasional_booking WHERE id_booking = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBooking);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractBiayaFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    public BiayaOperasional getBiayaById(int idBiaya) {
        String sql = "SELECT * FROM view_biaya_operasional_booking WHERE id_biaya = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBiaya);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBiayaFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Map<String, Double> getRingkasanBiayaPerBooking(int idBooking) {
        Map<String, Double> ringkasan = new HashMap<>();
        String sql = "SELECT * FROM view_ringkasan_biaya_per_booking WHERE id_booking = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBooking);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ringkasan.put("total_harga", rs.getDouble("total_harga"));
                ringkasan.put("total_gaji_sopir", rs.getDouble("total_gaji_sopir"));
                ringkasan.put("total_bbm", rs.getDouble("total_bbm"));
                ringkasan.put("total_tol", rs.getDouble("total_tol"));
                ringkasan.put("total_parkir", rs.getDouble("total_parkir"));
                ringkasan.put("total_makan_sopir", rs.getDouble("total_makan_sopir"));
                ringkasan.put("total_maintenance", rs.getDouble("total_maintenance"));
                ringkasan.put("total_lainnya", rs.getDouble("total_lainnya"));
                ringkasan.put("total_biaya_operasional", rs.getDouble("total_biaya_operasional"));
                ringkasan.put("sudah_bayar", rs.getDouble("sudah_bayar"));
                ringkasan.put("belum_bayar", rs.getDouble("belum_bayar"));
                ringkasan.put("keuntungan_bersih", rs.getDouble("keuntungan_bersih"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return ringkasan;
    }
    
    public double getTotalBiayaByBooking(int idBooking) {
        String sql = "SELECT COALESCE(SUM(jumlah), 0) as total FROM tbl_biaya_operasional WHERE id_booking = ?";
        
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
        
        return 0.0;
    }
    
    public boolean updateStatusBayar(int idBiaya, String status, Date tanggalBayar) {
        String sql = "UPDATE tbl_biaya_operasional SET status_bayar = ?, tanggal_bayar = ? WHERE id_biaya = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            if (tanggalBayar != null) {
                stmt.setTimestamp(2, new Timestamp(tanggalBayar.getTime()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setInt(3, idBiaya);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private BiayaOperasional extractBiayaFromResultSet(ResultSet rs) throws SQLException {
        BiayaOperasional biaya = new BiayaOperasional();
        biaya.setIdBiaya(rs.getInt("id_biaya"));
        biaya.setIdBooking(rs.getInt("id_booking"));
        biaya.setKodeBooking(rs.getString("kode_booking"));
        biaya.setTanggalBiaya(rs.getTimestamp("tanggal_biaya"));
        biaya.setJenisBiaya(rs.getString("jenis_biaya"));
        biaya.setKeterangan(rs.getString("keterangan"));
        biaya.setJumlah(rs.getDouble("jumlah"));
        // ✅ HAPUS: biaya.setBukti(rs.getString("bukti"));
        biaya.setStatusBayar(rs.getString("status_bayar"));
        biaya.setTanggalBayar(rs.getTimestamp("tanggal_bayar"));
        biaya.setCreatedByName(rs.getString("created_by_name"));
        
        // Additional fields
        biaya.setNamaPelanggan(rs.getString("nama_pelanggan"));
        biaya.setNoPolisi(rs.getString("no_polisi"));
        biaya.setTipeBus(rs.getString("tipe_bus"));
        biaya.setTanggalMulai(rs.getDate("tanggal_mulai"));
        biaya.setTanggalSelesai(rs.getDate("tanggal_selesai"));
        biaya.setTotalHarga(rs.getDouble("total_harga"));
        
        return biaya;
    }
}