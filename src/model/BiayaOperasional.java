package model;

import java.util.Date;

public class BiayaOperasional {
    private int idBiaya;
    private int idBooking;
    private String kodeBooking;
    private Date tanggalBiaya;
    private String jenisBiaya;
    private String keterangan;
    private double jumlah;
    private String bukti;
    private String statusBayar;
    private Date tanggalBayar;
    private int createdBy;
    private String createdByName;
    private Date createdAt;
    private Date updatedAt;
    
    // Additional fields from view
    private String namaPelanggan;
    private String noPolisi;
    private String tipeBus;
    private Date tanggalMulai;
    private Date tanggalSelesai;
    private double totalHarga;
    
    public BiayaOperasional() {
    }
    
    public BiayaOperasional(int idBooking, String jenisBiaya, String keterangan, 
                           double jumlah, String statusBayar, int createdBy) {
        this.idBooking = idBooking;
        this.jenisBiaya = jenisBiaya;
        this.keterangan = keterangan;
        this.jumlah = jumlah;
        this.statusBayar = statusBayar;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public int getIdBiaya() {
        return idBiaya;
    }
    
    public void setIdBiaya(int idBiaya) {
        this.idBiaya = idBiaya;
    }
    
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
    
    public Date getTanggalBiaya() {
        return tanggalBiaya;
    }
    
    public void setTanggalBiaya(Date tanggalBiaya) {
        this.tanggalBiaya = tanggalBiaya;
    }
    
    public String getJenisBiaya() {
        return jenisBiaya;
    }
    
    public void setJenisBiaya(String jenisBiaya) {
        this.jenisBiaya = jenisBiaya;
    }
    
    public String getKeterangan() {
        return keterangan;
    }
    
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    public double getJumlah() {
        return jumlah;
    }
    
    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }
    
    public String getBukti() {
        return bukti;
    }
    
    public void setBukti(String bukti) {
        this.bukti = bukti;
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
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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
    
    public String getTipeBus() {
        return tipeBus;
    }
    
    public void setTipeBus(String tipeBus) {
        this.tipeBus = tipeBus;
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
    
    public double getTotalHarga() {
        return totalHarga;
    }
    
    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }
    
    public String getJenisBiayaLabel() {
        switch(jenisBiaya) {
            case "gaji_sopir": return "Gaji Sopir";
            case "bbm": return "BBM";
            case "tol": return "Tol";
            case "parkir": return "Parkir";
            case "makan_sopir": return "Makan Sopir";
            case "maintenance": return "Maintenance";
            case "lainnya": return "Lainnya";
            default: return jenisBiaya;
        }
    }
}