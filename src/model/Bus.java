package model;

public class Bus {
    private int idBus;
    private String noPolisi;
    private String tipeBus;
    private String merk;
    private int kapasitas;
    private String fasilitas;
    private double hargaPerHari;
    private String foto;
    private String status;
    
    // Constructor
    public Bus() {}
    
    public Bus(int idBus, String noPolisi, String tipeBus, int kapasitas, double hargaPerHari, String status) {
        this.idBus = idBus;
        this.noPolisi = noPolisi;
        this.tipeBus = tipeBus;
        this.kapasitas = kapasitas;
        this.hargaPerHari = hargaPerHari;
        this.status = status;
    }
    
    // Getters and Setters
    public int getIdBus() {
        return idBus;
    }
    
    public void setIdBus(int idBus) {
        this.idBus = idBus;
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
    
    public String getMerk() {
        return merk;
    }
    
    public void setMerk(String merk) {
        this.merk = merk;
    }
    
    public int getKapasitas() {
        return kapasitas;
    }
    
    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }
    
    public String getFasilitas() {
        return fasilitas;
    }
    
    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }
    
    public double getHargaPerHari() {
        return hargaPerHari;
    }
    
    public void setHargaPerHari(double hargaPerHari) {
        this.hargaPerHari = hargaPerHari;
    }
    
    public String getFoto() {
        return foto;
    }
    
    public void setFoto(String foto) {
        this.foto = foto;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}