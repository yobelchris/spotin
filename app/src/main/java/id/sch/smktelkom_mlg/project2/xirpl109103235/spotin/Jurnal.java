package id.sch.smktelkom_mlg.project2.xirpl109103235.spotin;

import com.cloudant.sync.datastore.DocumentRevision;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhe on 24/04/2017.
 */

public class Jurnal {

    // Doc type diperlukan untuk mengidentifikasi dan mengelompokkan tipe kolektif di datastore.
    private static final String DOC_TYPE = "com.xirpl109103235.spotin";
    // Ini adalah nomor revisi dalam database yang mewakili tugas ini.
    private DocumentRevision rev;
    private String type = DOC_TYPE;
    // Variabel untuk tanda centang selesai.
    private boolean completed;
    // Teks tugas utama
    private String description;

    private Jurnal() {
    }

    Jurnal(String desc) {
        this.setDescription(desc);
        this.setCompleted(false);
        this.setType(DOC_TYPE);
    }

    // Buat objek tugas berdasarkan revisi doc dari datastore.
    static Jurnal fromRevision(DocumentRevision rev) {
        Jurnal t = new Jurnal();
        t.rev = rev;

        Map<String, Object> map = rev.asMap();
        if (map.containsKey("type") && map.get("type").equals(Jurnal.DOC_TYPE)) {
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
