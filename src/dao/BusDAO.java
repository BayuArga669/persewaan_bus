package dao;

import config.DatabaseConfig;
import model.Bus;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date; // ✅ java.util.Date

public class BusDAO {
    
    public boolean tambahBus(Bus bus) {
        String sql = "INSERT INTO tbl_bus (no_polisi, tipe_bus, merk, kapasitas, fasilitas, harga_per_hari, foto, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bus.getNoPolisi());
            stmt.setString(2, bus.getTipeBus());
            stmt.setString(3, bus.getMerk());
            stmt.setInt(4, bus.getKapasitas());
            stmt.setString(5, bus.getFasilitas());
            stmt.setDouble(6, bus.getHargaPerHari());
            stmt.setString(7, bus.getFoto());
            stmt.setString(8, bus.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBus(Bus bus) {
        String sql = "UPDATE tbl_bus SET no_polisi = ?, tipe_bus = ?, merk = ?, kapasitas = ?, fasilitas = ?, harga_per_hari = ?, foto = ?, status = ? WHERE id_bus = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bus.getNoPolisi());
            stmt.setString(2, bus.getTipeBus());
            stmt.setString(3, bus.getMerk());
            stmt.setInt(4, bus.getKapasitas());
            stmt.setString(5, bus.getFasilitas());
            stmt.setDouble(6, bus.getHargaPerHari());
            stmt.setString(7, bus.getFoto());
            stmt.setString(8, bus.getStatus());
            stmt.setInt(9, bus.getIdBus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hapusBus(int idBus) {
        String sql = "DELETE FROM tbl_bus WHERE id_bus = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBus);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Bus> getAllBus() {
        List<Bus> busList = new ArrayList<>();
        String sql = "SELECT * FROM tbl_bus ORDER BY id_bus DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("id_bus"));
                bus.setNoPolisi(rs.getString("no_polisi"));
                bus.setTipeBus(rs.getString("tipe_bus"));
                bus.setMerk(rs.getString("merk"));
                bus.setKapasitas(rs.getInt("kapasitas"));
                bus.setFasilitas(rs.getString("fasilitas"));
                bus.setHargaPerHari(rs.getDouble("harga_per_hari"));
                bus.setFoto(rs.getString("foto"));
                bus.setStatus(rs.getString("status"));
                busList.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busList;
    }
    
    public List<Bus> getBusTersedia() {
        List<Bus> busList = new ArrayList<>();
        String sql = "SELECT * FROM tbl_bus WHERE status != 'maintenance' ORDER BY tipe_bus, kapasitas";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("id_bus"));
                bus.setNoPolisi(rs.getString("no_polisi"));
                bus.setTipeBus(rs.getString("tipe_bus"));
                bus.setMerk(rs.getString("merk"));
                bus.setKapasitas(rs.getInt("kapasitas"));
                bus.setFasilitas(rs.getString("fasilitas"));
                bus.setHargaPerHari(rs.getDouble("harga_per_hari"));
                bus.setFoto(rs.getString("foto"));
                bus.setStatus(rs.getString("status"));
                busList.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busList;
    }
    
    public Bus getBusById(int idBus) {
        String sql = "SELECT * FROM tbl_bus WHERE id_bus = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBus);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("id_bus"));
                bus.setNoPolisi(rs.getString("no_polisi"));
                bus.setTipeBus(rs.getString("tipe_bus"));
                bus.setMerk(rs.getString("merk"));
                bus.setKapasitas(rs.getInt("kapasitas"));
                bus.setFasilitas(rs.getString("fasilitas"));
                bus.setHargaPerHari(rs.getDouble("harga_per_hari"));
                bus.setFoto(rs.getString("foto"));
                bus.setStatus(rs.getString("status"));
                return bus;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateStatusBus(int idBus, String status) {
        String sql = "UPDATE tbl_bus SET status = ? WHERE id_bus = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, idBus);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Bus> searchBus(String keyword, String statusFilter) {
        List<Bus> busList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tbl_bus WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (no_polisi LIKE ? OR tipe_bus LIKE ? OR merk LIKE ?)");
        }
        
        if (statusFilter != null && !statusFilter.equals("Semua")) {
            sql.append(" AND status = ?");
        }
        
        sql.append(" ORDER BY id_bus DESC");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            if (statusFilter != null && !statusFilter.equals("Semua")) {
                stmt.setString(paramIndex, statusFilter.toLowerCase());
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("id_bus"));
                bus.setNoPolisi(rs.getString("no_polisi"));
                bus.setTipeBus(rs.getString("tipe_bus"));
                bus.setMerk(rs.getString("merk"));
                bus.setKapasitas(rs.getInt("kapasitas"));
                bus.setFasilitas(rs.getString("fasilitas"));
                bus.setHargaPerHari(rs.getDouble("harga_per_hari"));
                bus.setFoto(rs.getString("foto"));
                bus.setStatus(rs.getString("status"));
                busList.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busList;
    }
    
    public Bus getBusByNoPolisi(String noPolisi) {
        String sql = "SELECT * FROM tbl_bus WHERE no_polisi = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, noPolisi);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bus bus = new Bus();
                    bus.setIdBus(rs.getInt("id_bus"));
                    bus.setNoPolisi(rs.getString("no_polisi"));
                    bus.setTipeBus(rs.getString("tipe_bus"));
                    bus.setMerk(rs.getString("merk"));
                    bus.setKapasitas(rs.getInt("kapasitas"));
                    bus.setFasilitas(rs.getString("fasilitas"));
                    bus.setHargaPerHari(rs.getDouble("harga_per_hari"));
                    bus.setFoto(rs.getString("foto"));
                    bus.setStatus(rs.getString("status"));
                    return bus;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Date> getBookedDates(int idBus) {
        List<Date> bookedDates = new ArrayList<>();
        String sql = "SELECT tanggal_mulai, tanggal_selesai FROM tbl_booking " +
                     "WHERE id_bus = ? " +
                     "AND status_booking IN ('pending', 'dikonfirmasi') " +
                     "ORDER BY tanggal_mulai";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBus);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                java.sql.Date sqlMulai = rs.getDate("tanggal_mulai");
                java.sql.Date sqlSelesai = rs.getDate("tanggal_selesai");
                
                if (sqlMulai == null || sqlSelesai == null) {
                    continue;
                }
                
                Date mulai = new Date(sqlMulai.getTime());
                Date selesai = new Date(sqlSelesai.getTime());
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(mulai);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(selesai);
                
                while (!cal.after(endCal)) {
                    bookedDates.add(new Date(cal.getTimeInMillis()));
                    cal.add(Calendar.DATE, 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedDates;
    }

    /**
     * Mengecek ketersediaan bus (versi untuk UPDATE - bisa exclude booking tertentu)
     */
    public boolean isBusAvailable(int idBus, Date tanggalMulai, Date tanggalSelesai, Integer excludeBookingId) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) as total FROM tbl_booking " +
            "WHERE id_bus = ? " +
            "AND status_booking IN ('pending', 'dikonfirmasi') " +
            "AND tanggal_mulai <= ? " +
            "AND tanggal_selesai >= ?"
        );

        if (excludeBookingId != null) {
            sql.append(" AND id_booking != ?");
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            java.sql.Date sqlTglMulai = new java.sql.Date(tanggalMulai.getTime());
            java.sql.Date sqlTglSelesai = new java.sql.Date(tanggalSelesai.getTime());

            stmt.setInt(1, idBus);
            stmt.setDate(2, sqlTglSelesai);
            stmt.setDate(3, sqlTglMulai);

            if (excludeBookingId != null) {
                stmt.setInt(4, excludeBookingId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mengecek ketersediaan bus (versi untuk CREATE - tidak exclude apa-apa)
     */
    public boolean isBusAvailable(int idBus, Date tanggalMulai, Date tanggalSelesai) {
        return isBusAvailable(idBus, tanggalMulai, tanggalSelesai, null);
    }
}