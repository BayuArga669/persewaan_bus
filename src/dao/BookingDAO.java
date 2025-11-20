package dao;

import config.DatabaseConfig;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    public boolean tambahBooking(Booking booking) {
        String sql = "INSERT INTO tbl_booking (kode_booking, id_pelanggan, id_bus, id_kasir, tanggal_mulai, tanggal_selesai, tujuan, jumlah_penumpang, lama_sewa, total_harga, status_booking, catatan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, booking.getKodeBooking());
            stmt.setInt(2, booking.getIdPelanggan());
            stmt.setInt(3, booking.getIdBus());
            stmt.setInt(4, booking.getIdKasir());
            stmt.setDate(5, new java.sql.Date(booking.getTanggalMulai().getTime()));
            stmt.setDate(6, new java.sql.Date(booking.getTanggalSelesai().getTime()));
            stmt.setString(7, booking.getTujuan());
            stmt.setInt(8, booking.getJumlahPenumpang());
            stmt.setInt(9, booking.getLamaSewa());
            stmt.setDouble(10, booking.getTotalHarga());
            stmt.setString(11, booking.getStatusBooking());
            stmt.setString(12, booking.getCatatan());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    booking.setIdBooking(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE tbl_booking SET tanggal_mulai = ?, tanggal_selesai = ?, tujuan = ?, jumlah_penumpang = ?, lama_sewa = ?, total_harga = ?, status_booking = ?, catatan = ? WHERE id_booking = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(booking.getTanggalMulai().getTime()));
            stmt.setDate(2, new java.sql.Date(booking.getTanggalSelesai().getTime()));
            stmt.setString(3, booking.getTujuan());
            stmt.setInt(4, booking.getJumlahPenumpang());
            stmt.setInt(5, booking.getLamaSewa());
            stmt.setDouble(6, booking.getTotalHarga());
            stmt.setString(7, booking.getStatusBooking());
            stmt.setString(8, booking.getCatatan());
            stmt.setInt(9, booking.getIdBooking());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStatusBooking(int idBooking, String status) {
        String sql = "UPDATE tbl_booking SET status_booking = ? WHERE id_booking = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, idBooking);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Booking> getAllBooking() {
        List<Booking> bookingList = new ArrayList<>();
        String sql = "SELECT b.*, p.nama_pelanggan, bus.no_polisi, u.nama_lengkap as nama_kasir " +
                     "FROM tbl_booking b " +
                     "JOIN tbl_pelanggan p ON b.id_pelanggan = p.id_pelanggan " +
                     "JOIN tbl_bus bus ON b.id_bus = bus.id_bus " +
                     "JOIN tbl_users u ON b.id_kasir = u.id_user " +
                     "ORDER BY b.tanggal_booking DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingList;
    }
    
    public Booking getBookingById(int idBooking) {
        String sql = "SELECT b.*, p.nama_pelanggan, bus.no_polisi, u.nama_lengkap as nama_kasir " +
                     "FROM tbl_booking b " +
                     "JOIN tbl_pelanggan p ON b.id_pelanggan = p.id_pelanggan " +
                     "JOIN tbl_bus bus ON b.id_bus = bus.id_bus " +
                     "JOIN tbl_users u ON b.id_kasir = u.id_user " +
                     "WHERE b.id_booking = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idBooking);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String generateKodeBooking() {
        String sql = "SELECT kode_booking FROM tbl_booking ORDER BY id_booking DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastKode = rs.getString("kode_booking");
                int number = Integer.parseInt(lastKode.substring(3)) + 1;
                return String.format("BKG%05d", number);
            } else {
                return "BKG00001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "BKG00001";
        }
    }
    
    public List<Booking> getBookingByKasir(int idKasir) {
        List<Booking> bookingList = new ArrayList<>();
        String sql = "SELECT b.*, p.nama_pelanggan, bus.no_polisi, u.nama_lengkap as nama_kasir " +
                     "FROM tbl_booking b " +
                     "JOIN tbl_pelanggan p ON b.id_pelanggan = p.id_pelanggan " +
                     "JOIN tbl_bus bus ON b.id_bus = bus.id_bus " +
                     "JOIN tbl_users u ON b.id_kasir = u.id_user " +
                     "WHERE b.id_kasir = ? " +
                     "ORDER BY b.tanggal_booking DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKasir);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingList;
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setIdBooking(rs.getInt("id_booking"));
        booking.setKodeBooking(rs.getString("kode_booking"));
        booking.setIdPelanggan(rs.getInt("id_pelanggan"));
        booking.setIdBus(rs.getInt("id_bus"));
        booking.setIdKasir(rs.getInt("id_kasir"));
        booking.setTanggalBooking(rs.getTimestamp("tanggal_booking"));
        booking.setTanggalMulai(rs.getDate("tanggal_mulai"));
        booking.setTanggalSelesai(rs.getDate("tanggal_selesai"));
        booking.setTujuan(rs.getString("tujuan"));
        booking.setJumlahPenumpang(rs.getInt("jumlah_penumpang"));
        booking.setLamaSewa(rs.getInt("lama_sewa"));
        booking.setTotalHarga(rs.getDouble("total_harga"));
        booking.setStatusBooking(rs.getString("status_booking"));
        booking.setCatatan(rs.getString("catatan"));
        booking.setNamaPelanggan(rs.getString("nama_pelanggan"));
        booking.setNoPolisi(rs.getString("no_polisi"));
        booking.setNamaKasir(rs.getString("nama_kasir"));
        return booking;
    }
}