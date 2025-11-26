package dao;

import config.DatabaseConfig;
import model.Bus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "SELECT * FROM tbl_bus WHERE status = 'tersedia' ORDER BY tipe_bus, kapasitas";
        
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

}