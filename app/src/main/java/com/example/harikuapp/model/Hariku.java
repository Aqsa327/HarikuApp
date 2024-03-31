package com.example.harikuapp.model;

import com.google.firebase.Timestamp;

public class Hariku {

    private String judul;
    private String deskrips;
    private String imageUrl;
    private String penggunaID;
    private Timestamp timeAdded;
    private String pengguna;

    public Hariku() {
        // konstruktor kosong untuk firebase (Mandatory)
    }

    public Hariku(String judul, String deskrips, String imageUrl, String penggunaID, Timestamp timeAdded, String pengguna) {
        this.judul = judul;
        this.deskrips = deskrips;
        this.imageUrl = imageUrl;
        this.penggunaID = penggunaID;
        this.timeAdded = timeAdded;
        this.pengguna = pengguna;
    }

    // Getter
    public String getJudul() {
        return judul;
    }

    public String getDeskrips() {
        return deskrips;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPenggunaID() {
        return penggunaID;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public String getPengguna() {
        return pengguna;
    }

    // Setter
    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setDeskrips(String deskrips) {
        this.deskrips = deskrips;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPenggunaID(String penggunaID) {
        this.penggunaID = penggunaID;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public void setPengguna(String pengguna) {
        this.pengguna = pengguna;
    }
}
