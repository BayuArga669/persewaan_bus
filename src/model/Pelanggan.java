package model;

public class Pelanggan {
    private int idPelanggan;
    private String namaPelanggan;
    private String noTelp;
    private String email;
    private String alamat;
    private String noKtp;
    
    // Constructor
    public Pelanggan() {}
    
    public Pelanggan(int idPelanggan, String namaPelanggan, String noTelp, String email) {
        this.idPelanggan = idPelanggan;
        this.namaPelanggan = namaPelanggan;
        this.noTelp = noTelp;
        this.email = email;
    }
    
    // Getters and Setters
    public int getIdPelanggan() {
        return idPelanggan;
    }
    
    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }
    
    public String getNamaPelanggan() {
        return namaPelanggan;
    }
    
    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }
    
    public String getNoTelp() {
        return noTelp;
    }
    
    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAlamat() {
        return alamat;
    }
    
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    
    public String getNoKtp() {
        return noKtp;
    }
    
    public void setNoKtp(String noKtp) {
        this.noKtp = noKtp;
    }
    
    @Override
    public String toString() {
        return namaPelanggan + " - " + noTelp;
    }
}