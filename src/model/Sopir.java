package model;

import java.util.Date;

public class Sopir {
    private int idSopir;
    private int idUser;
    private String noSim;
    private String jenisSim;
    private Date masaBerlakuSim;
    private String statusSopir;
    
    // Additional fields for display
    private String namaSopir;
    private String username;
    private String noTelp;
    
    // Constructor
    public Sopir() {}
    
    public Sopir(int idSopir, int idUser, String namaSopir, String noSim, String statusSopir) {
        this.idSopir = idSopir;
        this.idUser = idUser;
        this.namaSopir = namaSopir;
        this.noSim = noSim;
        this.statusSopir = statusSopir;
    }
    
    // Getters and Setters
    public int getIdSopir() {
        return idSopir;
    }
    
    public void setIdSopir(int idSopir) {
        this.idSopir = idSopir;
    }
    
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public String getNoSim() {
        return noSim;
    }
    
    public void setNoSim(String noSim) {
        this.noSim = noSim;
    }
    
    public String getJenisSim() {
        return jenisSim;
    }
    
    public void setJenisSim(String jenisSim) {
        this.jenisSim = jenisSim;
    }
    
    public Date getMasaBerlakuSim() {
        return masaBerlakuSim;
    }
    
    public void setMasaBerlakuSim(Date masaBerlakuSim) {
        this.masaBerlakuSim = masaBerlakuSim;
    }
    
    public String getStatusSopir() {
        return statusSopir;
    }
    
    public void setStatusSopir(String statusSopir) {
        this.statusSopir = statusSopir;
    }
    
    public String getNamaSopir() {
        return namaSopir;
    }
    
    public void setNamaSopir(String namaSopir) {
        this.namaSopir = namaSopir;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNoTelp() {
        return noTelp;
    }
    
    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
    
    @Override
    public String toString() {
        return namaSopir + " - " + noSim;
    }
}