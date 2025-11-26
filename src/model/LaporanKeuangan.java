package model;

public class LaporanKeuangan {
    // Pendapatan
    private double totalPendapatan;
    private double pendapatanCash;
    private double pendapatanTransfer;
    private double pendapatanEwallet;
    
    // Pengeluaran
    private double totalBiayaSopir;
    private double biayaSopirBelumBayar;
    private double biayaSopirSudahBayar;
    
    // Keuntungan
    private double keuntunganKotor;
    private double keuntunganBersih;
    
    // Statistik
    private int totalBooking;
    private int bookingSelesai;
    private int bookingDikonfirmasi;
    private int bookingDibatalkan;
    
    // Constructor
    public LaporanKeuangan() {}
    
    // Getters and Setters
    public double getTotalPendapatan() {
        return totalPendapatan;
    }
    
    public void setTotalPendapatan(double totalPendapatan) {
        this.totalPendapatan = totalPendapatan;
    }
    
    public double getPendapatanCash() {
        return pendapatanCash;
    }
    
    public void setPendapatanCash(double pendapatanCash) {
        this.pendapatanCash = pendapatanCash;
    }
    
    public double getPendapatanTransfer() {
        return pendapatanTransfer;
    }
    
    public void setPendapatanTransfer(double pendapatanTransfer) {
        this.pendapatanTransfer = pendapatanTransfer;
    }
    
    public double getPendapatanEwallet() {
        return pendapatanEwallet;
    }
    
    public void setPendapatanEwallet(double pendapatanEwallet) {
        this.pendapatanEwallet = pendapatanEwallet;
    }
    
    public double getTotalBiayaSopir() {
        return totalBiayaSopir;
    }
    
    public void setTotalBiayaSopir(double totalBiayaSopir) {
        this.totalBiayaSopir = totalBiayaSopir;
    }
    
    public double getBiayaSopirBelumBayar() {
        return biayaSopirBelumBayar;
    }
    
    public void setBiayaSopirBelumBayar(double biayaSopirBelumBayar) {
        this.biayaSopirBelumBayar = biayaSopirBelumBayar;
    }
    
    public double getBiayaSopirSudahBayar() {
        return biayaSopirSudahBayar;
    }
    
    public void setBiayaSopirSudahBayar(double biayaSopirSudahBayar) {
        this.biayaSopirSudahBayar = biayaSopirSudahBayar;
    }
    
    public double getKeuntunganKotor() {
        return keuntunganKotor;
    }
    
    public void setKeuntunganKotor(double keuntunganKotor) {
        this.keuntunganKotor = keuntunganKotor;
    }
    
    public double getKeuntunganBersih() {
        return keuntunganBersih;
    }
    
    public void setKeuntunganBersih(double keuntunganBersih) {
        this.keuntunganBersih = keuntunganBersih;
    }
    
    public int getTotalBooking() {
        return totalBooking;
    }
    
    public void setTotalBooking(int totalBooking) {
        this.totalBooking = totalBooking;
    }
    
    public int getBookingSelesai() {
        return bookingSelesai;
    }
    
    public void setBookingSelesai(int bookingSelesai) {
        this.bookingSelesai = bookingSelesai;
    }
    
    public int getBookingDikonfirmasi() {
        return bookingDikonfirmasi;
    }
    
    public void setBookingDikonfirmasi(int bookingDikonfirmasi) {
        this.bookingDikonfirmasi = bookingDikonfirmasi;
    }
    
    public int getBookingDibatalkan() {
        return bookingDibatalkan;
    }
    
    public void setBookingDibatalkan(int bookingDibatalkan) {
        this.bookingDibatalkan = bookingDibatalkan;
    }
}