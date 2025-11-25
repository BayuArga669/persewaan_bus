package model;

import java.util.Date;

public class Pembayaran {
    private int idPembayaran;
    private int idBooking;
    private Date tanggalBayar;
    private double jumlahBayar;
    private String metodeBayar;
    private String buktiTransfer;
    private String statusBayar;
    private String keterangan;
    
    // Additional fields for display
    private String kodeBooking;
    private String namaPelanggan;
    private double totalHarga;
    private double sisaPembayaran;
    
    // Constructor
    public Pembayaran() {}
    
    // Getters and Setters
    public int getIdPembayaran() {
        return idPembayaran;
    }
    
    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }
    
    public int getIdBooking() {
        return idBooking;
    }
    
    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }
    
    public Date getTanggalBayar() {
        return tanggalBayar;
    }
    
    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }
    
    public double getJumlahBayar() {
        return jumlahBayar;
    }
    
    public void setJumlahBayar(double jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
    }
    
    public String getMetodeBayar() {
        return metodeBayar;
    }
    
    public void setMetodeBayar(String metodeBayar) {
        this.metodeBayar = metodeBayar;
    }
    
    public String getBuktiTransfer() {
        return buktiTransfer;
    }
    
    public void setBuktiTransfer(String buktiTransfer) {
        this.buktiTransfer = buktiTransfer;
    }
    
    public String getStatusBayar() {
        return statusBayar;
    }
    
    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }
    
    public String getKeterangan() {
        return keterangan;
    }
    
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    public String getKodeBooking() {
        return kodeBooking;
    }
    
    public void setKodeBooking(String kodeBooking) {
        this.kodeBooking = kodeBooking;
    }
    
    public String getNamaPelanggan() {
        return namaPelanggan;
    }
    
    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }
    
    public double getTotalHarga() {
        return totalHarga;
    }
    
    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }
    
    public double getSisaPembayaran() {
        return sisaPembayaran;
    }
    
    public void setSisaPembayaran(double sisaPembayaran) {
        this.sisaPembayaran = sisaPembayaran;
    }
}