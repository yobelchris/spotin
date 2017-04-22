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
    private String description;

    private Task() {
    }

    Task(String desc) {
        this.setDescription(desc);
        this.setCompleted(false);
        this.setType(DOC_TYPE);
    }

    // Buat objek tugas berdasarkan revisi doc dari datastore.
    static Task fromRevision(DocumentRevision rev) {
        Task t = new Task();
        t.rev = rev;

        Map<String, Object> map = rev.asMap();
        if (map.containsKey("type") && map.get("type").equals(Task.DOC_TYPE)) {
            t.setType((String) map.get("type"));
            t.setCompleted((Boolean) map.get("completed"));
            t.setDescription((String) map.get("description"));
            return t;
        }
        return null;
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

    String getDescription() {
        return this.description;
    }

    void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public String toString() {
        return "{ desc: " + getDescription() + ", completed: " + isCompleted() + "}";
    }

    // Return tugas sebagai Hash Map agar mudah dikonsumsi oleh replicators dan datastores.
    Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("completed", completed);
        map.put("description", description);
        return map;
    }

}