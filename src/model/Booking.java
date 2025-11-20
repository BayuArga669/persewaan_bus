package model;

import java.util.Date;

public class Booking {
    private int idBooking;
    private String kodeBooking;
    private int idPelanggan;
    private int idBus;
    private int idKasir;
    private Date tanggalBooking;
    private Date tanggalMulai;
    private Date tanggalSelesai;
    private String tujuan;
    private int jumlahPenumpang;
    private int lamaSewa;
    private double totalHarga;
    private String statusBooking;
    private String catatan;
    
    // Additional fields for display
    private String namaPelanggan;
    private String noPolisi;
    private String namaKasir;
    
    // Constructor
    public Booking() {}
    
    // Getters and Setters
    public int getIdBooking() {
        return idBooking;
    }
    
    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }
    
    public String getKodeBooking() {
        return kodeBooking;
    }
    
    public void setKodeBooking(String kodeBooking) {
        this.kodeBooking = kodeBooking;
    }
    
    public int getIdPelanggan() {
        return idPelanggan;
    }
    
    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }
    
    public int getIdBus() {
        return idBus;
    }
    
    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }
    
    public int getIdKasir() {
        return idKasir;
    }
    
    public void setIdKasir(int idKasir) {
        this.idKasir = idKasir;
    }
    
    public Date getTanggalBooking() {
        return tanggalBooking;
    }
    
    public void setTanggalBooking(Date tanggalBooking) {
        this.tanggalBooking = tanggalBooking;
    }
    
    public Date getTanggalMulai() {
        return tanggalMulai;
    }
    
    public void setTanggalMulai(Date tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }
    
    public Date getTanggalSelesai() {
        return tanggalSelesai;
    }
    
    public void setTanggalSelesai(Date tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }
    
    public String getTujuan() {
        return tujuan;
    }
    
    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }
    
    public int getJumlahPenumpang() {
        return jumlahPenumpang;
    }
    
    public void setJumlahPenumpang(int jumlahPenumpang) {
        this.jumlahPenumpang = jumlahPenumpang;
    }
    
    public int getLamaSewa() {
        return lamaSewa;
    }
    
    public void setLamaSewa(int lamaSewa) {
        this.lamaSewa = lamaSewa;
    }
    
    public double getTotalHarga() {
        return totalHarga;
    }
    
    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }
    
    public String getStatusBooking() {
        return statusBooking;
    }
    
    public void setStatusBooking(String statusBooking) {
        this.statusBooking = statusBooking;
    }
    
    public String getCatatan() {
        return catatan;
    }
    
    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
    
    public String getNamaPelanggan() {
        return namaPelanggan;
    }
    
    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }
    
    public String getNoPolisi() {
        return noPolisi;
    }
    
    public void setNoPolisi(String noPolisi) {
        this.noPolisi = noPolisi;
    }
    
    public String getNamaKasir() {
        return namaKasir;
    }
    
    public void setNamaKasir(String namaKasir) {
        this.namaKasir = namaKasir;
    }
}