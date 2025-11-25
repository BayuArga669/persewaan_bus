package service;

import dao.BookingDAO;
import model.Booking;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;
    
    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }
    
    /**
     * Update otomatis status booking:
     * 1. Pending -> Dibatalkan (jika lewat tanggal selesai)
     * 2. Dikonfirmasi -> Selesai (jika lewat tanggal selesai)
     * Dan otomatis update status bus menjadi tersedia
     * 
     * @return Total booking yang diupdate
     */
    public int autoUpdateBookingStatus() {
        int totalUpdated = 0;
        
        // Dapatkan list booking yang akan di-cancel
        List<Booking> pendingExpired = bookingDAO.getAllBooking();
        
        // Update pending ke dibatalkan
        int canceledCount = bookingDAO.autoUpdatePendingExpired();
        if (canceledCount > 0) {
            System.out.println("Auto-canceled " + canceledCount + " pending booking(s)");
            totalUpdated += canceledCount;
            
            // Update status bus untuk booking yang dibatalkan
            updateBusStatusForExpiredBookings();
        }
        
        // Update dikonfirmasi ke selesai
        int completedCount = bookingDAO.autoUpdateConfirmedToCompleted();
        if (completedCount > 0) {
            System.out.println("Auto-completed " + completedCount + " confirmed booking(s)");
            totalUpdated += completedCount;
            
            // Update status bus untuk booking yang selesai
            updateBusStatusForCompletedBookings();
        }
        
        return totalUpdated;
    }
    
    /**
     * Update status bus untuk booking yang expired/dibatalkan
     */
    private void updateBusStatusForExpiredBookings() {
        List<Booking> bookings = bookingDAO.getAllBooking();
        for (Booking booking : bookings) {
            if (booking.getStatusBooking().equals("dibatalkan")) {
                bookingDAO.updateBusStatusFromBooking(booking.getIdBus(), "dibatalkan");
            }
        }
    }
    
    /**
     * Update status bus untuk booking yang selesai
     */
    private void updateBusStatusForCompletedBookings() {
        List<Booking> bookings = bookingDAO.getAllBooking();
        for (Booking booking : bookings) {
            if (booking.getStatusBooking().equals("selesai")) {
                bookingDAO.updateBusStatusFromBooking(booking.getIdBus(), "selesai");
            }
        }
    }
    
    /**
     * Update otomatis status booking dan tampilkan notifikasi
     * 
     * @return Message hasil update
     */
    public String autoUpdateWithMessage() {
        int totalUpdated = autoUpdateBookingStatus();
        
        if (totalUpdated > 0) {
            return "Status booking berhasil diperbarui otomatis!\n" +
                   "Total: " + totalUpdated + " booking\n" +
                   "Status bus telah diperbarui ke 'tersedia'";
        }
        
        return null; // Tidak ada update
    }
}