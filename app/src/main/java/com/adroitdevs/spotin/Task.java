package com.adroitdevs.spotin;

import com.cloudant.sync.datastore.DocumentRevision;

import java.util.HashMap;
import java.util.Map;

/*
 * Objek mewakili sebuah task.
 *
 * Serta bertindak sebagai objek nilai, kelas ini juga memiliki referensi ke DocumentRevision asli,
 * yang akan berlaku jika Tugas diambil dari database,
 * atau null (misalnya untuk Tasks yang telah dibuat namun belum disimpan ke Database).
 *
 * fromRevision() dan asMap() bertindak sebagai helpers untuk memetakan dan dari JSON - dalam sebuah aplikasi nyata,
 * sesuatu yang lebih kompleks seperti mapper objek dapat digunakan.
 */

class Task {

    // Doc type diperlukan untuk mengidentifikasi dan mengelompokkan tipe kolektif di datastore.
    private static final String DOC_TYPE = "com.adroitdevs.spotin";
    // Ini adalah nomor revisi dalam database yang mewakili tugas ini.
    private DocumentRevision rev;
    private String type = DOC_TYPE;
    // Variabel untuk tanda centang selesai.
    private boolean completed;
    // Teks tugas utama
    private String id;
    private String judul;
    private String lokasi;
    private String harga;
    private String gambar;
    private String deskripsi;
    private String telepon;
    private String koordinat;
    private String lokKota;
    private String tipe;
    private Task() {
    }

    Task(String desc) {
        this.setJudul(desc);
        this.setCompleted(false);
        this.setType(DOC_TYPE);
    }

    // Buat objek tugas berdasarkan revisi doc dari datastore.
    static Task fromRevision(DocumentRevision rev) {
        Task t = new Task();
        t.rev = rev;

        Map<String, Object> map = rev.asMap();
        if (/*map.containsKey("type") && map.get("type").equals(Task.DOC_TYPE)*/true) {
            /*t.setType((String) map.get("type"));
            t.setCompleted((Boolean) map.get("completed"));*/
            t.setJudul((String) map.get("nama"));
            t.setHarga((String) map.get("tarif"));
            t.setLokasi((String) map.get("lokasi"));
            t.setGambar((String) map.get("gambar"));
            t.setDeskripsi((String) map.get("deskripsi"));
            t.setKoordinat((String) map.get("koordinat"));
            t.setLokKota((String) map.get("kota"));
            t.setTelepon((String) map.get("cp"));
            t.setTipe((String) map.get("tipe"));
            t.setId((String) map.get("_id"));
            return t;
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    DocumentRevision getDocumentRevision() {
        return rev;
    }

    private void setType(String type) {
        this.type = type;
    }

    boolean isCompleted() {
        return this.completed;
    }

    void setCompleted(boolean completed) {
        this.completed = completed;
    }

    String getJudul() {
        return this.judul;
    }

    void setJudul(String desc) {
        this.judul = desc;
    }

    @Override
    public String toString() {
        return "{ nama: " + getJudul() + ", lokasi: " + getLokasi() + ", tarif: " + getHarga() + "}";
    }

    // Return tugas sebagai Hash Map agar mudah dikonsumsi oleh replicators dan datastores.
    Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("completed", completed);
        map.put("judul", judul);
        return map;
    }

    public String getLokKota() {
        return lokKota;
    }

    public void setLokKota(String lokKota) {
        this.lokKota = lokKota;
    }
}