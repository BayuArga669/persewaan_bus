package model;

import java.util.Date;

public class AssignmentSopir {
    private int idAssignment;
    private int idBooking;
    private int idSopir;
    private double feeSopir;
    private String statusBayar;
    private Date tanggalBayar;
    private String keterangan;
    
    // Additional fields for display
    private String kodeBooking;
    private String namaSopir;
    private String namaPelanggan;
    private String noPolisi;
    private String tujuan;
    private Date tanggalMulai;
    private Date tanggalSelesai;
    private String statusBooking;
    
    // Constructor
    public AssignmentSopir() {}
    
    // Getters and Setters
    public int getIdAssignment() {
        return idAssignment;
    }
    
    public void setIdAssignment(int idAssignment) {
        this.idAssignment = idAssignment;
    }
    
    public int getIdBooking() {
        return idBooking;
    }
    
    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }
    
    public int getIdSopir() {
        return idSopir;
    }
    
    public void setIdSopir(int idSopir) {
        this.idSopir = idSopir;
    }
    
    public double getFeeSopir() {
        return feeSopir;
    }
    
    public void setFeeSopir(double feeSopir) {
        this.feeSopir = feeSopir;
    }
    
    public String getStatusBayar() {
        return statusBayar;
    }
    
    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }
    
    public Date getTanggalBayar() {
        return tanggalBayar;
    }
    
    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
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
    
    public String getNamaSopir() {
        return namaSopir;
    }
    
    public void setNamaSopir(String namaSopir) {
        this.namaSopir = namaSopir;
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
    
    public String getTujuan() {
        return tujuan;
    }
    
    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
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
    
    public String getStatusBooking() {
        return statusBooking;
    }
    
    public void setStatusBooking(String statusBooking) {
        this.statusBooking = statusBooking;
    }
}